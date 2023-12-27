package org.effective_mobile.task_management_system.component;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.maintain.cache.AppCacheNames;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.database.repository.UserRepository;
import org.effective_mobile.task_management_system.exception.DeniedOperationException;
import org.effective_mobile.task_management_system.exception.IllegalStatusChangeException;
import org.effective_mobile.task_management_system.exception.TaskHasNoExecutorException;
import org.effective_mobile.task_management_system.exception.UserAlreadyExistsException;
import org.effective_mobile.task_management_system.exception.messages.TaskExceptionMessages;
import org.effective_mobile.task_management_system.exception.messages.UserExceptionMessages;
import org.effective_mobile.task_management_system.resource.json.auth.SignupRequestPojo;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static org.effective_mobile.task_management_system.exception.messages.ExceptionMessages.getMessage;
import static org.effective_mobile.task_management_system.exception.messages.UserExceptionMessages.NotFoundBy.EMAIL;
import static org.effective_mobile.task_management_system.utils.enums.Status.ASSIGNED;
import static org.effective_mobile.task_management_system.utils.enums.Status.NEW;

@Component
@AllArgsConstructor
public class UserComponent {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public void checkUsernameExists(String username) {
        if (!usernameExists(username)) {
            throw new EntityNotFoundException(UserExceptionMessages.notFound(username));
        }
    }

    public Boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void checkUsernameDoesNotExist(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException(UserExceptionMessages.usernameExists(username));
        }
    }

    public void checkEmailDoesNotExist(String email) {
        if (emailExists(email)) {
            throw new UserAlreadyExistsException(UserExceptionMessages.emailExists(email));
        }
    }

    public User createAndSaveUser(SignupRequestPojo signUpPayload) {
        User user = User.builder()
            .username(signUpPayload.getUsername())
            .email(signUpPayload.getEmail())
            .password(passwordEncoder.encode(signUpPayload.getPassword())).build();

        userRepository.save(user);
        return user;
    }

    public User getByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        user.orElseThrow(() -> new EntityNotFoundException(UserExceptionMessages.notFound(username)));
        return user.get();
    }

    @Cacheable(cacheNames = AppCacheNames.USERS_AUTH, key = "#email")
    public User getByEmail(String email) {
        return userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException(UserExceptionMessages.notFoundBy(EMAIL, email)));
    }

    public User getById(Long id) {
        return userRepository.findOrThrow(User.class, id);
    }

    public void checkCurrentUserIsCreator(CustomUserDetails principal, Task task) {
        Long creatorId = task.getCreator().getId();
        Long currentUserId = principal.getUserId();
        if (!Objects.equals(creatorId, currentUserId)) {
            throw createDeniedOperationEx(task, principal, "exception.access.task.edition.notCreator");
        }
    }

    public void validateStatusChange(Task task, Status newStatus, CustomUserDetails principal) {
        switch (newStatus) {
            case ASSIGNED -> {
                String message = getMessage("exception.task.status.assign", ASSIGNED);
                throw new IllegalStatusChangeException(message);
            }
            case EXECUTING, DONE, PENDING -> {
                checkCurrentUserIsExecutor(task, principal);
            }
            case NEW -> {
                String message = getMessage("exception.task.status.initial", NEW);
                throw new IllegalStatusChangeException(message);
            }
        };
    }

    private void checkCurrentUserIsExecutor(Task task, CustomUserDetails principal) {
        User executor = task.getExecutor();

        if (executor == null) {
            String message = TaskExceptionMessages.hasNoExecutor(task.getId());
            throw new TaskHasNoExecutorException(message);
        }

        if (!Objects.equals(executor.getId(), principal.getUserId())) {
            throw createDeniedOperationEx(task, principal, "exception.access.task.edition.notExecutor");
        }
    }

    private DeniedOperationException createDeniedOperationEx(
        Task task,
        CustomUserDetails principal,
        String templateKey
    ) {
        String message = getMessage(
            templateKey,
            User.class.getSimpleName(),
            principal.getUsernameAtDb(),
            Task.class.getSimpleName(),
            task.getId()
        );
        return new DeniedOperationException(message);
    }
}
