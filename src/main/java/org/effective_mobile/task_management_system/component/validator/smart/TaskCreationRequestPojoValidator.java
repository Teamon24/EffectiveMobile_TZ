package org.effective_mobile.task_management_system.component.validator.smart;

import org.effective_mobile.task_management_system.exception.ToEnumConvertException;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.utils.enums.converter.PriorityConverter;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

public class TaskCreationRequestPojoValidator extends SimpleValidator<TaskCreationRequestPojo> {

    private final PriorityConverter priorityConverter = new PriorityConverter();

    public TaskCreationRequestPojoValidator(SmartValidator smartValidator) {
        super(smartValidator);
    }

    @Override
    protected Class<TaskCreationRequestPojo> getValidatableClass() {
        return TaskCreationRequestPojo.class;
    }

    @Override
    public void simplyValidate(TaskCreationRequestPojo target, Errors errors, Object... validationHints) {
        String content = target.getContent();
        if (content != null) {
            if (content.isBlank()) {
                errors.rejectValue(TaskCreationRequestPojo.CONTENT_FIELD_NAME, "blankContent");
            }
        }

        String priority = target.getPriority();

        if (priority != null) {
            if (priority.isBlank()) {
                reject(priority, errors, "blankPriority");
            } else {
                try {
                    priorityConverter.convert(priority);
                } catch (ToEnumConvertException e) {
                    reject(priority, errors, "invalidPriority");
                }
            }
        }
    }

    private void reject(String priority, Errors errors, String invalidPriority) {
        String message = ValidationMessages.invalidPriority(priority);
        errors.rejectValue(TaskCreationRequestPojo.PRIORITY_FIELD_NAME, invalidPriority, message);
    }
}
