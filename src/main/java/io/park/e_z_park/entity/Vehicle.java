package io.park.e_z_park.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document
@Getter
@Setter
public class Vehicle {

    private String id = new ObjectId().toHexString();

    @NotNull
    @Size(max = 20)
    private String licensePlate;

    @Size(max = 50)
    private String vehicleType;

    @Size(max = 50)
    private String make;

    @Size(max = 50)
    private String model;

    @Size(max = 30)
    private String color;

    private LocalDateTime registeredAt;

//    @DocumentReference(lazy = true)
//    @NotNull
//    private User user;

//    @DocumentReference(lazy = true, lookup = "{ 'vehicle' : ?#{#self._id} }")
//    @ReadOnlyProperty
//    private Set<Reservation> vehicleReservations = new HashSet<>();

}
