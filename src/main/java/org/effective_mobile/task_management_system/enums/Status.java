package org.effective_mobile.task_management_system.enums;

import lombok.AllArgsConstructor;

/**
 * Статус задачи.
 */
@AllArgsConstructor
public enum Status implements ValuableEnum<String> {
    NEW(0, "new"),
    ASSIGNED(1, "assigned"),
    EXECUTING(2, "executing"),
    DONE(3, "done");

    private final int numericRepresentation;
    private final String value;

    @Override
    public String getValue() {
        return value;
    }
}
