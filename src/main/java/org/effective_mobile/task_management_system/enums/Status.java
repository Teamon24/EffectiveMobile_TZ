package org.effective_mobile.task_management_system.enums;

/**
 * Статус задачи. */
public enum Status implements ValuableEnum<String> {
    NEW,
    ASSIGNED,
    EXECUTING,
    DONE,
    PENDING;

    @Override
    public String getValue() {
        return this.name();
    }
}
