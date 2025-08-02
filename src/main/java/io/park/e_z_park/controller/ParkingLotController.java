package io.park.e_z_park.controller;

import io.park.e_z_park.model.ParkingLotDTO;
import io.park.e_z_park.model.SlotStatusResponseDTO;
import io.park.e_z_park.service.ParkingLotService;
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
@RequestMapping(value = "/api/parkingLots", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(final ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @GetMapping
    public ResponseEntity<List<ParkingLotDTO>> getAllParkingLots() {
        return ResponseEntity.ok(parkingLotService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLotDTO> getParkingLot(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(parkingLotService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createParkingLot(
            @RequestBody @Valid final ParkingLotDTO parkingLotDTO) {
        final String createdId = parkingLotService.create(parkingLotDTO);
        return new ResponseEntity<>('"' + createdId + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateParkingLot(@PathVariable(name = "id") final String id,
            @RequestBody @Valid final ParkingLotDTO parkingLotDTO) {
        parkingLotService.update(id, parkingLotDTO);
        return ResponseEntity.ok('"' + id + '"');
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteParkingLot(@PathVariable(name = "id") final String id) {
        final ReferencedWarning referencedWarning = parkingLotService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        parkingLotService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<ParkingLotDTO>> getParkingLotsByLocationId(@PathVariable(name = "locationId") String locationId) {
        List<ParkingLotDTO> parkingLots = parkingLotService.findByLocationId(locationId);

        if (parkingLots.isEmpty()) {
            return ResponseEntity.noContent().build();  // Return HTTP 204 No Content if no parking lots found
        }
        return ResponseEntity.ok(parkingLots);
    }

    @GetMapping("/{slotId}/status")
    public ResponseEntity<SlotStatusResponseDTO> getSlotStatus(@PathVariable String slotId) {
        return ResponseEntity.ok(parkingLotService.getSlotStatus(slotId));
    }

}
