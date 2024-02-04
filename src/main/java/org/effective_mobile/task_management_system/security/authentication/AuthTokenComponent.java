package org.effective_mobile.task_management_system.security.authentication;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.effective_mobile.task_management_system.exception.auth.TokenAuthenticationException;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface AuthTokenComponent {
    @Nullable String getToken(Cookie cookie);
    String getTokenFromCookies(HttpServletRequest request) throws TokenAuthenticationException;
    boolean hasTokenInCookies(HttpServletRequest request) throws TokenAuthenticationException;
    void validateToken(HttpServletRequest request) throws TokenAuthenticationException;

    String generateToken(UserDetails userDetails);
    ResponseCookie generateTokenResponseCookie(UserDetails userDetails);
    Cookie generateTokenCookie(UserDetails userDetails);

    String generateToken(UsernamePasswordAuthenticationToken token);
    String validateTokenAndGetUsername(String token) throws TokenAuthenticationException;
    Date getExpirationDate(String token) throws TokenAuthenticationException;
    Date getIssueDate(String token) throws TokenAuthenticationException;
}
