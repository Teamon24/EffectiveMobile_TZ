package org.effective_mobile.task_management_system.utils.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.effective_mobile.task_management_system.exception.ToEnumConvertException;
import org.effective_mobile.task_management_system.exception.messages.ExceptionMessages;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface ValuableEnum {

    @JsonValue
    <ValueType> ValueType getValue();

    default <ValueType> List<ValueType> getEnumValues() {
        return ImmutableList.of(getValue());
    }

    static <EnumType extends Enum<EnumType> & ValuableEnum, ValueType> EnumType convert(
        @NonNull final Class<EnumType> enumClass,
        @NonNull final ValueType value)
        throws ToEnumConvertException
    {
        final EnumType[] enumConstants = enumClass.getEnumConstants();
        for (EnumType enumConstant : enumConstants) {
            final List<ValueType> enumValues = enumConstant.getEnumValues();
            for (ValueType enumValue : enumValues) {
                if (enumValue instanceof String && value instanceof String) {
                    if (((String) enumValue).equalsIgnoreCase((String) value)) return enumConstant;
                } else {
                    if (enumValue.equals(value)) return enumConstant;
                }
            }
        }

        String message = ExceptionMessages.getMessage(
            "exception.enum.conversion",
            value,
            enumClass.getSimpleName(),
            StringUtils.join(names(enumClass), ", ")
        );

        throw new ToEnumConvertException(message);
    }

    static <T> List<T> values(Collection<? extends ValuableEnum> valuableEnums) {
        return valuableEnums
            .stream()
            .map(ValuableEnum::<T>getEnumValues)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    static <T> List<T> values(@NonNull final Class<? extends ValuableEnum> enumClass) {
        return Arrays
            .stream(enumClass.getEnumConstants())
            .map(ValuableEnum::<T>getEnumValues)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    static List<String> names(@NonNull final Class<? extends Enum<?>> enumClass) {
        return Arrays
            .stream(enumClass.getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.toList());
    }
}
