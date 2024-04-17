package com.Inventory.management.Inventory.Management.System.repository;

import com.Inventory.management.Inventory.Management.System.model.ERole;
import com.Inventory.management.Inventory.Management.System.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface RoleRepo extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}