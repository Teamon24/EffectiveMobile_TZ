package org.effective_mobile.task_management_system.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.effective_mobile.task_management_system.exception.ErrorCreator;
import org.effective_mobile.task_management_system.exception.ErrorInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenComponent jwtTokenComponent;
    private final ObjectMapper objectMapper;

    @Value("${app.jwt.cookieName}")
    private String jwtCookieName;

    public JwtTokenFilter(
        JwtUserDetailsService jwtUserDetailsService,
        JwtTokenComponent jwtTokenComponent,
        ObjectMapper objectMapper
    ) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenComponent = jwtTokenComponent;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
        final @NonNull HttpServletRequest request,
        final @NonNull HttpServletResponse response,
        final @NonNull FilterChain chain
    )
        throws ServletException, IOException
    {
        String jwtToken = jwtTokenComponent.getJwtFromCookies(request);
        if (jwtToken != null) {
            try {
                String username = jwtTokenComponent.validateJwtToken(jwtToken);
                UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (EntityNotFoundException e) {
                HttpStatus notFound = HttpStatus.NOT_FOUND;
                ErrorInfo errorInfo = ErrorCreator.createErrorInfo(request, e, notFound);
                response.setStatus(notFound.value());
                response.getWriter().write(convertObjectToJson(errorInfo));
                return;
            }
        }

        chain.doFilter(request, response);
    }


    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}