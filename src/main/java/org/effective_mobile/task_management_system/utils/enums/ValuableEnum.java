package org.effective_mobile.task_management_system.utils.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @param <ValueType> тип значения enum-костанты.
 */
public interface ValuableEnum<ValueType> {

    /**
     * @return обычное представление текущей enum-константы.
     */
    @JsonValue
    ValueType getValue();
}
