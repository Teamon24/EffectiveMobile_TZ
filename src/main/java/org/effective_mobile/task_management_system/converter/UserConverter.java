package org.effective_mobile.task_management_system.converter;

import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.pojo.UserCreationResponse;

public class UserConverter {
    public static UserCreationResponse userCreationResponse(User user) {
        return UserCreationResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .build();
    }
}
