package org.effective_mobile.task_management_system.validator.smart;

import jakarta.annotation.Nullable;
import org.springframework.validation.Errors;

import java.util.function.Function;

public interface StringValueEnumValidationComponent extends ValuableEnumValidationComponent<String> {
    void validate(
        String fieldName,
        @Nullable String fieldValue,
        Errors errors,
        Function<String, String> defaultMessage
    );

    void validate(
        String fieldName,
        @Nullable String fieldValue,
        Errors errors,
        String defaultMessage
    );
}
