package org.effective_mobile.task_management_system.pojo.task;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TaskExecutor extends UserInfo {
    public TaskExecutor(Long userId, String username) {
        super(userId, username);
    }
}
