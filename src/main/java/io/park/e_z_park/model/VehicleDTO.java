package io.park.e_z_park.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;


@Getter
@Setter
public class VehicleDTO {

    @Size(max = 255)
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

//    @NotNull
//    @Size(max = 255)
//    private String user;

}
