package org.effective_mobile.task_management_system.enums.database;

import jakarta.persistence.Converter;
import org.effective_mobile.task_management_system.enums.Priority;

@Converter
public class PriorityAttributeConverter extends EnumNameAttributeConverter<Integer, Priority> {


    @Override
    protected Class<PriorityAttributeConverter> getConverterClassName() {
        return PriorityAttributeConverter.class;
    }

    @Override
    protected Class<Priority> getEnumClassName() {
        return Priority.class;
    }
}