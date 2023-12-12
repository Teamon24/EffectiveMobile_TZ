package org.effective_mobile.task_management_system.component;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.entity.Role;
import org.effective_mobile.task_management_system.enums.UserRole;
import org.effective_mobile.task_management_system.repository.RoleRepository;
import org.springframework.stereotype.Component;

import static org.effective_mobile.task_management_system.exception.messages.ExceptionMessages.getMessage;

@AllArgsConstructor
@Component
public class RoleComponent {

    private RoleRepository roleRepository;

    public Role getDefaultRole() {
        UserRole defaultRole = UserRole.USER;
        return findByNameOrThrow(defaultRole);
    }

    public Role findByName(UserRole userRole) {
        return findByNameOrThrow(userRole);
    }

    private Role findByNameOrThrow(UserRole defaultRole) {
        return roleRepository
            .findByName(defaultRole)
            .orElseThrow(() -> new EntityNotFoundException(roleNotFoundMessage(defaultRole)));
    }

    private String roleNotFoundMessage(UserRole defaultRole) {
        return getMessage(
            "exception.entity.notFound.role.by.name",
            Role.class.getSimpleName(),
            defaultRole.name()
        );
    }
}
