package org.effective_mobile.task_management_system.converter;

import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.pojo.UserCreationResponsePojo;

public class UserConverter {
    public static UserCreationResponsePojo userCreationResponse(User user) {
        return UserCreationResponsePojo.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .build();
    }
}
