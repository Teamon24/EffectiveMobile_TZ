package org.effective_mobile.task_management_system.database.repository;

import org.effective_mobile.task_management_system.database.entity.Privilege;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrivilegeRepository extends AbstractJpaRepository<Privilege, Long> {

    Optional<Privilege> findByName(String name);

    @Query(value = """
            WITH RECURSIVE
            tree(name, id, parent_id) as (
                SELECT parent.name, parent.id, parent_id
                FROM privileges parent
                         LEFT JOIN roles_privileges rp on rp.privilege_id = parent.id
                         LEFT JOIN roles r on r.id = rp.role_id
                WHERE r.name in (:names)
                UNION
                SELECT child.name, child.id, child.parent_id
                FROM privileges child
                         JOIN tree ON child.parent_id = tree.id
                         LEFT JOIN roles_privileges rp on rp.privilege_id = child.id
                         LEFT JOIN roles r on r.id = rp.role_id
            )
            SELECT id, name, parent_id FROM tree
        """,
        nativeQuery = true)
    HashSet<Privilege> findByUserRoles(List<String> names);

    @Query(value =
        """
            WITH RECURSIVE tree(id, name, parent_id) as (
                SELECT p.id, p.name, parent_id
                FROM privileges p
                         LEFT JOIN roles_privileges rp ON p.id = rp.privilege_id
                         LEFT JOIN roles r ON r.id = rp.role_id
                WHERE p.id = :privilegeId
                UNION ALL
                SELECT child.id, child.name, child.parent_id
                FROM privileges child
                         JOIN tree ON child.parent_id = tree.id
            )
            SELECT id, name, parent_id FROM tree
        """,
        nativeQuery = true
    )
    HashSet<Privilege> findBranch(Long privilegeId);
}
