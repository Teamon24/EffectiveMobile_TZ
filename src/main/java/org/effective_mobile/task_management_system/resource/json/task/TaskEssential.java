package org.effective_mobile.task_management_system.resource.json.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.effective_mobile.task_management_system.resource.JsonPojos;
import org.effective_mobile.task_management_system.resource.json.JsonPojo;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString()
public abstract class TaskEssential implements JsonPojo {

    @JsonProperty(JsonPojos.Task.Field.PRIORITY)
    protected String priority;

    @JsonProperty(JsonPojos.Task.Field.CONTENT)
    protected String content;
}
