package io.park.e_z_park.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LotAnalyticDTO {

    @Size(max = 255)
    private String id;

    @NotNull
    private LocalDate reportDate;

    private Integer totalBookings;

    @Digits(integer = 10, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "36.08")
    private BigDecimal totalRevenue;

    @Digits(integer = 3, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "1.08")
    private BigDecimal avgOccupancy;

    @NotNull
    @Size(max = 255)
    private String parkingLot;

}
