package org.effective_mobile.task_management_system.service;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.pojo.auth.SignupPayload;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private UserComponent userComponent;

    public void checkUserDoesNotExists(SignupPayload signUpPayload) {
        userComponent.checkUsernameDoesNotExist(signUpPayload.getUsername());
        userComponent.checkEmailDoesNotExist(signUpPayload.getEmail());
    }

    public User createNewUser(SignupPayload signUpPayload) {
        return userComponent.createAndSaveUser(signUpPayload);
    }
}
