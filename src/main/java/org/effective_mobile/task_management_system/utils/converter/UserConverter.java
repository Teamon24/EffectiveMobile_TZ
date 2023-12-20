package org.effective_mobile.task_management_system.utils.converter;

import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.UserCreationResponsePojo;

public class UserConverter {
    public static UserCreationResponsePojo userCreationResponse(User user) {
        return UserCreationResponsePojo.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .build();
    }
}
