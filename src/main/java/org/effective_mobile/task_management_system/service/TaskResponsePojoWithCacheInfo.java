package org.effective_mobile.task_management_system.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.effective_mobile.task_management_system.resource.json.ResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo;

@Builder
public class TaskResponsePojoWithCacheInfo implements ResponsePojo {
    @JsonProperty private String ref;
    @JsonProperty private TaskResponsePojo taskResponsePojo;

    public TaskResponsePojoWithCacheInfo(String ref, TaskResponsePojo taskResponsePojo) {
        this.taskResponsePojo = taskResponsePojo;
        this.ref = ref;
    }
}
