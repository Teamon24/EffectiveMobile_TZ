package org.effective_mobile.task_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.enums.UserRole;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role extends HasLongId {

    @Getter
    @Setter
    @Column(length = 40, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole name;
}
