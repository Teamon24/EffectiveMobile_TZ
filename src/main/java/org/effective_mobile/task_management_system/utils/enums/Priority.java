package org.effective_mobile.task_management_system.utils.enums;

import org.effective_mobile.task_management_system.exception.ToEnumConvertException;

/**
 * Статус задачи. */
public enum Priority implements ValuableEnum {
    LOW,
    AVERAGE,
    HIGH;

    @Override
    public String getValue() {
        return this.name();
    }

    public static Priority convert(String value) throws ToEnumConvertException {
        return ValuableEnum.convert(Priority.class, value);
    }
}
