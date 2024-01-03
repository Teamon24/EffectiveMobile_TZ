package org.effective_mobile.task_management_system.resource.json.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.effective_mobile.task_management_system.resource.json.ResponsePojo;
import org.effective_mobile.task_management_system.utils.enums.Status;

@Getter
public class AssignmentResponsePojo extends AssignmentEssential implements ResponsePojo {

    @JsonProperty private String newExecutorUsername;
    @JsonProperty private String oldExecutorUsername;

    public AssignmentResponsePojo(
        Long taskId,
        String newExecutorUsername,
        String oldExecutorUsername
    ) {
        super(taskId, Status.ASSIGNED);
        this.newExecutorUsername = newExecutorUsername;
        this.oldExecutorUsername = oldExecutorUsername;
    }
}

