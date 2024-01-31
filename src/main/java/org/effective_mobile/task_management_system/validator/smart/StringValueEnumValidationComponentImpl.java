package org.effective_mobile.task_management_system.validator.smart;

import org.apache.commons.lang3.StringUtils;
import org.effective_mobile.task_management_system.exception.ToEnumConvertException;
import org.effective_mobile.task_management_system.utils.enums.Priority;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.effective_mobile.task_management_system.utils.enums.ValuableEnum;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
public class StringValueEnumValidationComponentImpl implements StringValueEnumValidationComponent {

    private final String template = "There is no converter for enum class: '%s'";

    private final Map<Class, Function<String, ValuableEnum>> converters =
        new HashMap<>() {{
            put(Priority.class, Priority::convert);
            put(Status.class, Status::convert);
        }};


    @Override
    public void validate(
        String fieldName,
        String fieldValue,
        Errors errors,
        Function<String, String> defaultMessage
    ) {
        Class enumClass = ValuableEnumByJsonPropertyNames.get(fieldName);
        Function<String, ValuableEnum> converter = getConverter(enumClass);
        if (StringUtils.isBlank(fieldValue)) {
            SmartValidationUtils.rejectValue(fieldName, fieldValue, errors, defaultMessage.apply(fieldValue));
        } else {
            try {
                converter.apply(fieldValue);
            } catch (ToEnumConvertException e) {
                SmartValidationUtils.rejectValue(fieldName, fieldValue, errors, defaultMessage.apply(fieldValue));
            }
        }
    }

    @Override
    public void validate(
        String fieldName,
        String fieldValue,
        Errors errors,
        String defaultMessage
    ) {
        validate(fieldName, fieldValue, errors, (ignored) -> defaultMessage);
    }

    private <T extends ValuableEnum> Function<?, ValuableEnum> getConverter(
        Class<T> tClass
    ) {
        return Optional
            .ofNullable(converters.get(tClass))
            .orElseThrow(() -> new RuntimeException(String.format(template, tClass)));
    }
}
