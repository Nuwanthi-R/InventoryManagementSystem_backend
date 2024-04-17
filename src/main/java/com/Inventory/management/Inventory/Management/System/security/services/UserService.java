package com.Inventory.management.Inventory.Management.System.security.services;
import com.Inventory.management.Inventory.Management.System.model.Asset;
import com.Inventory.management.Inventory.Management.System.model.ERole;
import com.Inventory.management.Inventory.Management.System.model.Role;
import com.Inventory.management.Inventory.Management.System.model.User;
import com.Inventory.management.Inventory.Management.System.payload.request.UserCreationRequest;
import com.Inventory.management.Inventory.Management.System.repository.RoleRepo;
import com.Inventory.management.Inventory.Management.System.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }


    public User createUser(UserCreationRequest request) {
        if (userRepo.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username " + request.getUsername() + " is already taken");
        }
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email " + request.getEmail() + " is already in use");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setTelephoneNumber(request.getTelephoneNumber());

        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null) {
            for (String roleName : request.getRoles()) {
                ERole enumRole = ERole.valueOf(roleName.toUpperCase());
                Role role = roleRepo.findByName(enumRole)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        } else {
            Role defaultRole = roleRepo.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(defaultRole);
        }
        user.setRoles(roles);

        return userRepo.save(user);
    }

    public User getUserByUsername(String username) {
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (optionalUser.isPresent()){
            return optionalUser.get();
        } else{
            throw new RuntimeException("User not found with username: " + username);
        }

    }

    public User updateUser(String username, UserCreationRequest request) {
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            if (!existingUser.getUsername().equals(request.getUsername()) && userRepo.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username " + request.getUsername() + " is already taken");
            }
            if (!existingUser.getEmail().equals(request.getEmail()) && userRepo.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email " + request.getEmail() + " is already in use");
            }
            existingUser.setUsername(request.getUsername());
            existingUser.setEmail(request.getEmail());
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            existingUser.setTelephoneNumber(request.getTelephoneNumber());
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
            Set<Role> roles = new HashSet<>();
            if (request.getRoles() != null) {
                for (String roleName : request.getRoles()) {
                    ERole enumRole = ERole.valueOf(roleName.toUpperCase());
                    Role role = roleRepo.findByName(enumRole)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                    roles.add(role);
                }
            } else {

                Role defaultRole = roleRepo.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Default role not found"));
                roles.add(defaultRole);
            }
            existingUser.setRoles(roles);

            return userRepo.save(existingUser);
        } else {
            throw new RuntimeException("User not found with username: " + username);
        }
    }


    public void deleteUser(String username) {
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (optionalUser.isPresent()) {
            userRepo.deleteByUsername(username);
        } else {
            throw new RuntimeException("User not found with username: " + username);
        }
    }

    public List<String> getAllUsernames() {
        List<User> users = userRepo.findAll();
        return users.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

}