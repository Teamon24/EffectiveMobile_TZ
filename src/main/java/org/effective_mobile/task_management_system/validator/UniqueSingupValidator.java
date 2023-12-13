package org.effective_mobile.task_management_system.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.springframework.beans.factory.annotation.Value;

public class UniqueSingupValidator implements ConstraintValidator<Signup, String> {

    private final UserComponent userComponent;

    @Value("${validation.error.signup.field}")
    private String fieldName;

    @Value("${validation.error.signup.value}")
    private String valueName;

    public UniqueSingupValidator(UserComponent userComponent) {
        this.userComponent = userComponent;
    }

    private Signup.Type fieldType;

    @Override
    public void initialize(Signup matching) {
        fieldType = matching.field();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Boolean isValid = switch (fieldType) {
            case USERNAME -> !userComponent.usernameExists(value);
            case EMAIL -> !userComponent.emailExists(value);
        };

        if (!isValid) {
            setUpMessage(value, context);
        }
        return isValid;
    }
    private HibernateConstraintValidatorContext setUpMessage(
        String value,
        ConstraintValidatorContext context
    ) {
        return ((ConstraintValidatorContextImpl) context)
            .addMessageParameter(fieldName, fieldType)
            .addMessageParameter(valueName, value);
    }
}
