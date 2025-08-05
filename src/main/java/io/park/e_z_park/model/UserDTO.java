package io.park.e_z_park.model;

import io.park.e_z_park.entity.Vehicle;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    @Size(max = 255)
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

}
