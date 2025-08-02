package io.park.e_z_park.entity;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;


@Document
@Getter
@Setter
public class LotAnalytic {

    @Id
    private String id;

    @NotNull
    private LocalDate reportDate;

    private Integer totalBookings;

    @Digits(integer = 10, fraction = 2)
    @Field(
            targetType = FieldType.DECIMAL128
    )
    private BigDecimal totalRevenue;

    @Digits(integer = 3, fraction = 2)
    @Field(
            targetType = FieldType.DECIMAL128
    )
    private BigDecimal avgOccupancy;

    @DocumentReference(lazy = true)
    @NotNull
    private ParkingLot parkingLot;

}
