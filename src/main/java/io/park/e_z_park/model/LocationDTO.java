package io.park.e_z_park.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LocationDTO {

    @Size(max = 255)
    private String id;

    @Size(max = 500)
    private String street;

    @Size(max = 50)
    private String city;

    @Size(max = 50)
    private String state;

    @Size(max = 10)
    private String zipCode;

    @Size(max = 50)
    private String country;

    @Digits(integer = 9, fraction = 9)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "84.0008")
    private BigDecimal latitude;

    @Digits(integer = 9, fraction = 9)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "39.0008")
    private BigDecimal longitude;

}
