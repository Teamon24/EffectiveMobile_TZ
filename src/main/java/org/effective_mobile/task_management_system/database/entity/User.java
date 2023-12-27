package org.effective_mobile.task_management_system.database.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@NoArgsConstructor
public class User extends AbstractEntity {

    @NotBlank
    @Size(max = 20)
    private String username;

    @Email
    @NotBlank
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @Setter
    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.DETACH} )
    @JoinColumn(name = "creator_id")
    @ToString.Exclude
    private List<Task> tasks = new ArrayList<>();

    @Builder
    private User(String username, String email, String password, List<Task> tasks) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.tasks = tasks;
    }
}