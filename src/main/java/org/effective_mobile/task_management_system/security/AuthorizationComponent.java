package org.effective_mobile.task_management_system.security;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthorizationComponent {
    private UserComponent userComponent;

    public boolean currentUserIsCreator(Long taskId) {
        userComponent.checkCurrentUserIsCreator(taskId);
        return true;
    }
}

