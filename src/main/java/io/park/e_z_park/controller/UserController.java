package io.park.e_z_park.controller;

import io.park.e_z_park.entity.User;
import io.park.e_z_park.entity.Vehicle;
import io.park.e_z_park.model.UserDTO;
import io.park.e_z_park.service.UserService;
import io.park.e_z_park.util.ReferencedException;
import io.park.e_z_park.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(userService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createUser(@RequestBody @Valid final UserDTO userDTO) {
        final String createdUserToken = userService.create(userDTO);
        return new ResponseEntity<>(createdUserToken, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable(name = "id") final String id,
            @RequestBody @Valid final UserDTO userDTO) {
        userService.update(id, userDTO);
        return ResponseEntity.ok('"' + id + '"');
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") final String id) {
        final ReferencedWarning referencedWarning = userService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam(name = "email") final String email,
                                            @RequestParam(name = "password") final String password){
        log.info("Received login request from user "+ email);
        return ResponseEntity.ok(userService.findByEmailAndPassword(email, password));
    }

    @PutMapping("/{userId}/vehicles")
    public ResponseEntity<User> updateUserVehicle(
            @PathVariable String userId,
            @RequestBody Vehicle vehicle) {
        User updatedUser = userService.addVehicleToUser(userId, vehicle);
        return ResponseEntity.ok(updatedUser);
    }
}
