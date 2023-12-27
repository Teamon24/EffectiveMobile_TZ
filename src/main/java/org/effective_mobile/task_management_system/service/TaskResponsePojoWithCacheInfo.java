package org.effective_mobile.task_management_system.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.effective_mobile.task_management_system.resource.json.JsonPojoId;
import org.effective_mobile.task_management_system.resource.json.ResponsePojo;

@Builder
public class TaskResponsePojoWithCacheInfo implements ResponsePojo, JsonPojoId {
    @JsonProperty private String ref;
    @JsonProperty private JsonPojoId taskResponsePojo;

    public TaskResponsePojoWithCacheInfo(String ref, JsonPojoId taskResponsePojo) {
        this.taskResponsePojo = taskResponsePojo;
        this.ref = ref;
    }

    @Override
    @JsonIgnore
    public Long getId() {
        return taskResponsePojo.getId();
    }

    @Getter
    @AllArgsConstructor
    public static class LazyExceptionTaskId implements JsonPojoId {
        @JsonProperty private Long id;
        @JsonProperty private Long userId;
    }
}
