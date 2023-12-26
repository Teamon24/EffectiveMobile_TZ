package org.effective_mobile.task_management_system.utils.enums.converter;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.effective_mobile.task_management_system.exception.ToEnumConvertException;
import org.effective_mobile.task_management_system.exception.messages.ExceptionMessages;
import org.effective_mobile.task_management_system.utils.enums.Priority;
import org.effective_mobile.task_management_system.utils.enums.ValuableEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @param <Enam> тип enum-константы, которая будет конвертирована.
 */
public abstract class EnumNameConverter<Enam extends Enum<Enam> & ValuableEnum<String>> {

    protected abstract Class<Enam> enumClass();

    public Enam convert(String value) {
        return getEnumOrDie(enumClass(), value);
    }

    /**
     * @param enumClass enum-класс, который реализует интерфейс {@link ValuableEnum}.
     * @param value     одно из значений enum-константы.
     * @return enum-константа (никогда null).
     * @throws ToEnumConvertException если не получается связать заданное значение с enum-константой.
     */
    public Enam getEnumOrDie(@NonNull final Class<Enam> enumClass, final String value)
        throws ToEnumConvertException {
        final Enam[] enumConstants = enumClass.getEnumConstants();
        for (Enam enumConstant : enumConstants) {
            final String enumValue = enumConstant.getValue();
            if (enumValue.equalsIgnoreCase(value)) return enumConstant;
        }

        String message = ExceptionMessages.getMessage(
            "exception.enum.conversion",
            value,
            enumClass().getSimpleName(),
            StringUtils.join(EnumNameConverter.names(enumClass()), ", ")
        );

        throw new ToEnumConvertException(message);
    }

    public static List<String> names(@NonNull final Class<? extends ValuableEnum<String>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants()).map(ValuableEnum::getValue).collect(Collectors.toList());
    }
}

