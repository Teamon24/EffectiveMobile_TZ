package org.effective_mobile.task_management_system.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.Set;

@Component
public class ContextComponent {

    public Long getUserId() {
        return getPrincipal().getUserId();
    }

    public CustomUserDetails getPrincipal() {
        return (CustomUserDetails) getAuthentication().getPrincipal();
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public void setAuthorities(Set<GrantedAuthority> authorities) {
        getPrincipal().setAuthorities(authorities);
    }

    public HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        return Optional
            .ofNullable(requestAttributes)
            .orElseThrow(() -> {
                String template = "There is no '%s' in '%s'";
                String servletRequestAttributeName = ServletRequestAttributes.class.getSimpleName();
                String requestHolderName = RequestContextHolder.class.getSimpleName();
                return new RuntimeException(template.formatted(servletRequestAttributeName, requestHolderName));
            })
            .getRequest();
    }

    public void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public boolean isAuthenticated() {
        return getAuthentication() != null;
    }
}
