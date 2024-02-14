package org.effective_mobile.task_management_system.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.effective_mobile.task_management_system.exception.ErrorCreator;
import org.effective_mobile.task_management_system.exception.ErrorInfo;
import org.effective_mobile.task_management_system.exception.auth.AuthenticationException;
import org.effective_mobile.task_management_system.exception.messages.AuthExceptionMessages;
import org.effective_mobile.task_management_system.security.ContextComponent;
import org.effective_mobile.task_management_system.security.CookieComponent;
import org.effective_mobile.task_management_system.utils.Api;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.effective_mobile.task_management_system.security.authentication.AuthenticationTokenComponent.throwIfBlank;

@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {
    private final String authTokenName;

    private final ObjectMapper objectMapper;
    private final ContextComponent contextComponent;
    private final CookieComponent cookieComponent;

    private final AuthenticationTokenComponent authenticationTokenComponent;
    private final UserDetailsService userDetailsService;

    public AuthenticationTokenFilter(
        ObjectMapper objectMapper,
        ContextComponent contextComponent,
        CookieComponent cookieComponent,
        UserDetailsService userDetailsService,
        AuthenticationTokenComponent authenticationTokenComponent,
        AuthenticationTokenProperties authenticationTokenProperties
    ) {
        this.authTokenName = authenticationTokenProperties.getAuthTokenName();

        this.objectMapper = objectMapper;
        this.contextComponent = contextComponent;
        this.cookieComponent = cookieComponent;

        this.userDetailsService = userDetailsService;
        this.authenticationTokenComponent = authenticationTokenComponent;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return Api.SIGN_UP.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
        final @NonNull HttpServletRequest request,
        final @NonNull HttpServletResponse response,
        final @NonNull FilterChain chain
    )
        throws IOException, ServletException
    {
        var httpStatus = HttpStatus.OK;
        Exception caught = null;

        try {
            String token = cookieComponent.getToken(request);
            throwIfBlank(token, AuthExceptionMessages.noTokenInCookie(this.authTokenName));
            String username = authenticationTokenComponent.validateTokenAndGetUsername(token);
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
        String errorInfoAsString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorInfo);
        response.getWriter().write(errorInfoAsString);
    }
}