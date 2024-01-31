package org.effective_mobile.task_management_system.utils.enums;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.exception.ToEnumConvertException;

@AllArgsConstructor
public enum UserRole implements ValuableEnum {
    CREATOR(0, "creator"),
    EXECUTOR(1, "executor");

    private final Integer order;
    private final String value;

    @Override
    public String getValue() {
        return value;
    }

    public static UserRole convert(String value) throws ToEnumConvertException {
        return ValuableEnum.convert(UserRole.class, value);
    }
}
