package org.effective_mobile.task_management_system.resource.json.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.component.validator.ValidEnum;
import org.effective_mobile.task_management_system.resource.json.RequestPojo;
import org.effective_mobile.task_management_system.utils.enums.Status;

@NoArgsConstructor
@AllArgsConstructor
public class StatusChangeRequestPojo implements RequestPojo {

    @Getter
    @ValidEnum(clazz = Status.class)
    private String newStatus;
}
