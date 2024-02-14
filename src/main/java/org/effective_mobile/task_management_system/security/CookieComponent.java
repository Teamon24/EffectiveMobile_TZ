package org.effective_mobile.task_management_system.security;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.effective_mobile.task_management_system.security.authentication.AuthenticationTokenProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class CookieComponent {
    private final String authTokenCookieName;

    public CookieComponent(AuthenticationTokenProperties authenticationTokenProperties) {
        this.authTokenCookieName = authenticationTokenProperties.getAuthTokenName();
    }

    @Nullable
    public String getToken(HttpServletRequest request) {
        return getValue(request, authTokenCookieName);
    }

    public boolean hasToken(HttpServletRequest request) {
        Cookie authTokenCookie = WebUtils.getCookie(request, authTokenCookieName);
        return authTokenCookie != null && StringUtils.isNotBlank(authTokenCookie.getValue());
    }

    private String getValue(HttpServletRequest request, String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        return cookie != null ? cookie.getValue() : null;
    }
}
