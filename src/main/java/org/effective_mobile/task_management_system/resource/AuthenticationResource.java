package org.effective_mobile.task_management_system.resource;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.ContextComponent;
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
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final JwtTokenComponent jwtTokenComponent;
    private final UserService userService;

    @PostMapping(Api.SIGN_UP)
    public Long signup(@RequestBody @Valid SignupPayload signUpPayload) {
        userService.checkUserDoesNotExists(signUpPayload);
        return userService.createNewUser(signUpPayload);
    }

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
        final String jwtCookie = jwtTokenComponent.generateToken(userDetails);

        return new SigninResponse(jwtCookie);
    }

    private UsernamePasswordAuthenticationToken createNamePassToken(SigninPayload signinPayload) {
        return new UsernamePasswordAuthenticationToken(
            signinPayload.getEmail(),
            signinPayload.getPassword()
        );
    }

    @PostMapping(Api.SIGN_OUT)
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtTokenComponent.getCleanTokenCookie();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body("signed out");
    }
}