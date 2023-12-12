package org.effective_mobile.task_management_system.component;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.entity.Role;
import org.effective_mobile.task_management_system.entity.Task;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.exception.messages.ExceptionMessages;
import org.effective_mobile.task_management_system.exception.UserAlreadyExistsException;
import org.effective_mobile.task_management_system.exception.messages.UserExceptionMessages;
import org.effective_mobile.task_management_system.pojo.auth.SignupPayload;
import org.effective_mobile.task_management_system.repository.UserRepository;
import org.effective_mobile.task_management_system.security.JwtPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Component
public class UserComponent {

    private UserRepository userRepository;
    private TaskComponent taskComponent;
    private PasswordEncoder passwordEncoder;
    private ContextComponent contextComponent;

    public Boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
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

    public User createAndSaveUser(SignupPayload signUpPayload, Role defaultRole) {
        User user = User.builder()
            .username(signUpPayload.getUsername())
            .roles(List.of(defaultRole))
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

    public void checkCurrentUserInPayload(@NotNull String creatorUsernameInPayload) {
        JwtPrincipal principal = contextComponent.getPrincipal();
        String currentUsername = principal.getUsernameAtDb();
        if (!currentUsername.equals(creatorUsernameInPayload)) {
            String message = ExceptionMessages.getMessage(
                "exception.access.task.creation",
                User.class.getSimpleName(),
                currentUsername,
                Task.class.getSimpleName(),
                User.class.getSimpleName(),
                creatorUsernameInPayload
            );
            throw new AccessDeniedException(message);
        }
    }

    public void checkCurrentUserIsCreator(Long taskId) {
        Task task = taskComponent.getTask(taskId);
        JwtPrincipal principal = contextComponent.getPrincipal();
        if (!Objects.equals(task.getCreator().getId(), principal.getUserId())) {
            throw createAccessDeniedEx(task, principal, "exception.access.task.edition.notCreator");
        }
    }

    public void checkCurrentUserIsExecutor(Long taskId) {
        Task task = taskComponent.getTask(taskId);
        JwtPrincipal principal = contextComponent.getPrincipal();
        User executor = task.getExecutor();
        if (!Objects.equals(executor.getId(), principal.getUserId())) {
            throw createAccessDeniedEx(task, principal, "exception.access.task.edition.notExecutor");
        }
    }

    private AccessDeniedException createAccessDeniedEx(
        Task task,
        JwtPrincipal principal,
        String templateKey
    ) {
        String message = ExceptionMessages.getMessage(
            templateKey,
            User.class.getSimpleName(),
            principal.getUsernameAtDb(),
            Task.class.getSimpleName(),
            task.getId()
        );
        return new AccessDeniedException(message);
    }
}
