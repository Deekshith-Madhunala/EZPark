package io.park.e_z_park.repository;

import io.park.e_z_park.entity.Location;
import io.park.e_z_park.entity.ParkingLot;
import io.park.e_z_park.entity.User;
import io.park.e_z_park.model.ParkingLotDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface ParkingLotRepository extends MongoRepository<ParkingLot, String> {

    ParkingLot findFirstByLocation(Location location);

    ParkingLot findFirstByCreatedBy(User user);

    boolean existsByIdIgnoreCase(String id);

    List<ParkingLotDTO> findByLocationId(String locationId);

    Optional<ParkingLot> findBySlotsSlotId(String slotId);

    Optional<ParkingLot> findByName(String name);
}
