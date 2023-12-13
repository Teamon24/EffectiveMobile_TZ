package org.effective_mobile.task_management_system.enums.converter;

import org.effective_mobile.task_management_system.enums.Priority;

public class PriorityConverter extends EnumNameConverter<Priority> {
    @Override
    protected Class<Priority> enumClass() {
        return Priority.class;
    }
}
