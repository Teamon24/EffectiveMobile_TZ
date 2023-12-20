package org.effective_mobile.task_management_system.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.effective_mobile.task_management_system.confing.JacksonConfig;
import org.effective_mobile.task_management_system.exception.ErrorCreator;
import org.effective_mobile.task_management_system.exception.ErrorInfo;
import org.effective_mobile.task_management_system.exception.InvalidAuthTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final AuthTokenComponent authTokenComponent;

    @Override
    protected void doFilterInternal(
        final @NonNull HttpServletRequest request,
        final @NonNull HttpServletResponse response,
        final @NonNull FilterChain chain
    )
        throws ServletException, IOException
    {
        String token = authTokenComponent.getTokenFromCookies(request);
        if (token != null) {
            try {
                String username = authTokenComponent.validateTokenAndGetSubject(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (EntityNotFoundException e) {
                addErrorToResponse(request, response, e, HttpStatus.NOT_FOUND);
                return;
            } catch (InvalidAuthTokenException e) {
                addErrorToResponse(request, response, e, HttpStatus.UNAUTHORIZED);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void addErrorToResponse(
        HttpServletRequest request,
        HttpServletResponse response,
        Exception e,
        HttpStatus httpStatus
    ) throws IOException {
        ErrorInfo errorInfo = ErrorCreator.createErrorInfo(request, e, httpStatus);
        response.setStatus(httpStatus.value());
        response.getWriter().write(convertObjectToJson(errorInfo));
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        return JacksonConfig.getObjectMapperInstance().writeValueAsString(object);
    }
}