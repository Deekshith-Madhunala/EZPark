package io.park.e_z_park.entity;

import lombok.Data;

@Data
public class ParkingSlot {

    private String slotId;
    private boolean isOccupied;
}
