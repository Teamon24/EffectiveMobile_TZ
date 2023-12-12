package org.effective_mobile.task_management_system.security;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.ContextComponent;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.enums.Status;
import org.effective_mobile.task_management_system.exception.IllegalStatusChangeException;
import org.effective_mobile.task_management_system.pojo.task.TaskCreationPayload;
import org.springframework.stereotype.Component;

import static org.effective_mobile.task_management_system.enums.Status.ASSIGNED;
import static org.effective_mobile.task_management_system.enums.Status.NEW;
import static org.effective_mobile.task_management_system.exception.messages.ExceptionMessages.getMessage;

@Component
@AllArgsConstructor
public class AuthorizationComponent {
    private UserComponent userComponent;

    public boolean currentUserIsCreator(Long taskId) {
        userComponent.checkCurrentUserIsCreator(taskId);
        return true;
    }

    public boolean currentUserIsExecutor(Long taskId) {
        userComponent.checkCurrentUserIsExecutor(taskId);
        return true;
    }

    public boolean canChangeStatus(Long taskId, Status newStatus) {
        return switch (newStatus) {
            case ASSIGNED -> {
                String message = getMessage("exception.access.task.status.assign", ASSIGNED);
                throw new IllegalStatusChangeException(message);
            }
            case EXECUTING, DONE, PENDING -> currentUserIsExecutor(taskId);
            case NEW -> {
                String message = getMessage("exception.access.task.status.initial", NEW);
                throw new IllegalStatusChangeException(message);
            }
        };
    }
}

