package org.effective_mobile.task_management_system.component;

import jakarta.servlet.http.HttpServletRequest;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public class ContextComponent {

    public CustomUserDetails getPrincipal() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
}
