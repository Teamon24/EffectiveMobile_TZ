package org.effective_mobile.task_management_system.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.effective_mobile.task_management_system.enums.UserRole;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter

@Entity
@Table(name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
    })
@ToString
public class User extends AbstractEntity {

    public void setId(Long id) {
        this.id = id;
    }

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    public List<UserRole> getRoleEnums() {
        return getRoles().stream().map(Role::getName).toList();
    }

    @Setter
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "creator_id")
    @ToString.Exclude
    private List<Task> tasks = new ArrayList<>();

    @Builder
    public User(String username, String email, String password, List<Role> roles, List<Task> tasks) {
        this.username = username;
        this.email = email;
        this.password = password;
        if (roles.isEmpty()) {
            this.roles = List.of(new Role(UserRole.USER));
        } else {
            this.roles = roles;
        }
        this.tasks = tasks;
    }
}