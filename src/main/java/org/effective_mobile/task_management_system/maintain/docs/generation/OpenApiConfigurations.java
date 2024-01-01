package org.effective_mobile.task_management_system.maintain.docs.generation;

import jakarta.persistence.EntityManager;
import org.effective_mobile.task_management_system.component.CommentComponent;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.component.TaskComponentImpl;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.database.repository.CommentRepository;
import org.effective_mobile.task_management_system.database.repository.FilteredAndPagedTaskRepository;
import org.effective_mobile.task_management_system.database.repository.TaskRepository;
import org.effective_mobile.task_management_system.database.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.function.Supplier;

@Configuration
public class OpenApiConfigurations {
    @Bean @Profile("open-api-generation") public Supplier<EntityManager> entityManager() { return () -> null; }
    @Bean @Profile("open-api-generation") public Supplier<FilteredAndPagedTaskRepository> filteredAndPagedTaskRepository() { return () -> null; }
    @Bean @Profile("open-api-generation") public Supplier<CommentRepository> commentRepository() { return () -> null; }
    @Bean @Profile("open-api-generation") public Supplier<TaskRepository> taskRepository() { return () -> null; }
    @Bean @Profile("open-api-generation") public Supplier<UserRepository> userRepository() { return () -> null; }
    @Bean @Profile("open-api-generation") public CommentComponent commentComponent(Optional<CommentRepository> commentRepository) {
        return new CommentComponent(commentRepository.orElse(null));
    }

    @Bean @Profile("open-api-generation") public TaskComponent taskComponent(
        Optional<TaskRepository> taskRepository,
        Optional<FilteredAndPagedTaskRepository> filteredAndPagedTaskRepository
    ) {
        return new TaskComponentImpl(
            taskRepository.orElse(null),
            filteredAndPagedTaskRepository.orElse(null));
    }

    @Bean @Profile("open-api-generation") public UserComponent userComponent(
        Optional<UserRepository> userRepository,
        Optional<PasswordEncoder> passwordEncoder
    ) {
        return new UserComponent(
            userRepository.orElse(null),
            passwordEncoder.orElse(null));
    }
}
