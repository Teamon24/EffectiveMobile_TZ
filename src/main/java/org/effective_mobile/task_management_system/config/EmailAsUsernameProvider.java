package org.effective_mobile.task_management_system.config;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.component.UsernameProvider;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.auth.SigninRequestPojo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Класс содержит логику, которая определяет,
 * что за данные будут использоваться в качестве {@link UserDetails#getUsername()}
 */
@Component
@AllArgsConstructor
public final class EmailAsUsernameProvider implements UsernameProvider {

    private final UserComponent userComponent;

    @Override
    public User getUserBy(String username) {
        return userComponent.getByEmail(username);
    }
    @Override
    public String getUsername(User user) {
        return user.getEmail();
    }

    @Override
    public String getUsername(SigninRequestPojo pojo) {
        return pojo.getEmail();
    }
    @Override
    public Object getCredentials(SigninRequestPojo pojo) {
        return pojo.getPassword();
    }

    @Override
    public String getSubject(UsernamePasswordAuthenticationToken authentication) {
        return authentication.getName();
    }

    @Override
    public String getSubject(UserDetails userDetails) {
        return userDetails.getUsername();
    }
}
