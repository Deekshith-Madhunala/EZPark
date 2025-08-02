package io.park.e_z_park.entity;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;


@Document
@Getter
@Setter
public class ParkingLot {

    @Id
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
    @Field(
            targetType = FieldType.DECIMAL128
    )
    private BigDecimal pricePerHour;

    @NotNull
    @Size(max = 20)
    private String type;

    @NotNull
    private LocalTime openingTime;

    @NotNull
    private LocalTime closingTime;

    private LocalDateTime createdAt;

    @DocumentReference(lazy = true)
    @NotNull
    private Location location;

    private List<ParkingSlot> slots = new java.util.ArrayList<>();

    @DocumentReference(lazy = true)
    @NotNull
    private User createdBy;

    @DocumentReference(lazy = true, lookup = "{ 'parkingLot' : ?#{#self._id} }")
    @ReadOnlyProperty
    private Set<Reservation> parkingLotReservations = new HashSet<>();

    @DocumentReference(lazy = true, lookup = "{ 'parkingLot' : ?#{#self._id} }")
    @ReadOnlyProperty
    private Set<LotAnalytic> parkingLotLotAnalytics = new HashSet<>();

}
