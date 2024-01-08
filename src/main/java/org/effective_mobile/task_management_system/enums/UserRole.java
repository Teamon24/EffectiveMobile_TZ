package org.effective_mobile.task_management_system.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRole implements ValuableEnum<String> {
    CREATOR(0, "creator"),
    EXECUTOR(1, "executor");


    private final Integer order;
    private final String value;

    @Override
    public String getValue() {
        return value;
    }
}
