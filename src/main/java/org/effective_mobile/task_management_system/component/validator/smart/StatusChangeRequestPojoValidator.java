package org.effective_mobile.task_management_system.component.validator.smart;

import org.effective_mobile.task_management_system.exception.ToEnumConvertException;
import org.effective_mobile.task_management_system.resource.json.task.StatusChangeRequestPojo;
import org.effective_mobile.task_management_system.utils.enums.converter.StatusConverter;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

import static org.effective_mobile.task_management_system.resource.json.task.StatusChangeRequestPojo.STATUS_FIELD_NAME;

public class StatusChangeRequestPojoValidator extends SimpleValidator<StatusChangeRequestPojo> {

    private final StatusConverter statusConverter = new StatusConverter();

    public StatusChangeRequestPojoValidator(SmartValidator smartValidator) {
        super(smartValidator);
    }

    @Override
    protected Class<StatusChangeRequestPojo> getValidatableClass() {
        return StatusChangeRequestPojo.class;
    }

    @Override
    public void simplyValidate(StatusChangeRequestPojo target, Errors errors, Object... validationHints) {
        String status = target.getStatus();
        if (status == null || status.isBlank()) {
            reject(errors, resolveErrorCode(status), status);
            return;
        }

        try {
            statusConverter.convert(status);
        } catch (ToEnumConvertException e) {
            reject(errors, "invalidStatus", status);
        }
    }

    private String resolveErrorCode(String value) {
        if (value == null) return "nullStatus";
        if (value.isEmpty()) return "emptyStatus";
        if (value.isBlank()) return "blankStatus";
        return value;
    }

    private void reject(Errors errors, String blankStatus, String status) {
        String message = ValidationMessages.invalidStatus(status);
        errors.rejectValue(STATUS_FIELD_NAME, blankStatus, message);
    }
}
