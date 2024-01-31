package org.effective_mobile.task_management_system.utils.enums.converter.attribute;

import jakarta.persistence.AttributeConverter;
import org.effective_mobile.task_management_system.utils.enums.ValuableEnum;


public abstract class
ValuableEnumAttributeConverter<E extends Enum<E> & ValuableEnum, ValueType> implements AttributeConverter<E, ValueType>
{
    abstract Class<E> enumClass();

    protected ValueType toDatabaseColumn(E attribute) {
        return attribute.getValue();
    }

    protected E toEntityAttribute(ValueType dbValue) {
        return ValuableEnum.convert(enumClass(), dbValue);
    }

    @Override
    public final ValueType convertToDatabaseColumn(E attribute) {
        if (attribute == null)
            return null;

        ValueType value = toDatabaseColumn(attribute);
        if (value == null) {
            throw new IllegalArgumentException(
                "There is no database value for '%s#%s'".formatted(enumClass().getName(), attribute)
            );
        }
        return value;
    }

    @Override
    public final E convertToEntityAttribute(ValueType dbValue) {
        if (dbValue == null)
            return null;
 
        E valuableEnum = toEntityAttribute(dbValue);
        if (valuableEnum == null) {
            throw new IllegalArgumentException(
                "There is no '%s' for database value '%s'".formatted(enumClass().getName(), dbValue)
            );
        }
        return valuableEnum;
    }
 
}