package org.effective_mobile.task_management_system.validator.smart.task;

import org.apache.commons.lang3.StringUtils;
import org.effective_mobile.task_management_system.validator.smart.SimpleValidator;
import org.effective_mobile.task_management_system.validator.smart.ValuableEnumValidationComponent;
import org.effective_mobile.task_management_system.exception.messages.ValidationMessages;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.utils.enums.Priority;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

import static org.effective_mobile.task_management_system.validator.smart.SmartValidationUtils.rejectValue;
import static org.effective_mobile.task_management_system.exception.messages.ValidationMessages.invalidContent;
import static org.effective_mobile.task_management_system.utils.JsonPojos.Task.Field.CONTENT;

public class TaskCreationValidator extends SimpleValidator<TaskCreationRequestPojo> {

    private final ValuableEnumValidationComponent valuableEnumValidationComponent;

    public TaskCreationValidator(
        SmartValidator smartValidator,
        ValuableEnumValidationComponent valuableEnumValidationComponent
    ) {
        super(smartValidator);
        this.valuableEnumValidationComponent = valuableEnumValidationComponent;
    }

    @Override
    protected Class<TaskCreationRequestPojo> getValidatableClass() {
        return TaskCreationRequestPojo.class;
    }

    @Override
    public void simplyValidate(TaskCreationRequestPojo target, Errors errors, Object... validationHints) {
        String content = target.getContent();
        if (StringUtils.isBlank(content)) {
            rejectValue(CONTENT, content, errors, invalidContent(content));
        }

        String priority = target.getPriority();
        valuableEnumValidationComponent.validate(Priority.class, priority, errors, ValidationMessages::invalidPriority);
    }
}
