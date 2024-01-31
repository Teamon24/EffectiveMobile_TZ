package org.effective_mobile.task_management_system.validator.smart.task;

import org.apache.commons.lang3.StringUtils;
import org.effective_mobile.task_management_system.exception.messages.ValidationMessages;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.utils.JsonPojos;
import org.effective_mobile.task_management_system.validator.smart.SimpleValidator;
import org.effective_mobile.task_management_system.validator.smart.StringValueEnumValidationComponent;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

import static org.effective_mobile.task_management_system.exception.messages.ValidationMessages.invalidContent;
import static org.effective_mobile.task_management_system.utils.JsonPojos.Task.Field.CONTENT;
import static org.effective_mobile.task_management_system.validator.smart.SmartValidationUtils.rejectValue;

public class TaskCreationValidator extends SimpleValidator<TaskCreationRequestPojo> {

    private final StringValueEnumValidationComponent valuableEnumValidationComponent;

    public TaskCreationValidator(
        SmartValidator smartValidator,
        StringValueEnumValidationComponent valuableEnumValidationComponent
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
        valuableEnumValidationComponent.validate(
            JsonPojos.Task.Field.PRIORITY,
            priority,
            errors,
            ValidationMessages::invalidPriority);
    }
}
