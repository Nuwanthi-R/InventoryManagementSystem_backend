package com.Inventory.management.Inventory.Management.System.controller;
import com.Inventory.management.Inventory.Management.System.model.ERole;
import com.Inventory.management.Inventory.Management.System.model.Role;
import com.Inventory.management.Inventory.Management.System.model.User;
import com.Inventory.management.Inventory.Management.System.repository.RoleRepo;
import com.Inventory.management.Inventory.Management.System.repository.UserRepo;
import com.Inventory.management.Inventory.Management.System.security.jwt.JwtUtils;
import com.Inventory.management.Inventory.Management.System.security.services.UserDetailsImpl;
import com.Inventory.management.Inventory.Management.System.payload.request.LoginRequest;
import com.Inventory.management.Inventory.Management.System.payload.request.SignupRequest;
import com.Inventory.management.Inventory.Management.System.payload.response.JwtResponse;
import com.Inventory.management.Inventory.Management.System.payload.response.MessageResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;


@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepo userRepository;

    @Autowired
    RoleRepo roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Attempting user authentication: {}", loginRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User authenticated successfully: {}", userDetails.getUsername());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            boolean usernameExists = userRepository.existsByUsername(signUpRequest.getUsername());
            boolean emailExists = userRepository.existsByEmail(signUpRequest.getEmail());

            if (usernameExists && emailExists) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Username and email are already taken!"));
            } else if (usernameExists) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Username is already taken!"));
            } else if (emailExists) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Email is already in use!"));
            }
            if ( signUpRequest.getRoles() == null ||signUpRequest.getRoles().contains("user")) {
                if (signUpRequest.getFirstName() == null || signUpRequest.getLastName() == null || signUpRequest.getTelephoneNumber() == null) {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: First name, last name, and telephone number are required for regular users!"));
                }
            }
            User user = new User(signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    encoder.encode(signUpRequest.getPassword()));
            Set<String> strRoles = signUpRequest.getRoles();
            Set<Role> roles = new HashSet<>();
            if (strRoles == null) {
                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            } else {
                strRoles.forEach(role -> {
                    switch (role) {
                        case "admin":
                            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(adminRole);
                            break;
                        default:
                            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                    }
                });
            }
            user.setRoles(roles);
            if (roles.stream().anyMatch(role -> role.getName().equals(ERole.ROLE_USER))) {
                user.setFirstName(signUpRequest.getFirstName());
                user.setLastName(signUpRequest.getLastName());
                user.setTelephoneNumber(signUpRequest.getTelephoneNumber());
            }
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (Exception e) {
            logger.error("Error occurred while registering user", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error occurred while registering user: " + e.getMessage()));
        }
    }
    @GetMapping("/user/profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<User> getUserProfile(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("Getting profile for user: {}", userDetails.getUsername());
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admin/profile")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getAdminProfile(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("Getting profile for admin: {}", userDetails.getUsername());
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.err.println("Error: User not found");
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user/{username}/profile")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUserProfileByUsername(@PathVariable String username) {
        logger.info("Getting profile for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }


    @PutMapping("/admin/updateProfile/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> updateAdminProfile(Authentication authentication, @RequestBody User updatedAdmin) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("Updating profile for admin: {}", userDetails.getUsername());
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        if (!admin.getUsername().equals(updatedAdmin.getUsername()) && userRepository.existsByUsername(updatedAdmin.getUsername())) {
            throw new RuntimeException("Username " + updatedAdmin.getUsername() + " is already taken");
        }
        if (!admin.getEmail().equals(updatedAdmin.getEmail()) && userRepository.existsByEmail(updatedAdmin.getEmail())) {
            throw new RuntimeException("Email " + updatedAdmin.getEmail() + " is already in use");
        }
        admin.setUsername(updatedAdmin.getUsername());
        admin.setEmail(updatedAdmin.getEmail());
        if (updatedAdmin.getPassword() != null) {
            admin.setPassword(passwordEncoder.encode(updatedAdmin.getPassword()));
        }
        userRepository.save(admin);
        return ResponseEntity.ok(admin);
    }


    @PutMapping("/user/updateProfile/{username}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<User> updateUserProfile(Authentication authentication, @RequestBody User updatedUser) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        logger.info("Updating profile for user: {}", userDetails.getUsername());
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.getUsername().equals(updatedUser.getUsername()) && userRepository.existsByUsername(updatedUser.getUsername())) {
            throw new RuntimeException("Username taken");
        }
        if (!user.getEmail().equals(updatedUser.getEmail()) && userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new RuntimeException("Email taken");
        }
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        user.setTelephoneNumber(updatedUser.getTelephoneNumber());
        user.setUsername(updatedUser.getUsername());
        String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }
}