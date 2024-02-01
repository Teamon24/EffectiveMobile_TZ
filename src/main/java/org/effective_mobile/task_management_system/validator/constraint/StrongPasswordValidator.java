package org.effective_mobile.task_management_system.validator.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.utils.constraints.length.user.password;

@AllArgsConstructor
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    /**
     * Regex, который проверяет строку на наличие:
     * <p><strong> (?=.*\d)</strong>: минимум одна цифра
     * <p><strong> (?=.*[a-z])</strong>: минимум одна прописная буква
     * <p><strong> (?=.*[A-Z])</strong>: минимум одна заглавная буква
     * <p><strong> (?=.*[@#$%^&+=!*()])</strong>: минимум один спец. символ
     * <p><strong> .{a,}</strong>: минимум a символов.
     */
    private static final String SPECIAL_SYMBOLS_PART = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()])";
    private static final String MIN_LENGTH_PART = "{%s,}";

    public static final String STRONG_PASSWORD_REGEX =
        "^" + SPECIAL_SYMBOLS_PART + "." +  MIN_LENGTH_PART.formatted(password.MIN) + "$";

    private FieldAndValueValidationComponent validationComponent;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            validationComponent.invalidValueMessage(context);
            return false;
        }
        return value.matches(STRONG_PASSWORD_REGEX);
    }
}