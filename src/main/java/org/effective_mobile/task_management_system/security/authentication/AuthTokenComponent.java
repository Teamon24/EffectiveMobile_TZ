package org.effective_mobile.task_management_system.security.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.effective_mobile.task_management_system.exception.auth.TokenAuthenticationException;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthTokenComponent {
    @Nullable String getToken(Cookie cookie);
    String getTokenFromCookies(HttpServletRequest request);

    String generateToken(UsernamePasswordAuthenticationToken subject);
    String generateToken(UserDetails userDetails);
    ResponseCookie generateTokenResponseCookie(UserDetails userDetails);

    Cookie generateTokenCookie(UserDetails userDetails);

    DecodedJWT validateToken(String token) throws TokenAuthenticationException;
    String validateTokenAndGetUsername(String token) throws TokenAuthenticationException;
}
