package org.effective_mobile.task_management_system.enums.database;

import jakarta.persistence.AttributeConverter;
import org.effective_mobile.task_management_system.enums.ValuableEnum;

public abstract class EnumNameAttributeConverter<V, E extends ValuableEnum<V>> implements AttributeConverter<E, V> {
    private final String SIMPLE_NAME = getConverterClassName().getSimpleName();
    private final String ENUM_SIMPLE_NAME = getEnumClassName().getSimpleName();

    private static final String TO_DB_ERROR_MESSAGE =
        "%s: there should not be 'null' value at the database for %s";

    private static final String TO_ENUM_ERROR_MESSAGE =
        "%s: there should not have been 'null' value at the database for %s";

    protected abstract <C extends EnumNameAttributeConverter<V, E>> Class<C> getConverterClassName();

    protected abstract Class<E> getEnumClassName();

    @Override
    public V convertToDatabaseColumn(E attribute) {
        if (attribute == null) {
            throw new RuntimeException(format(TO_DB_ERROR_MESSAGE));
        }
        return attribute.getValue();
    }

    @Override
    public E convertToEntityAttribute(V dbValue) {
        if (dbValue == null) {
            throw new RuntimeException(format(TO_ENUM_ERROR_MESSAGE));
        }

        E[] userRoles = this.getEnumClassName().getEnumConstants();

        E found = null;
        for (E userRole : userRoles) {
            if (userRole.getValue().equals(dbValue)) {
                found = userRole;
            }
        }
        if (found == null) {
            throw new IllegalArgumentException(
                "%s: there is no case for %s value '%s'".formatted(
                    SIMPLE_NAME,
                    ENUM_SIMPLE_NAME,
                    dbValue)
            );
        }
        return found;
    }

    private String format(String template) {
        return template.formatted(getConverterClassName(), getEnumClassName());
    }

}
