package io.park.e_z_park.repository;

import io.park.e_z_park.entity.ParkingLot;
import io.park.e_z_park.entity.Reservation;
import io.park.e_z_park.entity.User;
import io.park.e_z_park.entity.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface ReservationRepository extends MongoRepository<Reservation, String> {

    Reservation findFirstByUser(User user);

//    Reservation findFirstByVehicle(Vehicle vehicle);

    Reservation findFirstByParkingLot(ParkingLot parkingLot);

    boolean existsByIdIgnoreCase(String id);

    List<Reservation> findByUserId(String userId);
}
