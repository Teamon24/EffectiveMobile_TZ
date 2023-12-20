package org.effective_mobile.task_management_system.pojo.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.enums.Status;
import org.effective_mobile.task_management_system.validator.ValidEnum;

@NoArgsConstructor
@AllArgsConstructor
public class NewStatusRequest {

    @Getter
    @ValidEnum(clazz = Status.class)
    @JsonProperty(value = "new_status")
    private String newStatus;
}
