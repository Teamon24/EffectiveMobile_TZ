package org.effective_mobile.task_management_system.security;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.ContextComponent;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthorizationComponent {
    private UserComponent userComponent;
    private ContextComponent contextComponent;
    private TaskComponent taskComponent;

    public boolean currentUserIsCreator(Long taskId) {
        Task task = taskComponent.getTask(taskId);
        CustomUserDetails principal = contextComponent.getPrincipal();
        userComponent.checkCurrentUserIsCreator(principal, task);
        return true;
    }
}

