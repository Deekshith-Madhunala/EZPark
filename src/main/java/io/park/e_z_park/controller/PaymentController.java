package io.park.e_z_park.controller;

import io.park.e_z_park.model.PaymentDTO;
import io.park.e_z_park.service.PaymentService;
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
@RequestMapping(value = "/api/payments", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPayment(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(paymentService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createPayment(@RequestBody @Valid final PaymentDTO paymentDTO) {
        final String createdId = paymentService.create(paymentDTO);
        return new ResponseEntity<>('"' + createdId + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePayment(@PathVariable(name = "id") final String id,
            @RequestBody @Valid final PaymentDTO paymentDTO) {
        paymentService.update(id, paymentDTO);
        return ResponseEntity.ok('"' + id + '"');
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePayment(@PathVariable(name = "id") final String id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
