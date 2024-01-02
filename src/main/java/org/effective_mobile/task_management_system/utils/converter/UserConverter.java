package org.effective_mobile.task_management_system.utils.converter;

import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.assignment.SignupResponsePojo;

public class UserConverter {
    public static SignupResponsePojo userCreationResponse(User user) {
        return SignupResponsePojo.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .build();
    }
}
