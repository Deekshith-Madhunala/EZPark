package io.park.e_z_park.entity;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
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
public class Location {

    @Id
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
    @Field(
            targetType = FieldType.DECIMAL128
    )
    private BigDecimal latitude;

    @Digits(integer = 9, fraction = 9)
    @Field(
            targetType = FieldType.DECIMAL128
    )
    private BigDecimal longitude;

    @DocumentReference(lazy = true, lookup = "{ 'location' : ?#{#self._id} }")
    @ReadOnlyProperty
    private Set<ParkingLot> locationParkingLots = new HashSet<>();

}
