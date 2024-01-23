package org.effective_mobile.task_management_system.validator.smart;

import jakarta.annotation.Nullable;
import org.effective_mobile.task_management_system.utils.enums.ValuableEnum;
import org.springframework.validation.Errors;

import java.util.function.Function;

/**
 * Smart-валидация значения {@link ValuableEnum#getValue()} для {@link ValuableEnum}.
 */
public interface ValuableEnumValidationComponent {
    <T extends ValuableEnum<String>> void validate(
        Class<T> valuableEnumClass,
        @Nullable String value,
        Errors errors,
        Function<String, String> defaultMessage
    );

    <T extends ValuableEnum<String>> void validate(
        Class<T> valuableEnumClass,
        @Nullable String value,
        Errors errors,
        String defaultMessage
    );
}
