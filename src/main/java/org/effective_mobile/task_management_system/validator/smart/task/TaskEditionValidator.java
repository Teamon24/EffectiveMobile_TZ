package org.effective_mobile.task_management_system.validator.smart.task;

import org.apache.commons.lang3.ObjectUtils;
import org.effective_mobile.task_management_system.validator.smart.SimpleValidator;
import org.effective_mobile.task_management_system.validator.smart.ValuableEnumValidationComponent;
import org.effective_mobile.task_management_system.exception.messages.ValidationMessages;
import org.effective_mobile.task_management_system.resource.json.task.TaskEditionRequestPojo;
import org.effective_mobile.task_management_system.utils.enums.Priority;
import org.effective_mobile.task_management_system.validator.smart.SmartValidationUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

import static org.effective_mobile.task_management_system.exception.messages.ValidationMessages.invalidContent;
import static org.effective_mobile.task_management_system.resource.JsonPojos.Task.Field.CONTENT;

public class TaskEditionValidator extends SimpleValidator<TaskEditionRequestPojo> {

    private final ValuableEnumValidationComponent valuableEnumValidationComponent;

    public TaskEditionValidator(
        SmartValidator smartValidator,
        ValuableEnumValidationComponent valuableEnumValidationComponent
    ) {
        super(smartValidator);
        this.valuableEnumValidationComponent = valuableEnumValidationComponent;
    }

    @Override
    protected Class<TaskEditionRequestPojo> getValidatableClass() {
        return TaskEditionRequestPojo.class;
    }

    @Override
    public void simplyValidate(TaskEditionRequestPojo target, Errors errors, Object... validationHints) {
        String content = target.getContent();
        String priority = target.getPriority();

        if (ObjectUtils.allNull(content, priority)) {
            String defaultMessage = ValidationMessages.emptyBody();
            SmartValidationUtils.rejectValue(CONTENT, null, errors, defaultMessage);
            valuableEnumValidationComponent.validate(Priority.class, null, errors, defaultMessage);
            return;
        }

        if (content != null) {
            if (content.isBlank()) {
                SmartValidationUtils.rejectValue(CONTENT, content, errors, invalidContent(content));
            }
        }

        if (priority != null) {
            valuableEnumValidationComponent.validate(
                Priority.class, priority, errors, ValidationMessages::invalidPriority);
        }
    }
}
