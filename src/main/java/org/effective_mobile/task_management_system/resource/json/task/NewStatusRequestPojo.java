package org.effective_mobile.task_management_system.resource.json.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.resource.json.JsonPojo;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.effective_mobile.task_management_system.utils.validator.ValidEnum;

@NoArgsConstructor
@AllArgsConstructor
public class NewStatusRequestPojo implements JsonPojo {

    @Getter
    @ValidEnum(clazz = Status.class)
    private String newStatus;
}
