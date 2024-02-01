package org.effective_mobile.task_management_system.database.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.effective_mobile.task_management_system.pojo.HasUserInfo;
import org.effective_mobile.task_management_system.utils.constraints.length.user;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends AbstractEntity implements HasUserInfo {

    @NotBlank
    @Size(min = user.username.MIN, max = user.username.MAX)
    @Column(unique = true, nullable = false, length = user.username.MAX)
    private String username;

    @Email
    @NotBlank
    @Size(min = user.email.MIN, max = user.email.MAX)
    @Column(unique = true, nullable = false, length = user.email.MAX)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    @Setter
    @OneToMany(cascade = { CascadeType.REMOVE })
    @JoinColumn(name = "creator_id")
    @ToString.Exclude
    private List<Task> tasks = new ArrayList<>();

    @Builder
    private User(String username, String email, String password, List<Role> roles, List<Task> tasks) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.tasks = tasks;
        this.roles = roles;
    }
}