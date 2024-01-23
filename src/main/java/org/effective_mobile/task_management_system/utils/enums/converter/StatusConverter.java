package org.effective_mobile.task_management_system.utils.enums.converter;

import org.effective_mobile.task_management_system.utils.enums.Status;

public class StatusConverter extends ValuableEnumConverter<Status> {
    @Override
    public Class<Status> enumClass() {
        return Status.class;
    }
}
