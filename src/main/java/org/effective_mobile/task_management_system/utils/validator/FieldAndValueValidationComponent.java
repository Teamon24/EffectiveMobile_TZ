package org.effective_mobile.task_management_system.utils.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FieldAndValueValidationComponent {

    @Value("${validation.error.signup.invalid}")
    private String invalidValueTemplate;

    @Value("${validation.error.signup.field}")
    private String fieldParameterName;

    @Value("${validation.error.signup.value}")
    private String valueParameterName;

    HibernateConstraintValidatorContext setUpMessage(
        String fieldName,
        String value,
        ConstraintValidatorContext context
    ) {
        return ((ConstraintValidatorContextImpl) context)
            .addMessageParameter(fieldParameterName, fieldName)
            .addMessageParameter(valueParameterName, value);
    }

    void invalidValueMessage(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate(invalidValueTemplate)
            .addConstraintViolation();
    }
}
