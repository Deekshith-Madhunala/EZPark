package io.park.e_z_park.controller;

import io.park.e_z_park.entity.Reservation;
import io.park.e_z_park.model.ReservationDTO;
import io.park.e_z_park.service.ReservationService;
import io.park.e_z_park.util.ReferencedException;
import io.park.e_z_park.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping(value = "/api/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservation(
            @PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(reservationService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createReservation(
            @RequestBody @Valid final ReservationDTO reservationDTO) {
        final String createdId = reservationService.create(reservationDTO);
        return new ResponseEntity<>('"' + createdId + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateReservation(@PathVariable(name = "id") final String id,
            @RequestBody @Valid final ReservationDTO reservationDTO) {
        reservationService.update(id, reservationDTO);
        return ResponseEntity.ok('"' + id + '"');
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteReservation(@PathVariable(name = "id") final String id) {
        final ReferencedWarning referencedWarning = reservationService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public List<ReservationDTO> getReservationsByUser(@PathVariable String userId) {
        List<Reservation> reservations = reservationService.getReservationsByUserId(userId);
        // map entities to DTOs simply here
        return reservations.stream()
                .map(reservation -> {
                    ReservationDTO dto = new ReservationDTO();
                    dto.setId(reservation.getId());
                    dto.setStartTime(reservation.getStartTime());
                    dto.setEndTime(reservation.getEndTime());
                    dto.setStatus(reservation.getStatus());
                    dto.setPricePaid(reservation.getPricePaid());
                    dto.setCreatedAt(reservation.getCreatedAt());
                    dto.setUser(reservation.getUser() != null ? reservation.getUser().getId() : null);
                    dto.setParkingLot(reservation.getParkingLot() != null ? reservation.getParkingLot().getId() : null);
                    return dto;
                })
                .toList();
    }


}
