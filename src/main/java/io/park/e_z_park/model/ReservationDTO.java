package io.park.e_z_park.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReservationDTO {

    @Size(max = 255)
    private String id;

    @NotNull
    private OffsetDateTime startTime;

    @NotNull
    private OffsetDateTime endTime;

    @NotNull
    @Size(max = 20)
    private String status;

    @Digits(integer = 6, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "47.08")
    private BigDecimal pricePaid;

    private LocalDateTime createdAt;

    @NotNull
    @Size(max = 255)
    private String user;

//    @NotNull
//    @Size(max = 255)
//    private String vehicle;

    @NotNull
    @Size(max = 255)
    private String parkingLot;

}
