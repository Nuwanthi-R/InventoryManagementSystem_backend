package com.Inventory.management.Inventory.Management.System.repository;

import com.Inventory.management.Inventory.Management.System.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo  extends MongoRepository<User, String> {

        Optional<User> findByUsername(String username);

        Boolean existsByUsername(String username);

        Boolean existsByEmail(String email);

        Optional<User> findByEmail(String email);

        Optional<User> findByFirstName(String firstname);
        void deleteByUsername(String username);

}

