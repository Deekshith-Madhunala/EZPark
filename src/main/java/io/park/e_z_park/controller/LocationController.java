package io.park.e_z_park.controller;

import io.park.e_z_park.model.LocationDTO;
import io.park.e_z_park.service.LocationService;
import io.park.e_z_park.util.ReferencedException;
import io.park.e_z_park.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
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


@RestController
@RequestMapping(value = "/api/locations", produces = MediaType.APPLICATION_JSON_VALUE)
public class LocationController {

    private final LocationService locationService;

    public LocationController(final LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        return ResponseEntity.ok(locationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocation(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(locationService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createLocation(
            @RequestBody @Valid final LocationDTO locationDTO) {
        final String createdId = locationService.create(locationDTO);
        return new ResponseEntity<>('"' + createdId + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateLocation(@PathVariable(name = "id") final String id,
            @RequestBody @Valid final LocationDTO locationDTO) {
        locationService.update(id, locationDTO);
        return ResponseEntity.ok('"' + id + '"');
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteLocation(@PathVariable(name = "id") final String id) {
        final ReferencedWarning referencedWarning = locationService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/city/{cityName}")
    public ResponseEntity<List<LocationDTO>> getLocationsByCity(@PathVariable(name = "cityName") String cityName) {
        List<LocationDTO> locations = locationService.findByCity(cityName);
        if (locations.isEmpty()) {
            return ResponseEntity.noContent().build();  // Return HTTP 204 No Content if no locations found
        }
        return ResponseEntity.ok(locations);
    }

}
