package io.park.e_z_park.repository;

import io.park.e_z_park.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByIdIgnoreCase(String id);

    User findByEmail(String email);
}
