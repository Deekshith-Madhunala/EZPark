package io.park.e_z_park.service;

import io.park.e_z_park.entity.*;
import io.park.e_z_park.model.ParkingLotDTO;
import io.park.e_z_park.model.SlotStatusResponseDTO;
import io.park.e_z_park.repository.LocationRepository;
import io.park.e_z_park.repository.LotAnalyticRepository;
import io.park.e_z_park.repository.ParkingLotRepository;
import io.park.e_z_park.repository.ReservationRepository;
import io.park.e_z_park.repository.UserRepository;
import io.park.e_z_park.util.NotFoundException;
import io.park.e_z_park.util.ReferencedWarning;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final LotAnalyticRepository lotAnalyticRepository;

    public ParkingLotService(final ParkingLotRepository parkingLotRepository,
            final LocationRepository locationRepository, final UserRepository userRepository,
            final ReservationRepository reservationRepository,
            final LotAnalyticRepository lotAnalyticRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.lotAnalyticRepository = lotAnalyticRepository;
    }

    public List<ParkingLotDTO> findAll() {
        final List<ParkingLot> parkingLots = parkingLotRepository.findAll(Sort.by("id"));
        return parkingLots.stream()
                .map(parkingLot -> mapToDTO(parkingLot, new ParkingLotDTO()))
                .toList();
    }

    public ParkingLotDTO get(final String id) {
        return parkingLotRepository.findById(id)
                .map(parkingLot -> mapToDTO(parkingLot, new ParkingLotDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final ParkingLotDTO parkingLotDTO) {
        final ParkingLot parkingLot = new ParkingLot();
        mapToEntity(parkingLotDTO, parkingLot);
        parkingLot.setId(parkingLotDTO.getId());
        generateParkingSlots(parkingLot);
        return parkingLotRepository.save(parkingLot).getId();
    }

    private void generateParkingSlots(ParkingLot parkingLot) {
        int totalSpots = parkingLot.getTotalSpots();
        List<ParkingSlot> slots = new ArrayList<>();

        // Instead of using abbreviation, just use a simple number for the slot
        for (int i = 1; i <= totalSpots; i++) {
            ParkingSlot slot = new ParkingSlot();
            String slotNumber = String.format("%d", i); // Simple number format

            slot.setSlotId(slotNumber);
            slot.setOccupied(false);
            slots.add(slot);
        }
        parkingLot.setAvailableSpots(slots.size());
        parkingLot.setSlots(slots);
    }

    public void update(final String id, final ParkingLotDTO parkingLotDTO) {
        final ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(parkingLotDTO, parkingLot);
        parkingLotRepository.save(parkingLot);
    }

    public void delete(final String id) {
        parkingLotRepository.deleteById(id);
    }

    private ParkingLotDTO mapToDTO(final ParkingLot parkingLot, final ParkingLotDTO parkingLotDTO) {
        parkingLotDTO.setId(parkingLot.getId());
        parkingLotDTO.setName(parkingLot.getName());
        parkingLotDTO.setTotalSpots(parkingLot.getTotalSpots());
        parkingLotDTO.setAvailableSpots(parkingLot.getSlots().size());
        parkingLotDTO.setPricePerHour(parkingLot.getPricePerHour());
        parkingLotDTO.setType(parkingLot.getType());
        parkingLotDTO.setOpeningTime(parkingLot.getOpeningTime());
        parkingLotDTO.setClosingTime(parkingLot.getClosingTime());
        parkingLotDTO.setCreatedAt(parkingLot.getCreatedAt());
        parkingLotDTO.setSlots(parkingLot.getSlots());
        parkingLotDTO.setLocation(parkingLot.getLocation() == null ? null : parkingLot.getLocation().getId());
        parkingLotDTO.setCreatedBy(parkingLot.getCreatedBy() == null ? null : parkingLot.getCreatedBy().getId());
        return parkingLotDTO;
    }

    private ParkingLot mapToEntity(final ParkingLotDTO parkingLotDTO, final ParkingLot parkingLot) {
        parkingLot.setName(parkingLotDTO.getName());
        parkingLot.setTotalSpots(parkingLotDTO.getTotalSpots());
        parkingLot.setAvailableSpots(parkingLotDTO.getSlots().size());
        parkingLot.setPricePerHour(parkingLotDTO.getPricePerHour());
        parkingLot.setType(parkingLotDTO.getType());
        parkingLot.setOpeningTime(parkingLotDTO.getOpeningTime());
        parkingLot.setClosingTime(parkingLotDTO.getClosingTime());
        parkingLot.setCreatedAt(parkingLotDTO.getCreatedAt());
        parkingLot.setSlots(parkingLotDTO.getSlots());
        final Location location = parkingLotDTO.getLocation() == null ? null : locationRepository.findById(parkingLotDTO.getLocation())
                .orElseThrow(() -> new NotFoundException("location not found"));
        parkingLot.setLocation(location);
        final User createdBy = parkingLotDTO.getCreatedBy() == null ? null : userRepository.findById(parkingLotDTO.getCreatedBy())
                .orElseThrow(() -> new NotFoundException("createdBy not found"));
        parkingLot.setCreatedBy(createdBy);
        return parkingLot;
    }

    public boolean idExists(final String id) {
        return parkingLotRepository.existsByIdIgnoreCase(id);
    }

    public ReferencedWarning getReferencedWarning(final String id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Reservation parkingLotReservation = reservationRepository.findFirstByParkingLot(parkingLot);
        if (parkingLotReservation != null) {
            referencedWarning.setKey("parkingLot.reservation.parkingLot.referenced");
            referencedWarning.addParam(parkingLotReservation.getId());
            return referencedWarning;
        }
        final LotAnalytic parkingLotLotAnalytic = lotAnalyticRepository.findFirstByParkingLot(parkingLot);
        if (parkingLotLotAnalytic != null) {
            referencedWarning.setKey("parkingLot.lotAnalytic.parkingLot.referenced");
            referencedWarning.addParam(parkingLotLotAnalytic.getId());
            return referencedWarning;
        }
        return null;
    }

    public List<ParkingLotDTO> findByLocationId(String locationId) {
        return parkingLotRepository.findByLocationId(locationId);
    }

    public SlotStatusResponseDTO getSlotStatus(String parkingLotId, String slotId) {
        ParkingLot lot = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ParkingLot not found"));

        ParkingSlot slot = lot.getSlots().stream()
                .filter(s -> s.getSlotId().equals(slotId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found in lot"));

        Location location = lot.getLocation();

        SlotStatusResponseDTO dto = new SlotStatusResponseDTO();
        dto.setSlotId(slot.getSlotId());
        dto.setOccupied(slot.isOccupied());
        dto.setParkingLotId(lot.getId());
        dto.setParkingLotName(lot.getName());
        dto.setPricePerHour(lot.getPricePerHour());
        dto.setAvailableSpots(lot.getAvailableSpots());
        dto.setTotalSpots(lot.getTotalSpots());

        // Set location data
        if (location != null) {
            dto.setStreet(location.getStreet());
            dto.setCity(location.getCity());
            dto.setState(location.getState());
            dto.setZipCode(location.getZipCode());
            dto.setCountry(location.getCountry());
            dto.setLatitude(location.getLatitude());
            dto.setLongitude(location.getLongitude());
        }

        return dto;
    }

    public ParkingLotDTO getByName(String name) {
        ParkingLot parkingLot = parkingLotRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Parking lot with name " + name + " not found."));

        return mapToDTO(parkingLot, new ParkingLotDTO());
    }


}
