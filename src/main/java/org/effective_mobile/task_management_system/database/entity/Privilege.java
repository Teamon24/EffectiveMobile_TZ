package org.effective_mobile.task_management_system.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "privileges")
@Builder
public class Privilege extends AbstractEntity {

    @NotNull
    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "parent_id")
    private Long parentId;
}
