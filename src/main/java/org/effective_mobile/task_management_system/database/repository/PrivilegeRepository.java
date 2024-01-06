package org.effective_mobile.task_management_system.database.repository;

import org.effective_mobile.task_management_system.database.entity.Privilege;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.HashSet;

@NoRepositoryBean
public interface PrivilegeRepository extends AbstractJpaRepository<Privilege, Long> {
    HashSet<Privilege> findByUserId(Long userId);
}
