package org.effective_mobile.task_management_system.resource.json.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.effective_mobile.task_management_system.resource.json.ResponsePojo;
import org.effective_mobile.task_management_system.utils.enums.Status;

public class UnassignmentResponsePojo extends AssignmentEssential implements ResponsePojo {

    @JsonProperty
    private final Status status;

    public UnassignmentResponsePojo(Long taskId) {
        super(taskId, null);
        status = Status.PENDING;
    }
}
