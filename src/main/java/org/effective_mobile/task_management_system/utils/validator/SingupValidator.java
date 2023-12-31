package org.effective_mobile.task_management_system.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.effective_mobile.task_management_system.component.UserComponent;

public class SingupValidator implements ConstraintValidator<Signup, String> {

    private final UserComponent userComponent;
    private final FieldAndValueValidationComponent validationComponent;

    public SingupValidator(
        UserComponent userComponent,
        FieldAndValueValidationComponent validationComponent
    ) {
        this.userComponent = userComponent;
        this.validationComponent = validationComponent;
    }

    private Signup.Type fieldName = null;

    @Override
    public void initialize(Signup matching) {
        fieldName = matching.field();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            validationComponent.invalidValueMessage(context);
            return false;
        }

        Boolean isValid = switch (fieldName) {
            case USERNAME -> !userComponent.usernameExists(value);
            case EMAIL -> !userComponent.emailExists(value);
        };

        if (!isValid) {
            validationComponent.setUpMessage(fieldName.name(), value, context);
        }
        return isValid;
    }
}
