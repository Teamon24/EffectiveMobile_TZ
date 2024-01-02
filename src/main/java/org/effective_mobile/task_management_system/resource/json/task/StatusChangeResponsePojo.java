package org.effective_mobile.task_management_system.resource.json.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.resource.json.ResponsePojo;
import org.effective_mobile.task_management_system.utils.enums.Status;

@NoArgsConstructor
@AllArgsConstructor
public class StatusChangeResponsePojo implements ResponsePojo {
    @Getter
    @JsonProperty
    private Long taskId;

    @Getter
    @JsonProperty
    private Status oldStatus;

    @Getter
    @JsonProperty
    private Status newStatus;
}
