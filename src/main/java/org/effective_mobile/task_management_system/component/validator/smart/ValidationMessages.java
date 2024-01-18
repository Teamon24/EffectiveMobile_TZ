package org.effective_mobile.task_management_system.component.validator.smart;

import org.apache.commons.lang3.StringUtils;
import org.effective_mobile.task_management_system.exception.messages.ExceptionMessages;
import org.effective_mobile.task_management_system.utils.enums.Priority;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.effective_mobile.task_management_system.utils.enums.ValuableEnum;
import org.effective_mobile.task_management_system.utils.enums.converter.EnumNameConverter;

public class ValidationMessages {

    public static String invalidPriority(String priority) {
        return invalid(priority, Priority.class, "validation.error.enum.invalid");
    }

    public static String invalidStatus(String status) {
        return invalid(status, Status.class, "validation.error.enum.invalid");
    }

    public static String invalid(String value, Class<? extends ValuableEnum<String>> enumClass, String templateKey) {
        return ExceptionMessages.getMessage(
                templateKey,
                resolveValue(value),
                enumClass,
                StringUtils.join(EnumNameConverter.values(enumClass), ", ")
            );
    }

    private static String resolveValue(String value) {
        if (value == null) return "<NUll>";
        if (value.isEmpty()) return "<EMPTY>";
        if (value.isBlank()) return "<BLANK>";
        return value;
    }

}
