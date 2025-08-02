package io.park.e_z_park.repository;

import io.park.e_z_park.entity.User;
import io.park.e_z_park.entity.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface VehicleRepository extends MongoRepository<Vehicle, String> {

//    Vehicle findFirstByUser(User user);

    boolean existsByIdIgnoreCase(String id);

}
