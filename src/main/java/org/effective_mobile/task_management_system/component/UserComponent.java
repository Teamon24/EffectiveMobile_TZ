package org.effective_mobile.task_management_system.component;

import jakarta.persistence.EntityNotFoundException;
import org.effective_mobile.task_management_system.entity.Task;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.exception.DeniedOperationException;
import org.effective_mobile.task_management_system.exception.TaskHasNoExecutorException;
import org.effective_mobile.task_management_system.exception.UserAlreadyExistsException;
import org.effective_mobile.task_management_system.exception.messages.TaskExceptionMessages;
import org.effective_mobile.task_management_system.exception.messages.UserExceptionMessages;
import org.effective_mobile.task_management_system.pojo.auth.SignupRequestPojo;
import org.effective_mobile.task_management_system.repository.UserRepository;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static org.effective_mobile.task_management_system.exception.messages.ExceptionMessages.getMessage;

@Component
public class UserComponent {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContextComponent contextComponent;

    private TaskComponent taskComponent;

    public UserComponent(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        ContextComponent contextComponent
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.contextComponent = contextComponent;
    }

    /**
     * Сеттер для устранения циклической зависимости.
     * @param taskComponent - экземпляр класса {@link TaskComponent}.
     */
    public void setTaskComponent(TaskComponent taskComponent) {
        this.taskComponent = taskComponent;
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

    public User getById(Long id) {
        return userRepository.findOrThrow(User.class, id);
    }

    public void checkCurrentUserIsCreator(Long taskId) {
        Task task = taskComponent.getTask(taskId);
        CustomUserDetails principal = contextComponent.getPrincipal();
        if (!Objects.equals(task.getCreator().getId(), principal.getUserId())) {
            throw createDeniedOperationEx(task, principal, "exception.access.task.edition.notCreator");
        }
    }

    public void checkCurrentUserIsExecutor(Long taskId) {
        Task task = taskComponent.getTask(taskId);
        CustomUserDetails principal = contextComponent.getPrincipal();
        User executor = task.getExecutor();

        if (executor == null) {
            String message = TaskExceptionMessages.hasNoExecutor(taskId);
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
