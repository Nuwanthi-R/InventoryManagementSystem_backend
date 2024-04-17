package com.Inventory.management.Inventory.Management.System.controller;
import com.Inventory.management.Inventory.Management.System.model.ERole;
import com.Inventory.management.Inventory.Management.System.model.User;
import com.Inventory.management.Inventory.Management.System.payload.request.UserCreationRequest;
import com.Inventory.management.Inventory.Management.System.payload.response.MessageResponse;
import com.Inventory.management.Inventory.Management.System.repository.UserRepo;
import com.Inventory.management.Inventory.Management.System.security.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userService.getAllUsers();
        logger.debug("Retrieved {} users", users.size());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreationRequest request) {
        logger.info("Creating user: {}", request.getUsername());
        User createdUser = userService.createUser(request);
        logger.info("User created: {}", createdUser.getUsername());
        return ResponseEntity.ok(createdUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getUser/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        logger.info("Fetching user by username: {}", username);
        User user = userService.getUserByUsername(username);
        if (user != null) {
            logger.debug("User found: {}", user.getUsername());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            logger.warn("User not found with username: {}", username);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/updateUser/{username}")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable String username, @RequestBody UserCreationRequest request) {
        logger.info("Attempting to update user with username: {}", username);
        userService.updateUser(username, request);
        logger.info("User updated successfully: {}", username);
        return ResponseEntity.ok(new MessageResponse("User updated successfully"));
    }

    @GetMapping("/checkUsername/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = userRepo.existsByUsername(username);
        logger.info("Checking if username exists: {}", username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/checkEmail/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        logger.info("Checking if email exists: {}", email);
        boolean exists = userRepo.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteUser/{username}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable String username) {
        logger.info("Attempting to delete user with username: {}", username);
        userService.deleteUser(username);
        logger.info("User deleted successfully: {}", username);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }

    @GetMapping("/allUsernames")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public List<String> getAllUsernames() {
        logger.info("Getting all usernames");
        return userService.getAllUsernames();
    }
}
