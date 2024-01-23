package org.effective_mobile.task_management_system.resource.json.task;

import org.effective_mobile.task_management_system.resource.json.RequestPojo;

public class TaskEditionRequestPojo extends TaskEssential implements RequestPojo {
    public TaskEditionRequestPojo() {}
    public TaskEditionRequestPojo(String content, String priority) {
        super(priority, content);
    }
}
