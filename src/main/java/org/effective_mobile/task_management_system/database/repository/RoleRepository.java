package org.effective_mobile.task_management_system.database.repository;

import org.effective_mobile.task_management_system.database.entity.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends AbstractJpaRepository<Role, Long> {
}
