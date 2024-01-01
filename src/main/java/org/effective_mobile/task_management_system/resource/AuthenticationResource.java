package org.effective_mobile.task_management_system.resource;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.ContextComponent;
import org.effective_mobile.task_management_system.component.UsernameProvider;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.UserCreationResponsePojo;
import org.effective_mobile.task_management_system.resource.json.auth.SigninRequestPojo;
import org.effective_mobile.task_management_system.resource.json.auth.SigninResponsePojo;
import org.effective_mobile.task_management_system.resource.json.auth.SignupRequestPojo;
import org.effective_mobile.task_management_system.security.AuthTokenComponent;
import org.effective_mobile.task_management_system.service.UserService;
import org.effective_mobile.task_management_system.utils.converter.UserConverter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
public class AuthenticationResource {

    private final AuthenticationManager authenticationManager;
    private final UsernameProvider usernameProvider;
    private final ContextComponent contextComponent;
    private final AuthTokenComponent authTokenComponent;
    private final UserService userService;

    @PostMapping(Api.SIGN_UP)
    public UserCreationResponsePojo signup(@RequestBody @Valid SignupRequestPojo signUpPayload) {
        userService.checkUserDoesNotExists(signUpPayload);
        User newUser = userService.createNewUser(signUpPayload);
        return UserConverter.userCreationResponse(newUser);
    }

    @PostMapping(Api.SIGN_IN)
    public SigninResponsePojo signin(@RequestBody @Valid final SigninRequestPojo signinRequestPojo) {
        UsernamePasswordAuthenticationToken authentication = unauthenticated(signinRequestPojo);
        try {
            authenticationManager.authenticate(authentication);
        } catch (final BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        contextComponent.setAuthentication(authentication);
        final String token = authTokenComponent.generateToken(authentication);
        return new SigninResponsePojo(token);
    }

    private UsernamePasswordAuthenticationToken unauthenticated(SigninRequestPojo signinRequestPojo) {
        return UsernamePasswordAuthenticationToken.unauthenticated(
            usernameProvider.getUsername(signinRequestPojo),
            usernameProvider.getCredentials(signinRequestPojo)
        );
    }
}