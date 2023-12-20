package org.effective_mobile.task_management_system.resource.json.task;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TaskExecutor extends UserInfo {
    public TaskExecutor(Long userId, String username) {
        super(userId, username);
    }
}
