package org.effective_mobile.task_management_system.resource.json.assignment;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.pojo.HasTaskId;
import org.effective_mobile.task_management_system.utils.enums.Status;

/**
 * Json pojo с информацией для создания задачи.
 */
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AssignmentEssential implements HasTaskId {
    @Getter @JsonProperty protected Long taskId;
    @Getter @JsonProperty protected Status status;
}

