package org.effective_mobile.task_management_system.utils.enums.converter;

import org.effective_mobile.task_management_system.utils.enums.Status;

public class StatusConverter extends EnumNameConverter<Status> {
    @Override
    protected Class<Status> enumClass() {
        return Status.class;
    }
}
