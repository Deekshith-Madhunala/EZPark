package io.park.e_z_park.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaymentDTO {

    @Size(max = 255)
    private String id;

    @NotNull
    @Digits(integer = 6, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "92.08")
    private BigDecimal amount;

    @NotNull
    @Size(max = 20)
    private String status;

    @NotNull
    @Size(max = 20)
    private String method;

    @Size(max = 100)
    private String transactionId;

    private LocalDateTime paidAt;

    @NotNull
    @Size(max = 255)
    private String reservation;

}
