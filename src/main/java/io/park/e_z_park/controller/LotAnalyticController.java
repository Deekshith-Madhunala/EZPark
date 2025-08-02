package io.park.e_z_park.controller;

import io.park.e_z_park.model.LotAnalyticDTO;
import io.park.e_z_park.service.LotAnalyticService;
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
@RequestMapping(value = "/api/lotAnalytics", produces = MediaType.APPLICATION_JSON_VALUE)
public class LotAnalyticController {

    private final LotAnalyticService lotAnalyticService;

    public LotAnalyticController(final LotAnalyticService lotAnalyticService) {
        this.lotAnalyticService = lotAnalyticService;
    }

    @GetMapping
    public ResponseEntity<List<LotAnalyticDTO>> getAllLotAnalytics() {
        return ResponseEntity.ok(lotAnalyticService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LotAnalyticDTO> getLotAnalytic(
            @PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(lotAnalyticService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createLotAnalytic(
            @RequestBody @Valid final LotAnalyticDTO lotAnalyticDTO) {
        final String createdId = lotAnalyticService.create(lotAnalyticDTO);
        return new ResponseEntity<>('"' + createdId + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateLotAnalytic(@PathVariable(name = "id") final String id,
            @RequestBody @Valid final LotAnalyticDTO lotAnalyticDTO) {
        lotAnalyticService.update(id, lotAnalyticDTO);
        return ResponseEntity.ok('"' + id + '"');
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteLotAnalytic(@PathVariable(name = "id") final String id) {
        lotAnalyticService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
