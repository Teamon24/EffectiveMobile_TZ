package org.effective_mobile.task_management_system.pojo.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.effective_mobile.task_management_system.enums.Status;

public class AssignmentResponse extends AssignmentEssential {
    public AssignmentResponse() {}

    @Getter
    @JsonProperty
    private String oldExecutor;

    @Getter
    @JsonProperty
    private final Status status = Status.ASSIGNED;

    public AssignmentResponse(Long taskId, String executorUsername, String oldExecutor) {
        super(taskId, executorUsername);
        this.oldExecutor = oldExecutor;
    }
}
