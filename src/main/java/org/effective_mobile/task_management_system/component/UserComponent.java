package org.effective_mobile.task_management_system.component;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.database.repository.UserRepository;
import org.effective_mobile.task_management_system.exception.DeniedOperationException;
import org.effective_mobile.task_management_system.exception.UserAlreadyExistsException;
import org.effective_mobile.task_management_system.exception.messages.AccessExceptionMessages;
import org.effective_mobile.task_management_system.exception.messages.UserExceptionMessages;
import org.effective_mobile.task_management_system.resource.json.auth.SignupRequestPojo;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static org.effective_mobile.task_management_system.exception.messages.UserExceptionMessages.NotFoundBy.EMAIL;
import static org.effective_mobile.task_management_system.maintain.cache.AppCacheNames.USERS_AUTH;

@Component
@AllArgsConstructor
public class UserComponent {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getById(Long id) {
        return userRepository.findOrThrow(User.class, id);
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

    @Cacheable(cacheNames = USERS_AUTH, key = "#email")
    public User getByEmail(String email) {
        return userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException(UserExceptionMessages.notFoundBy(EMAIL, email)));
    }

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

    public void checkUserIsCreator(CustomUserDetails principal, Task task) {
        if (!isCreator(principal, task)) {
            String message = AccessExceptionMessages.notACreator(principal, task.getId());
            throw new DeniedOperationException(message);
        }
    }

    public void checkUserIsExecutor(CustomUserDetails principal, Task task) {
        if (!isExecutor(principal, task)) {
            String message = AccessExceptionMessages.notAExecutor(principal, task.getId());
            throw new DeniedOperationException(message);
        }
    }

    public boolean isExecutor(CustomUserDetails principal, Task task) {
        User executor = task.getExecutor();
        if (executor == null) return false;
        return Objects.equals(principal.getUserId(), executor.getId());
    }

    public boolean isCreator(CustomUserDetails principal, Task task) {
        Long creatorId = task.getCreator().getId();
        return Objects.equals(principal.getUserId(), creatorId);
    }
}
