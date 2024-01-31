package org.effective_mobile.task_management_system.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.utils.enums.UserRole;
import org.effective_mobile.task_management_system.utils.enums.converter.attribute.UserRoleAttributeConverter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends AbstractEntity {

    @Column(length = 40, nullable = false, unique = true)
    @Convert(converter = UserRoleAttributeConverter.class)
    private UserRole name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "roles_privileges",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "privilege_id"))
    private List<Privilege> privileges = new ArrayList<>();

}
