package org.effective_mobile.task_management_system.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.effective_mobile.task_management_system.component.ContextComponent;
import org.effective_mobile.task_management_system.exception.ErrorCreator;
import org.effective_mobile.task_management_system.exception.ErrorInfo;
import org.effective_mobile.task_management_system.exception.auth.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final ObjectMapper objectMapper;
    private final ContextComponent contextComponent;

    @Override
    protected void doFilterInternal(
        final @NonNull HttpServletRequest request,
        final @NonNull HttpServletResponse response,
        final @NonNull FilterChain chain
    )
        throws ServletException, IOException
    {
        String authToken = authTokenComponent.getTokenFromCookies(request);
        if (authToken != null) {
            var httpStatus = HttpStatus.OK;
            Exception caught = null;
            try {
                String username = authTokenComponent.validateTokenAndGetUsername(authToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = unauthenticated(userDetails, request);
                contextComponent.setAuthentication(authentication);
            } catch (EntityNotFoundException e) {
                httpStatus = HttpStatus.NOT_FOUND;
                caught = e;
            } catch (AuthenticationException e) {
                httpStatus = HttpStatus.UNAUTHORIZED;
                caught = e;
            } catch (Exception e) {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                caught = e;
            }

            if (httpStatus != HttpStatus.OK) {
                addErrorToResponse(request, response, caught, httpStatus);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken unauthenticated(
        UserDetails userDetails,
        @NonNull HttpServletRequest request
    ) {
        var authentication = UsernamePasswordAuthenticationToken.unauthenticated(userDetails, null);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }

    private void addErrorToResponse(
        HttpServletRequest request,
        HttpServletResponse response,
        @NotNull Exception e,
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
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}