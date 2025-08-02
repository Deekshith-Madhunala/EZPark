package io.park.e_z_park.repository;

import io.park.e_z_park.entity.Location;
import io.park.e_z_park.model.LocationDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface LocationRepository extends MongoRepository<Location, String> {

    boolean existsByIdIgnoreCase(String id);

    List<LocationDTO> findByCity(String city);

}
