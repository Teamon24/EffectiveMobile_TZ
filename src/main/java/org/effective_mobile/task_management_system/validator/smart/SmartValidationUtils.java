package org.effective_mobile.task_management_system.validator.smart;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

public class SmartValidationUtils {
    public static String resolveErrorCode(String fieldName, String fieldValue) {
        String capitalizedFieldName = StringUtils.capitalize(fieldName);
        if (fieldValue == null)   return "null_" + capitalizedFieldName;
        if (fieldValue.isEmpty()) return "empty_" + capitalizedFieldName;
        if (fieldValue.isBlank()) return "blank_" + capitalizedFieldName;
        return "invalid_" + fieldValue;
    }

    public static void rejectValue(
        String fieldName,
        String fieldValue,
        Errors errors,
        String defaultMessage
    ) {
        String errorCode = resolveErrorCode(fieldName, fieldValue);
        errors.rejectValue(fieldName, errorCode, defaultMessage);
    }
}
