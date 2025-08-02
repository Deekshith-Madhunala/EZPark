package io.park.e_z_park.service;

import io.park.e_z_park.entity.Reservation;
import io.park.e_z_park.entity.User;
import io.park.e_z_park.entity.Vehicle;
import io.park.e_z_park.model.VehicleDTO;
import io.park.e_z_park.repository.ReservationRepository;
import io.park.e_z_park.repository.UserRepository;
import io.park.e_z_park.repository.VehicleRepository;
import io.park.e_z_park.util.NotFoundException;
import io.park.e_z_park.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public VehicleService(final VehicleRepository vehicleRepository,
            final UserRepository userRepository,
            final ReservationRepository reservationRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<VehicleDTO> findAll() {
        final List<Vehicle> vehicles = vehicleRepository.findAll(Sort.by("id"));
        return vehicles.stream()
                .map(vehicle -> mapToDTO(vehicle, new VehicleDTO()))
                .toList();
    }

    public VehicleDTO get(final String id) {
        return vehicleRepository.findById(id)
                .map(vehicle -> mapToDTO(vehicle, new VehicleDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final VehicleDTO vehicleDTO) {
        final Vehicle vehicle = new Vehicle();
        mapToEntity(vehicleDTO, vehicle);
        vehicle.setId(vehicleDTO.getId());
        return vehicleRepository.save(vehicle).getId();
    }

    public void update(final String id, final VehicleDTO vehicleDTO) {
        final Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(vehicleDTO, vehicle);
        vehicleRepository.save(vehicle);
    }

    public void delete(final String id) {
        vehicleRepository.deleteById(id);
    }

    private VehicleDTO mapToDTO(final Vehicle vehicle, final VehicleDTO vehicleDTO) {
        vehicleDTO.setId(vehicle.getId());
        vehicleDTO.setLicensePlate(vehicle.getLicensePlate());
        vehicleDTO.setVehicleType(vehicle.getVehicleType());
        vehicleDTO.setMake(vehicle.getMake());
        vehicleDTO.setModel(vehicle.getModel());
        vehicleDTO.setColor(vehicle.getColor());
        vehicleDTO.setRegisteredAt(vehicle.getRegisteredAt());
//        vehicleDTO.setUser(vehicle.getUser() == null ? null : vehicle.getUser().getId());
        return vehicleDTO;
    }

    private Vehicle mapToEntity(final VehicleDTO vehicleDTO, final Vehicle vehicle) {
        vehicle.setLicensePlate(vehicleDTO.getLicensePlate());
        vehicle.setVehicleType(vehicleDTO.getVehicleType());
        vehicle.setMake(vehicleDTO.getMake());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setColor(vehicleDTO.getColor());
        vehicle.setRegisteredAt(vehicleDTO.getRegisteredAt());
//        final User user = vehicleDTO.getUser() == null ? null : userRepository.findById(vehicleDTO.getUser())
//                .orElseThrow(() -> new NotFoundException("user not found"));
//        vehicle.setUser(user);
        return vehicle;
    }

    public boolean idExists(final String id) {
        return vehicleRepository.existsByIdIgnoreCase(id);
    }

    public ReferencedWarning getReferencedWarning(final String id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
//        final Reservation vehicleReservation = reservationRepository.findFirstByVehicle(vehicle);
//        if (vehicleReservation != null) {
//            referencedWarning.setKey("vehicle.reservation.vehicle.referenced");
//            referencedWarning.addParam(vehicleReservation.getId());
//            return referencedWarning;
//        }
        return null;
    }

}
