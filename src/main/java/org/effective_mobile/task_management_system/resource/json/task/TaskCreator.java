package org.effective_mobile.task_management_system.resource.json.task;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TaskCreator extends UserInfo {
    public TaskCreator(Long userId, String username) {
        super(userId, username);
    }
}
