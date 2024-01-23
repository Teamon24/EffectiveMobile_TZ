package org.effective_mobile.task_management_system.resource;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.security.ContextComponent;
import org.effective_mobile.task_management_system.security.UsernameProvider;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.assignment.SignupResponsePojo;
import org.effective_mobile.task_management_system.resource.json.auth.SigninRequestPojo;
import org.effective_mobile.task_management_system.resource.json.auth.SigninResponsePojo;
import org.effective_mobile.task_management_system.resource.json.auth.SignupRequestPojo;
import org.effective_mobile.task_management_system.security.authentication.AuthTokenComponent;
import org.effective_mobile.task_management_system.service.UserService;
import org.effective_mobile.task_management_system.utils.Api;
import org.effective_mobile.task_management_system.utils.converter.UserConverter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final UserDetailsService userDetailsService;
    private final AuthTokenComponent authTokenComponent;
    private final UserService userService;

    @PostMapping(Api.SIGN_UP)
    public SignupResponsePojo signup(@RequestBody @Valid SignupRequestPojo signUpPayload) {
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
        String username = usernameProvider.getUsername(signinRequestPojo);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Object credentials = usernameProvider.getCredentials(signinRequestPojo);
        return UsernamePasswordAuthenticationToken.unauthenticated(userDetails, credentials);
    }
}