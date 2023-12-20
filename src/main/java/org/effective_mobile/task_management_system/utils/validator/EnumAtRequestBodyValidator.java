package org.effective_mobile.task_management_system.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.effective_mobile.task_management_system.exception.ToEnumConvertException;
import org.effective_mobile.task_management_system.utils.enums.Priority;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.effective_mobile.task_management_system.utils.enums.ValuableEnum;
import org.effective_mobile.task_management_system.utils.enums.converter.EnumNameConverter;
import org.effective_mobile.task_management_system.utils.enums.converter.PriorityConverter;
import org.effective_mobile.task_management_system.utils.enums.converter.StatusConverter;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class EnumAtRequestBodyValidator implements ConstraintValidator<ValidEnum, String> {

    @Value("${validation.error.enum.invalid.wrongValue}")
    private String wrongValueParam;

    @Value("${validation.error.enum.invalid.enum}")
    private String enumParam;

    @Value("${validation.error.enum.invalid.acceptableValues}")
    private String acceptableValuesParam;

    private Class<? extends ValuableEnum<String>> enumClass;

    private final Map<?, EnumNameConverter<?>> converters = new HashMap<>() {{
        put(Status.class, new StatusConverter());
        put(Priority.class, new PriorityConverter());
    }};

    @Override
    public void initialize(ValidEnum matching) {
        this.enumClass = matching.clazz();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        EnumNameConverter<?> enumNameConverter = converters.get(this.enumClass);
        try {
            enumNameConverter.convert(value);
        } catch (ToEnumConvertException e) {
            createMessage(value, (ConstraintValidatorContextImpl) context);
            return false;
        }
        return true;
    }

    private void createMessage(String value, ConstraintValidatorContextImpl context) {
        context
            .addMessageParameter(wrongValueParam, StringUtils.defaultIfEmpty(value, "<empty>"))
            .addMessageParameter(enumParam, enumClass.getSimpleName())
            .addMessageParameter(acceptableValuesParam, StringUtils.join(EnumNameConverter.names(enumClass), ", "));
    }
}
