package org.effective_mobile.task_management_system.resource.json.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.effective_mobile.task_management_system.resource.json.ResponsePojo;
import org.effective_mobile.task_management_system.utils.enums.Status;

@Getter
public class AssignmentResponsePojo extends AssignmentEssential implements ResponsePojo {

    @JsonProperty
    protected Status status = Status.ASSIGNED;

    @JsonProperty
    private String newExecutorUsername;

    public String getOldExecutorUsername() {
        return super.getExecutorUsername();
    }

    public AssignmentResponsePojo(Long taskId, String newExecutorUsername, String oldExecutorUsername) {
        super(taskId, oldExecutorUsername);
        this.newExecutorUsername = newExecutorUsername;
    }
}
