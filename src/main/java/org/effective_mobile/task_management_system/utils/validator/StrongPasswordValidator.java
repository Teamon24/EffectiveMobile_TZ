package org.effective_mobile.task_management_system.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    /**
     * Regex, который проверяет строку на наличие:
     * <p><strong> (?=.*\d)</strong>: минимум одна цифра
     * <p><strong> (?=.*[a-z])</strong>: минимум одна прописная буква
     * <p><strong> (?=.*[A-Z])</strong>: минимум одна заглавная буква
     * <p><strong> (?=.*[@#$%^&+=!*()])</strong>: минимум один спец. символ
     * <p><strong> .{8,}</strong>: минимум 8 символов.
     */
    public static final String STRONG_PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()]).{8,}$";

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