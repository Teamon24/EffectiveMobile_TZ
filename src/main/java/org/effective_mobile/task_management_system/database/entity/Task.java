package org.effective_mobile.task_management_system.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.utils.enums.Priority;
import org.effective_mobile.task_management_system.utils.enums.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Сущность задания, создаваемого пользователем.
 * Пользователи могут управлять своими задачами:
 * создавать новые,
 * редактировать существующие,
 * просматривать и удалять,
 * менять статус и
 * назначать исполнителей задачи.
 * Пользователи могут просматривать задачи других пользователей, а исполнители задачи могут менять статус своих задач.
 */
@NoArgsConstructor
@Getter
@Entity
@Table(name = "tasks")
public class Task extends AbstractEntity {

    @Setter
    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Priority priority;

    @Setter
    @Column
    private String content;

    // TODO: избавиться от FetchType.EAGER (сейчас связи подтягиваются из-за spring-кеша).
    // https://stackoverflow.com/questions/35997541/getting-org-hibernate-lazyinitializationexception-exceptions-after-retrieving
    @Setter
    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;

    // TODO: избавиться от FetchType.EAGER (сейчас связи подтягиваются из-за spring-кеша).
    @NotNull
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Setter
    @OneToMany
    @JoinColumn(name = "task_id")
    private List<Comment> comments = new ArrayList<>();

    @Builder
    private Task(
        Priority priority,
        Status status,
        String content,
        User creator,
        User executor,
        List<Comment> comments
    ) {
        this.priority = Objects.requireNonNullElse(priority, Priority.LOW);
        this.status = Objects.requireNonNullElse(status, Status.NEW);
        this.content = content;
        this.executor = executor;
        this.creator = Objects.requireNonNull(creator);
        this.comments = comments;
    }
}