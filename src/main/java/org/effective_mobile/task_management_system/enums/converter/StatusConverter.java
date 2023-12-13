package org.effective_mobile.task_management_system.enums.converter;

import org.effective_mobile.task_management_system.enums.Status;

public class StatusConverter extends EnumNameConverter<Status> {
    @Override
    protected Class<Status> enumClass() {
        return Status.class;
    }
}
