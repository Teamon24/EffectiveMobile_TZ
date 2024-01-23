package org.effective_mobile.task_management_system.resource.json.task;

import lombok.Getter;
import lombok.Setter;
import org.effective_mobile.task_management_system.resource.json.RequestPojo;

/**
 * Json pojo с информацией для создания задачи.
 */
@Getter
@Setter
public class TaskCreationRequestPojo extends TaskEssential implements RequestPojo {

    public TaskCreationRequestPojo() {}

    public TaskCreationRequestPojo(String priority, String content) {
        super(priority, content);
    }
}

