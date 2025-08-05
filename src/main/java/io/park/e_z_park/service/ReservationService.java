package io.park.e_z_park.service;

import io.park.e_z_park.entity.*;
import io.park.e_z_park.model.ReservationDTO;
import io.park.e_z_park.repository.ParkingLotRepository;
import io.park.e_z_park.repository.PaymentRepository;
import io.park.e_z_park.repository.ReservationRepository;
import io.park.e_z_park.repository.UserRepository;
import io.park.e_z_park.repository.VehicleRepository;
import io.park.e_z_park.util.NotFoundException;
import io.park.e_z_park.util.ReferencedWarning;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    public ReservationService(final ReservationRepository reservationRepository,
                              final UserRepository userRepository, final VehicleRepository vehicleRepository,
                              final ParkingLotRepository parkingLotRepository,
                              final PaymentRepository paymentRepository, EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.paymentRepository = paymentRepository;
        this.emailService = emailService;
    }

    public List<ReservationDTO> findAll() {
        final List<Reservation> reservations = reservationRepository.findAll(Sort.by("id"));
        return reservations.stream()
                .map(reservation -> mapToDTO(reservation, new ReservationDTO()))
                .toList();
    }

    public ReservationDTO get(final String id) {
        return reservationRepository.findById(id)
                .map(reservation -> mapToDTO(reservation, new ReservationDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final ReservationDTO reservationDTO) {
        final Reservation reservation = new Reservation();

        log.info("reservationDTO: " + reservationDTO.getParkingLot());

        // Retrieve the parking lot based on the parkingLot ID
        ParkingLot parkingLotDTO = parkingLotRepository.findById(reservationDTO.getParkingLot()).orElseThrow(
                () -> new RuntimeException("ParkingLot not found!")
        );

        log.info("received date ="+ reservationDTO.getStartTime());

        // Check if it's a new day to reset available spots (based on current date)
        LocalDate currentDateUtc = Instant.now().atZone(ZoneOffset.UTC).toLocalDate();

// Convert reservation start time to UTC date
        LocalDate reservationDateUtc = reservationDTO.getStartTime()
                .withOffsetSameInstant(ZoneOffset.UTC)  // normalize offset to UTC instant
                .toLocalDate();

        log.info("reservationDateUtc: " + reservationDateUtc);
        log.info("currentDateUtc: " + currentDateUtc);

        if (!currentDateUtc.isEqual(reservationDateUtc)) {
            if (currentDateUtc.isBefore(reservationDateUtc)) {
                log.info("Resetting parking lot for tomorrow or future date: " + reservationDateUtc);

                parkingLotDTO.setAvailableSpots(parkingLotDTO.getTotalSpots());

                for (ParkingSlot slot : parkingLotDTO.getSlots()) {
                    slot.setOccupied(false);
                }

                parkingLotRepository.save(parkingLotDTO);
            }
        }
        // Find an available slot to book
        ParkingSlot bookedSlot = null;
        for (ParkingSlot slot : parkingLotDTO.getSlots()) {
            if (!slot.isOccupied()) {  // If the slot is not occupied, book it
                slot.setOccupied(true); // Mark the slot as occupied
                bookedSlot = slot;
                break; // Exit once a slot is booked
            }
        }

        if (bookedSlot == null) {
            throw new RuntimeException("No available slots in this parking lot.");
        }

        // Update the available spots in the parking lot
//        parkingLotDTO.setAvailableSpots(parkingLotDTO.getAvailableSpots() - 1);
        long availableCount = parkingLotDTO.getSlots().stream()
                .filter(slot -> !slot.isOccupied())
                .count();
        parkingLotDTO.setAvailableSpots((int) availableCount);

        // Update the reservation object
        mapToEntity(reservationDTO, reservation);
        reservation.setId(reservationDTO.getId());
        reservation.setPricePaid(parkingLotDTO.getPricePerHour());  // Set price if needed
        reservation.setStatus("Booked");

        // Store the booked slot information (optional, based on your use case)
//        reservation.setSlotId(bookedSlot.getSlotId());
        String slotId = bookedSlot.getSlotId();

        // Save the reservation
        Reservation savedReservation = reservationRepository.save(reservation);

        // Save the updated parking lot with the new slot status
        parkingLotRepository.save(parkingLotDTO);

        // Send a confirmation email to the user
        sendReservationEmail(reservation, savedReservation, slotId);

        // Return the saved reservation's ID
        return savedReservation.getId();
    }

    public void sendReservationEmail(Reservation reservation, Reservation savedReservation, String slotId) {
        String userEmail = reservation.getUser().getEmail(); // Assuming you have a User object associated with the reservation
        String subject = "Reservation Confirmation - " + savedReservation.getId();

        // Format the start and end time into a readable format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' hh:mm a", Locale.US);
        String formattedStartTime = savedReservation.getStartTime().toLocalDateTime().format(formatter);
        String formattedEndTime = savedReservation.getEndTime().toLocalDateTime().format(formatter);

        // Format the email body with readable times
        String body = String.format(
                "Dear %s,\n\nYour reservation has been successfully booked!\n\n" +
                        "Reservation ID: %s\n" +
                        "Parking Lot: %s\n" +
                        "Slot: %s\n" +
                        "Start Time: %s\n" +
                        "End Time: %s\n" +
                        "Total Price: $%.2f\n\n" +
                        "Thank you for using our service.\n\nBest Regards,\nYour Parking Service Team",
                reservation.getUser().getFirstname(), // Assuming your User object has a `getName()` method
                savedReservation.getId(),
                reservation.getParkingLot().getName(),
                slotId,
                formattedStartTime,
                formattedEndTime,
                savedReservation.getPricePaid()
        );

        // Send email
        emailService.sendEmail(userEmail, subject, body);
    }

    public void update(final String id, final ReservationDTO reservationDTO) {
        final Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found with id: " + id));

        String newStatus = reservationDTO.getStatus();
        String currentStatus = existingReservation.getStatus();

        boolean isFinalStatusTransition =
                (("Cancelled".equals(newStatus) || "Checked Out".equals(newStatus)) &&
                        !newStatus.equals(currentStatus));
        if (isFinalStatusTransition) {
            String parkingLotId = reservationDTO.getParkingLot();
            ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId)
                    .orElseThrow(() -> new NotFoundException("Parking lot not found with id: " + parkingLotId));

            parkingLot.setAvailableSpots(parkingLot.getAvailableSpots() + 1);
            for (ParkingSlot slot : parkingLot.getSlots()) {
                if (slot.isOccupied()) {
                    slot.setOccupied(false);
                    break; // Only update the first one
                }
            }
            long availableCount = parkingLot.getSlots().stream()
                    .filter(slot -> !slot.isOccupied())
                    .count();
            parkingLot.setAvailableSpots((int) availableCount);
            parkingLotRepository.save(parkingLot);
        }

        // Update reservation details
        mapToEntity(reservationDTO, existingReservation);
        reservationRepository.save(existingReservation);
    }



    public void delete(final String id) {
        reservationRepository.deleteById(id);
    }

    private ReservationDTO mapToDTO(final Reservation reservation,
            final ReservationDTO reservationDTO) {
        reservationDTO.setId(reservation.getId());
        reservationDTO.setStartTime(reservation.getStartTime());
        reservationDTO.setEndTime(reservation.getEndTime());
        reservationDTO.setStatus(reservation.getStatus());
        reservationDTO.setPricePaid(reservation.getPricePaid());
        reservationDTO.setCreatedAt(reservation.getCreatedAt());
        reservationDTO.setUser(reservation.getUser() == null ? null : reservation.getUser().getId());
//        reservationDTO.setVehicle(reservation.getVehicle() == null ? null : reservation.getVehicle().getId());
        reservationDTO.setParkingLot(reservation.getParkingLot() == null ? null : reservation.getParkingLot().getId());
        return reservationDTO;
    }

    private Reservation mapToEntity(final ReservationDTO reservationDTO,
            final Reservation reservation) {
        reservation.setStartTime(reservationDTO.getStartTime());
        reservation.setEndTime(reservationDTO.getEndTime());
        reservation.setStatus(reservationDTO.getStatus());
        reservation.setPricePaid(reservationDTO.getPricePaid());
        reservation.setCreatedAt(reservationDTO.getCreatedAt());
        final User user = reservationDTO.getUser() == null ? null : userRepository.findById(reservationDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        reservation.setUser(user);
//        final Vehicle vehicle = reservationDTO.getVehicle() == null ? null : vehicleRepository.findById(reservationDTO.getVehicle())
//                .orElseThrow(() -> new NotFoundException("vehicle not found"));
//        reservation.setVehicle(vehicle);
        final ParkingLot parkingLot = reservationDTO.getParkingLot() == null ? null : parkingLotRepository.findById(reservationDTO.getParkingLot())
                .orElseThrow(() -> new NotFoundException("parkingLot not found"));
        reservation.setParkingLot(parkingLot);
        return reservation;
    }

    public boolean idExists(final String id) {
        return reservationRepository.existsByIdIgnoreCase(id);
    }

    public ReferencedWarning getReferencedWarning(final String id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Payment reservationPayment = paymentRepository.findFirstByReservation(reservation);
        if (reservationPayment != null) {
            referencedWarning.setKey("reservation.payment.reservation.referenced");
            referencedWarning.addParam(reservationPayment.getId());
            return referencedWarning;
        }
        return null;
    }

    public List<Reservation> getReservationsByUserId(String userId) {
        return reservationRepository.findByUserId(userId);
    }

}
