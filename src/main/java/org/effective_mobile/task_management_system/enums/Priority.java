package org.effective_mobile.task_management_system.enums;

import lombok.AllArgsConstructor;

/**
 * Статус задачи.
 */
@AllArgsConstructor
public enum Priority implements ValuableEnum<Integer> {
    LOW(1, "low"),
    AVERAGE(2, "average"),
    HIGH(3, "high");

    private final int numericRepresentation;
    private final String value;

    @Override
    public Integer getValue() {
        return numericRepresentation;
    }
}
