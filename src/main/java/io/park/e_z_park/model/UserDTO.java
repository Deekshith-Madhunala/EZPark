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

    @Size(max = 100)
    private String name;

    @NotNull
    @Size(max = 100)
    private String email;

    @Size(max = 255)
    private String passwordHash;

    @Size(max = 20)
    private String phone;

    @NotNull
    @Size(max = 20)
    private String role;

    private LocalDateTime createdAt;

    private List<Vehicle> vehicles;

}
