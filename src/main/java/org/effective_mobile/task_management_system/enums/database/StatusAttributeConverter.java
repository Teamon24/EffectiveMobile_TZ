package org.effective_mobile.task_management_system.enums.database;

import jakarta.persistence.Converter;
import org.effective_mobile.task_management_system.enums.Status;

@Converter
public class StatusAttributeConverter extends EnumNameAttributeConverter<String, Status> {


    @Override
    protected Class<StatusAttributeConverter> getConverterClassName() {
        return StatusAttributeConverter.class;
    }

    @Override
    protected Class<Status> getEnumClassName() {
        return Status.class;
    }
}