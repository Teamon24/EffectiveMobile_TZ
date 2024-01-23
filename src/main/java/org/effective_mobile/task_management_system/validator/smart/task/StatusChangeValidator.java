package org.effective_mobile.task_management_system.validator.smart.task;

import org.effective_mobile.task_management_system.exception.messages.ValidationMessages;
import org.effective_mobile.task_management_system.resource.json.task.StatusChangeRequestPojo;
import org.effective_mobile.task_management_system.utils.JsonPojos;
import org.effective_mobile.task_management_system.validator.smart.SimpleValidator;
import org.effective_mobile.task_management_system.validator.smart.ValuableEnumValidationComponent;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

public class StatusChangeValidator extends SimpleValidator<StatusChangeRequestPojo> {

    private final ValuableEnumValidationComponent valuableEnumValidationComponent;

    public StatusChangeValidator(
        SmartValidator smartValidator,
        ValuableEnumValidationComponent valuableEnumValidationComponent
    ) {
        super(smartValidator);
        this.valuableEnumValidationComponent = valuableEnumValidationComponent;
    }

    @Override
    protected Class<StatusChangeRequestPojo> getValidatableClass() {
        return StatusChangeRequestPojo.class;
    }

    @Override
    public void simplyValidate(StatusChangeRequestPojo target, Errors errors, Object... validationHints) {
        valuableEnumValidationComponent.validate(
            JsonPojos.Task.Field.STATUS,
            target.getStatus(),
            errors,
            ValidationMessages::invalidStatus);
    }
}
