package org.effective_mobile.task_management_system.validator.smart;

import org.apache.commons.lang3.StringUtils;
import org.effective_mobile.task_management_system.exception.ToEnumConvertException;
import org.effective_mobile.task_management_system.utils.enums.ValuableEnum;
import org.effective_mobile.task_management_system.utils.enums.converter.PriorityConverter;
import org.effective_mobile.task_management_system.utils.enums.converter.StatusConverter;
import org.effective_mobile.task_management_system.utils.enums.converter.ValuableEnumConverter;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

@Component
public class ValuableEnumValidationComponentImpl implements ValuableEnumValidationComponent {

    private final String template = "There is no converter for enum class: '%s'";

    private final Collection<ValuableEnumConverter<? extends ValuableEnum<String>>> converters =
        new ArrayList<>() {{
            add(new PriorityConverter());
            add(new StatusConverter());
        }};


    @Override
    public <T extends ValuableEnum<String>> void validate(
        Class<T> valuableEnumClass,
        String value,
        Errors errors,
        Function<String, String> defaultMessage
    ) {
        ValuableEnumConverter<? extends ValuableEnum<String>> converter = getConverter(valuableEnumClass);

        if (StringUtils.isBlank(value)) {
            SmartValidationUtils.rejectValue(converter.getJsonPropertyName(), value, errors, defaultMessage.apply(value));
        } else {
            try {
                converter.convert(value);
            } catch (ToEnumConvertException e) {
                SmartValidationUtils.rejectValue(converter.getJsonPropertyName(), value, errors, defaultMessage.apply(value));
            }
        }
    }

    @Override
    public <T extends ValuableEnum<String>> void validate(
        Class<T> valuableEnumClass,
        String value,
        Errors errors,
        String defaultMessage
    ) {
        validate(valuableEnumClass, value, errors, (ignored) -> defaultMessage);
    }

    private <T extends ValuableEnum<String>> ValuableEnumConverter<? extends ValuableEnum<String>> getConverter(
        Class<T> tClass
    ) {
        return converters.stream()
            .filter(it -> it.enumClass() == tClass)
            .findFirst()
            .orElseThrow(() -> new RuntimeException(String.format(template, tClass)));
    }
}
