package io.park.e_z_park.repository;

import io.park.e_z_park.entity.Payment;
import io.park.e_z_park.entity.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface PaymentRepository extends MongoRepository<Payment, String> {

    Payment findFirstByReservation(Reservation reservation);

    boolean existsByIdIgnoreCase(String id);

}
