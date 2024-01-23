package org.effective_mobile.task_management_system.security.authorization;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.effective_mobile.task_management_system.security.ContextComponent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@AllArgsConstructor
@Log4j2
public class AuthorizationFilter extends OncePerRequestFilter {

    private final AuthorizationComponent authorizationComponent;
    private final ContextComponent contextComponent;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (contextComponent.isAuthenticated()) {
            RequiredAuthorizationInfo requiredAuthorizationInfo = authorizationComponent.extractAuthorizationInfo(request);
            Set<GrantedAuthority> authorities = authorizationComponent.getAuthorities(requiredAuthorizationInfo);
            contextComponent.setAuthorities(authorities);
            if (log.isDebugEnabled()) {
                String template = "User (id = '%s') was authorized with: %s";
                log.debug(template.formatted(requiredAuthorizationInfo.getUserId(), authorities));
            }
        }
        filterChain.doFilter(request, response);
    }
}
