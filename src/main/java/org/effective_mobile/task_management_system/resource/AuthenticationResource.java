package org.effective_mobile.task_management_system.resource;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.ContextComponent;
import org.effective_mobile.task_management_system.converter.UserConverter;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.pojo.UserCreationResponsePojo;
import org.effective_mobile.task_management_system.pojo.auth.SigninRequestPojo;
import org.effective_mobile.task_management_system.pojo.auth.SigninResponsePojo;
import org.effective_mobile.task_management_system.pojo.auth.SignupRequestPojo;
import org.effective_mobile.task_management_system.security.AuthTokenComponent;
import org.effective_mobile.task_management_system.security.CustomUserDetailsService;
import org.effective_mobile.task_management_system.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
@AllArgsConstructor
public class AuthenticationResource {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final ContextComponent contextComponent;
    private final AuthTokenComponent authTokenComponent;
    private final UserService userService;

    @Tag(name = "Регистрация")
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Long.class)) }),
        @ApiResponse(responseCode = "404", description = "Пользователя не существует")
    })
    @PostMapping(Api.SIGN_UP)
    public UserCreationResponsePojo signup(@RequestBody @Valid SignupRequestPojo signUpPayload) {
        userService.checkUserDoesNotExists(signUpPayload);
        User newUser = userService.createNewUser(signUpPayload);
        return UserConverter.userCreationResponse(newUser);
    }

    @Tag(name = "Вход в систему")
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = SigninResponsePojo.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "404", description = "Пользователя не существует")
    })
    @PostMapping(Api.SIGN_IN)
    public SigninResponsePojo signin(
        @RequestBody @Valid final SigninRequestPojo signinRequestPojo
    ) {
        try {
            UsernamePasswordAuthenticationToken authentication = createNamePassToken(signinRequestPojo);
            authenticationManager.authenticate(authentication);
            contextComponent.setAuthentication(authentication);
        } catch (final BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(signinRequestPojo.getEmail());
        final String token = authTokenComponent.generateToken(userDetails);

        return new SigninResponsePojo(token);
    }

    private UsernamePasswordAuthenticationToken createNamePassToken(SigninRequestPojo signinRequestPojo) {
        return new UsernamePasswordAuthenticationToken(
            signinRequestPojo.getEmail(),
            signinRequestPojo.getPassword()
        );
    }
}