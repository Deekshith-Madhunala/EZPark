package io.park.e_z_park.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
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
public class Reservation {

    @Id
    private String id;

    @NotNull
    private OffsetDateTime startTime;

    @NotNull
    private OffsetDateTime endTime;

    @NotNull
    @Size(max = 20)
    private String status;

    @Digits(integer = 6, fraction = 2)
    @Field(
            targetType = FieldType.DECIMAL128
    )
    private BigDecimal pricePaid;

    private LocalDateTime createdAt;

//    @DocumentReference(lazy = true)
//    @NotNull
//    private User user;

//    @DocumentReference(lazy = true)
//    @NotNull
//    private Vehicle vehicle;

    @DocumentReference(lazy = true)
    @NotNull
    @JsonIgnoreProperties({"reservations", "otherBackReferencesIfAny"})
    private User user;

    @DocumentReference(lazy = true)
    @NotNull
    @JsonIgnoreProperties({"reservations", "otherBackReferencesIfAny"})
    private ParkingLot parkingLot;


    @DocumentReference(lazy = true, lookup = "{ 'reservation' : ?#{#self._id} }")
    @ReadOnlyProperty
    private Set<Payment> reservationPayments = new HashSet<>();

}
