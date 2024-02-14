package org.effective_mobile.task_management_system.resource.json.assignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.effective_mobile.task_management_system.resource.json.ResponsePojo;
import org.effective_mobile.task_management_system.utils.enums.Status;

public class UnassignmentResponsePojo extends AssignmentEssential implements ResponsePojo {

    @JsonIgnore
    protected String executorUsername = null;

    public UnassignmentResponsePojo(Long taskId) {
        super(taskId, Status.PENDING);
    }
}
