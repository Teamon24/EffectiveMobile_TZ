package org.effective_mobile.task_management_system.resource.json.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.utils.enums.Status;

@Getter
@NoArgsConstructor
public class AssignmentResponsePojo extends AssignmentEssential {
    @JsonProperty
    private final Status status = Status.ASSIGNED;

    @JsonProperty
    private String newExecutorUsername;

    @JsonProperty
    public String getOldExecutorUsername() {
        return super.getExecutorUsername();
    }

    public AssignmentResponsePojo(Long taskId, String newExecutorUsername, String oldExecutorUsername) {
        super(taskId, oldExecutorUsername);
        this.newExecutorUsername = newExecutorUsername;
    }
}
