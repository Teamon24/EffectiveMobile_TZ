package org.effective_mobile.task_management_system.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {

    public void initialize(NullOrNotBlank parameters) {}

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || !value.isBlank();
    }
}
