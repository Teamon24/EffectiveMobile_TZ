package org.effective_mobile.task_management_system.utils.enums.converter;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.effective_mobile.task_management_system.exception.ToEnumConvertException;
import org.effective_mobile.task_management_system.exception.messages.ExceptionMessages;
import org.effective_mobile.task_management_system.utils.enums.ValuableEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @param <Enum> тип enum-константы, которая будет конвертирована.
 */
public abstract class ValuableEnumConverter<Enum extends java.lang.Enum<Enum> & ValuableEnum<String>> {

    public abstract Class<Enum> enumClass();

    public abstract String getJsonPropertyName();

    public Enum convert(String value) throws ToEnumConvertException {
        return getEnumOrDie(enumClass(), value);
    }

    /**
     * @param enumClass enum-класс, который реализует интерфейс {@link ValuableEnum}.
     * @param value     одно из значений enum-константы.
     * @return enum-константа (никогда null).
     * @throws ToEnumConvertException если не получается связать заданное значение с enum-константой.
     */
    public Enum getEnumOrDie(@NonNull final Class<Enum> enumClass, final String value) throws ToEnumConvertException {
        final Enum[] enumConstants = enumClass.getEnumConstants();
        for (Enum enumConstant : enumConstants) {
            final String enumValue = enumConstant.getValue();
            if (enumValue.equalsIgnoreCase(value)) return enumConstant;
        }

        String message = ExceptionMessages.getMessage(
            "exception.enum.conversion",
            value,
            enumClass().getSimpleName(),
            StringUtils.join(ValuableEnumConverter.names(enumClass()), ", ")
        );

        throw new ToEnumConvertException(message);
    }

    public static <T> List<T> values(@NonNull final Class<? extends ValuableEnum<T>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants()).map(ValuableEnum::getValue).collect(Collectors.toList());
    }

    public static List<String> names(@NonNull final Class<? extends java.lang.Enum<?>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants()).map(java.lang.Enum::name).collect(Collectors.toList());
    }
}

