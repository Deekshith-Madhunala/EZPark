package io.park.e_z_park.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;


@Document
@Getter
@Setter
public class User {

    @Id
    private String id;

    @NotNull
    @Size(max = 100)
    private String firstname;

    @NotNull
    @Size(max = 100)
    private String lastname;

    @NotNull
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 100)
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String zipCode;

    @Size(max = 255)
    private String passwordHash;

    @NotNull
    @Size(max = 20)
    private String role;

    private LocalDateTime createdAt;

    private List<Vehicle> vehicles;

    @DocumentReference(lazy = true, lookup = "{ 'user' : ?#{#self._id} }")
    @ReadOnlyProperty
    @JsonIgnoreProperties({"user", "createdBy", "userVehicles", "createdByParkingLots", "userReservations"})
    private Set<Vehicle> userVehicles = new HashSet<>();

    @DocumentReference(lazy = true, lookup = "{ 'createdBy' : ?#{#self._id} }")
    @ReadOnlyProperty
    @JsonIgnoreProperties({"user", "createdBy", "userVehicles", "createdByParkingLots", "userReservations"})
    private Set<ParkingLot> createdByParkingLots = new HashSet<>();

// Uncommented for example, also add JsonIgnoreProperties if needed
//@DocumentReference(lazy = true, lookup = "{ 'user' : ?#{#self._id} }")
//@ReadOnlyProperty
//@JsonIgnoreProperties({"user", "createdBy", "userVehicles", "createdByParkingLots", "userReservations"})
//private Set<Reservation> userReservations = new HashSet<>();


//    @DocumentReference(lazy = true, lookup = "{ 'user' : ?#{#self._id} }")
//    @ReadOnlyProperty
//    private Set<Reservation> userReservations = new HashSet<>();

}
