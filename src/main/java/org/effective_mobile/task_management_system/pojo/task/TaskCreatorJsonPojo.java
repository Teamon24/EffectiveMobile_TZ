package org.effective_mobile.task_management_system.pojo.task;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TaskCreatorJsonPojo extends UserInfoJsonPojo {
    public TaskCreatorJsonPojo(Long userId, String username) {
        super(userId, username);
    }
}
