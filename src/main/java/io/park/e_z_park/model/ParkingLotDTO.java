package io.park.e_z_park.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.park.e_z_park.entity.ParkingSlot;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ParkingLotDTO {

    @Size(max = 255)
    private String id;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    private Integer totalSpots;

    @NotNull
    private Integer availableSpots;

    @NotNull
    @Digits(integer = 5, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "36.08")
    private BigDecimal pricePerHour;

    @NotNull
    @Size(max = 20)
    private String type;

    @NotNull
    @Schema(type = "string", example = "18:30")
    private LocalTime openingTime;

    @NotNull
    @Schema(type = "string", example = "18:30")
    private LocalTime closingTime;

    private LocalDateTime createdAt;

    private List<ParkingSlot> slots = new java.util.ArrayList<>();


    @NotNull
    @Size(max = 255)
    private String location;

    @NotNull
    @Size(max = 255)
    private String createdBy;

}
