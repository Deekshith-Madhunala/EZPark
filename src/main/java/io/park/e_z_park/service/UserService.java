package io.park.e_z_park.service;

import io.park.e_z_park.entity.ParkingLot;
import io.park.e_z_park.entity.Reservation;
import io.park.e_z_park.entity.User;
import io.park.e_z_park.entity.Vehicle;
import io.park.e_z_park.model.UserDTO;
import io.park.e_z_park.repository.ParkingLotRepository;
import io.park.e_z_park.repository.ReservationRepository;
import io.park.e_z_park.repository.UserRepository;
import io.park.e_z_park.repository.VehicleRepository;
import io.park.e_z_park.util.JwtUtil;
import io.park.e_z_park.util.NotFoundException;
import io.park.e_z_park.util.ReferencedWarning;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ReservationRepository reservationRepository;

    public UserService(final UserRepository userRepository,
            final VehicleRepository vehicleRepository,
            final ParkingLotRepository parkingLotRepository,
            final ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("id"));
        return users.stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList();
    }

    public UserDTO get(final String id) {
        return userRepository.findById(id)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        user.setId(userDTO.getId());
        User savedUser = userRepository.save(user);
        return JwtUtil.generateToken(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole());
//                user.getVehicles());

    }

    public void update(final String id, final UserDTO userDTO) {
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final String id) {
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPasswordHash(user.getPasswordHash());
        userDTO.setPhone(user.getPhone());
        userDTO.setRole(user.getRole());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setVehicles(user.getVehicles());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPasswordHash(userDTO.getPasswordHash());
        user.setPhone(userDTO.getPhone());
        user.setRole(userDTO.getRole());
        user.setCreatedAt(userDTO.getCreatedAt());
        user.setVehicles(userDTO.getVehicles());
        return user;
    }

    public boolean idExists(final String id) {
        return userRepository.existsByIdIgnoreCase(id);
    }

    public ReferencedWarning getReferencedWarning(final String id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
//        final Vehicle userVehicle = vehicleRepository.findFirstByUser(user);
//        if (userVehicle != null) {
//            referencedWarning.setKey("user.vehicle.user.referenced");
//            referencedWarning.addParam(userVehicle.getId());
//            return referencedWarning;
//        }
        final ParkingLot createdByParkingLot = parkingLotRepository.findFirstByCreatedBy(user);
        if (createdByParkingLot != null) {
            referencedWarning.setKey("user.parkingLot.createdBy.referenced");
            referencedWarning.addParam(createdByParkingLot.getId());
            return referencedWarning;
        }
        final Reservation userReservation = reservationRepository.findFirstByUser(user);
        if (userReservation != null) {
            referencedWarning.setKey("user.reservation.user.referenced");
            referencedWarning.addParam(userReservation.getId());
            return referencedWarning;
        }
        return null;
    }

    public String findByEmailAndPassword(String email, String password) {
        final User user = userRepository.findByEmail(email); // Find user by email
        if (user == null) {
            throw new NotFoundException("User not found");
        }
//        if (!PasswordEncryptionUtil.matchPassword(password, user.getPassword())) {
//            throw new IllegalArgumentException("Invalid password");
//        }
        return JwtUtil.generateToken(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
//                user.getVehicles()
        );
    }

    public User addVehicleToUser(String userId, Vehicle vehicle) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate ObjectId-style ID if needed
            if (vehicle.getId() == null || vehicle.getId().isEmpty()) {
                vehicle.setId(new ObjectId().toHexString());
            }

        // FIXED: Provide ZoneId to convert Instant to LocalDateTime
        vehicle.setRegisteredAt(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        if (user.getVehicles() == null) {
            user.setVehicles(new ArrayList<>());
        }

        user.getVehicles().add(vehicle);
        return userRepository.save(user);
    }
}
