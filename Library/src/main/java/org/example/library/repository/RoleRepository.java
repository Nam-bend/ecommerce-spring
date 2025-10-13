package org.example.library.repository;

import org.example.library.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
//    Role findByName(String name);
    Optional<Role> findByName(String name);
}
