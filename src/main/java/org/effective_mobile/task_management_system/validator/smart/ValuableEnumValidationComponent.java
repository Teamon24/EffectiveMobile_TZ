package org.effective_mobile.task_management_system.validator.smart;

import jakarta.annotation.Nullable;
import org.effective_mobile.task_management_system.utils.enums.ValuableEnum;
import org.springframework.validation.Errors;

import java.util.function.Function;

/**
 * Smart-валидация значения {@link ValuableEnum#getValue()} для {@link ValuableEnum}.
 */
public interface ValuableEnumValidationComponent<ValueType> {
    void validate(
        String fieldName,
        @Nullable ValueType fieldValue,
        Errors errors,
        Function<ValueType, String> defaultMessage
    );

    void validate(
        String fieldName,
        @Nullable ValueType fieldValue,
        Errors errors,
        String defaultMessage
    );
}
