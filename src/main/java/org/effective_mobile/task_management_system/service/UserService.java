package org.effective_mobile.task_management_system.service;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.auth.SignupRequestPojo;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private UserComponent userComponent;

    public void checkUserDoesNotExists(SignupRequestPojo signupRequestPojo) {
        userComponent.checkUsernameDoesNotExist(signupRequestPojo.getUsername());
        userComponent.checkEmailDoesNotExist(signupRequestPojo.getEmail());
    }

    public User createNewUser(SignupRequestPojo signupRequestPojo) {
        return userComponent.createAndSaveUser(signupRequestPojo);
    }
}
