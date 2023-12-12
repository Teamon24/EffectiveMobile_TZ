package org.effective_mobile.task_management_system.pojo.assignment;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.pojo.HasExecutorUsername;
import org.effective_mobile.task_management_system.pojo.HasTaskId;

/**
 * Json pojo с информацией для создания задачи.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AssignmentEssential implements HasExecutorUsername, HasTaskId {
    @JsonProperty private Long taskId;
    @JsonProperty private String executorUsername;
}

