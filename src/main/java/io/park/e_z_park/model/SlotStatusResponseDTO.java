package io.park.e_z_park.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SlotStatusResponseDTO {
    private String slotId;
    private boolean isOccupied;

    private String parkingLotId;
    private String parkingLotName;
    private BigDecimal pricePerHour;
    private Integer availableSpots;
    private Integer totalSpots;

    // Location details
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
