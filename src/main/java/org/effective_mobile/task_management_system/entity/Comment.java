package org.effective_mobile.task_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Entity
@Table(name = "comment")
@NoArgsConstructor
public class Comment extends DatedEntity {

    @Setter
    @Size(min = 2, max = 510)
    @Column(length = 510)
    private String content;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Comment(
        String content,
        User user,
        Task task
    ) {
        this.content = content;
        this.user = user;
        this.task = Objects.requireNonNull(task);
    }

    @PostLoad
    public void postConstruct() {
        // TODO: сделать так, чтобы комментарий удаленного пользователя корректно обрабатывался, т.е.
        // если пользователь удален, то отдавать вместо имя пользователя
        // информацию об отсутствии пользователя в системе
        this.user = Objects.requireNonNullElse(
            this.getUser(),
            User.builder().username("Пользователь удален").build()
        );
    }
}