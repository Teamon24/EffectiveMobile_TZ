package org.effective_mobile.task_management_system.resource.json.assignment;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.pojo.HasExecutorUsername;
import org.effective_mobile.task_management_system.pojo.HasTaskId;
import org.effective_mobile.task_management_system.resource.json.JsonPojo;

/**
 * Json pojo с информацией для создания задачи.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AssignmentEssential implements HasExecutorUsername, HasTaskId, JsonPojo {
    @JsonProperty private Long taskId;

    @JsonIgnore
    private String executorUsername;

    @Override
    public String getExecutorUsername() {
        return executorUsername;
    }
}

