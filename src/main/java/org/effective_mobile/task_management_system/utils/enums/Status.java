package org.effective_mobile.task_management_system.utils.enums;

import org.effective_mobile.task_management_system.exception.ToEnumConvertException;

/**
 * Статус задачи. */
public enum Status implements ValuableEnum {
    NEW,
    ASSIGNED,
    EXECUTING,
    DONE,
    PENDING;

    @Override
    public String getValue() {
        return this.name();
    }

    public static Status convert(String value) throws ToEnumConvertException {
        return ValuableEnum.convert(Status.class, value);
    }
}
