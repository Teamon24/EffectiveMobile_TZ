package org.effective_mobile.task_management_system.pojo.assignment;

import lombok.Getter;

@Getter
public class AssignmentPayload extends AssignmentEssential {
    public AssignmentPayload(Long taskId, String executorUsername) {
        super(taskId, executorUsername);
    }
}
