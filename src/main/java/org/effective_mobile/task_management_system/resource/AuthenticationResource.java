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
import org.effective_mobile.task_management_system.pojo.UserCreationResponse;
import org.effective_mobile.task_management_system.pojo.auth.SigninPayload;
import org.effective_mobile.task_management_system.pojo.auth.SigninResponse;
import org.effective_mobile.task_management_system.pojo.auth.SignupPayload;
import org.effective_mobile.task_management_system.security.JwtTokenComponent;
import org.effective_mobile.task_management_system.security.CustomUserDetailsService;
import org.effective_mobile.task_management_system.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
    private final JwtTokenComponent tokenComponent;
    private final UserService userService;

    @Tag(name = "Регистрация")
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Long.class)) }),
        @ApiResponse(responseCode = "404", description = "Пользователя не существует")
    })
    @PostMapping(Api.SIGN_UP)
    public UserCreationResponse signup(@RequestBody @Valid SignupPayload signUpPayload) {
        userService.checkUserDoesNotExists(signUpPayload);
        User newUser = userService.createNewUser(signUpPayload);
        return UserConverter.userCreationResponse(newUser);
    }

    @Tag(name = "Вход в систему")
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = SigninResponse.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "404", description = "Пользователя не существует")
    })
    @PostMapping(Api.SIGN_IN)
    public SigninResponse signin(
        @RequestBody @Valid final SigninPayload signinPayload
    ) {
        try {
            UsernamePasswordAuthenticationToken authentication = createNamePassToken(signinPayload);
            authenticationManager.authenticate(authentication);
            contextComponent.setAuthentication(authentication);
        } catch (final BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(signinPayload.getEmail());
        final String token = tokenComponent.generateToken(userDetails);

        return new SigninResponse(token);
    }

    private UsernamePasswordAuthenticationToken createNamePassToken(SigninPayload signinPayload) {
        return new UsernamePasswordAuthenticationToken(
            signinPayload.getEmail(),
            signinPayload.getPassword()
        );
    }
}