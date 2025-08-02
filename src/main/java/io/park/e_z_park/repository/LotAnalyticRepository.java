package io.park.e_z_park.repository;

import io.park.e_z_park.entity.LotAnalytic;
import io.park.e_z_park.entity.ParkingLot;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface LotAnalyticRepository extends MongoRepository<LotAnalytic, String> {

    LotAnalytic findFirstByParkingLot(ParkingLot parkingLot);

    boolean existsByIdIgnoreCase(String id);

}
