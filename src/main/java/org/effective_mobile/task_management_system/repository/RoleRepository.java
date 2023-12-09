package org.effective_mobile.task_management_system.repository;

import java.util.Optional;

import org.effective_mobile.task_management_system.entity.Role;
import org.effective_mobile.task_management_system.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(UserRole name);
}