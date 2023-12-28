package org.effective_mobile.task_management_system.security;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.ContextComponent;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@AllArgsConstructor
public class AuthorizationComponentImpl implements AuthorizationComponent {

    private UserComponent userComponent;
    private ContextComponent contextComponent;
    private TaskComponent taskComponent;

    @Override
    public boolean currentUserIsCreator(Long taskId) {
        Task task = taskComponent.getTask(taskId);
        CustomUserDetails principal = contextComponent.getPrincipal();
        userComponent.checkCurrentUserIsCreator(principal, task);
        return true;
    }

    @Override
    public HashSet<GrantedAuthority> getAuthorities(User user) {
        return new HashSet<>();
    }
}

