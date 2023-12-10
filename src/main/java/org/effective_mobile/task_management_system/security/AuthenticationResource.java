package org.effective_mobile.task_management_system.security;

import jakarta.validation.Valid;
import org.effective_mobile.task_management_system.pojo.SigninRequest;
import org.effective_mobile.task_management_system.pojo.SigninResponse;
import org.effective_mobile.task_management_system.pojo.SignupRequest;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AuthenticationResource {

    private final AuthenticationManager authenticationManager;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    public AuthenticationResource(
        AuthenticationManager authenticationManager,
        JwtUserDetailsService jwtUserDetailsService,
        JwtTokenService jwtTokenService,
        UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
    }

    @PostMapping(ApiPath.signup)
    public Long signup(@Valid @RequestBody SignupRequest signUpRequest) {
        userService.validateNewUser(signUpRequest);
        return userService.createNewUser(signUpRequest);
    }

    @PostMapping(ApiPath.signin)
    public ResponseEntity<SigninResponse> signin(
        @RequestBody @Valid final SigninRequest signinRequest
    ) {
        try {
            UsernamePasswordAuthenticationToken authentication = createNamePassToken(signinRequest);
            authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (final BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(signinRequest.getEmail());
        final ResponseCookie jwtCookie = jwtTokenService.generateJwtCookie(userDetails);

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .body(new SigninResponse(jwtCookie.getValue()));
    }

    private UsernamePasswordAuthenticationToken createNamePassToken(SigninRequest signinRequest) {
        return new UsernamePasswordAuthenticationToken(
            signinRequest.getEmail(),
            signinRequest.getPassword()
        );
    }

    @PostMapping(ApiPath.signout)
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtTokenService.getCleanTokenCookie();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body("signed out");
    }
}