package org.effective_mobile.task_management_system.pojo.task;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TaskExecutorJsonPojo extends UserInfoJsonPojo {
    public TaskExecutorJsonPojo(Long userId, String username) {
        super(userId, username);
    }
}
