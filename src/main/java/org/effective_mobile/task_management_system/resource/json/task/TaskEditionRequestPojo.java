package org.effective_mobile.task_management_system.resource.json.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.resource.json.RequestPojo;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskEditionRequestPojo implements RequestPojo {

    @JsonProperty
    private String content;

    @JsonProperty
    private String priority;
}
