// src/main/java/com/devops/userservice/repository/RoleRepository.java
package com.devops.userservice.repository;

import com.devops.userservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(Role.ERole name);
}