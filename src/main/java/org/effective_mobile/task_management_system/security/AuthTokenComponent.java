package org.effective_mobile.task_management_system.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.effective_mobile.task_management_system.exception.InvalidTokenException;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthTokenComponent {
    ResponseCookie getCleanTokenCookie();
    String getTokenFromCookies(HttpServletRequest request);
    String getToken(Cookie cookie);
    ResponseCookie generateTokenCookie(UserDetails userDetails);
    String generateToken(final UserDetails userDetails);
    void validateToken(String token) throws InvalidTokenException;
    String validateTokenAndGetSubject(final String token) throws InvalidTokenException;
}
