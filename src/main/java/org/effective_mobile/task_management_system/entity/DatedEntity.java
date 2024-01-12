package org.effective_mobile.task_management_system.entity;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;

import java.util.Date;

import static org.effective_mobile.task_management_system.entity.Naming.CREATED_AT;
import static org.effective_mobile.task_management_system.entity.Naming.DELETED_AT;
import static org.effective_mobile.task_management_system.entity.Naming.UPDATED_AT;

@Getter
@MappedSuperclass
public abstract class DatedEntity extends HasLongId implements HasCreatedAt, HasUpdatedAt, HasDeletedAt {

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdAt;

    @Column(name = UPDATED_AT, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date updatedAt;

    @Column(name = DELETED_AT, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    @PreDestroy
    protected void onDeleted() {
        this.deletedAt = new Date();
    }
}
