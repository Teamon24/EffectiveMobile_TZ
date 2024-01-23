package org.effective_mobile.task_management_system.utils.enums.converter;

import org.effective_mobile.task_management_system.utils.enums.Priority;

public class PriorityConverter extends ValuableEnumConverter<Priority> {

    @Override
    public Class<Priority> enumClass() {
        return Priority.class;
    }
}
