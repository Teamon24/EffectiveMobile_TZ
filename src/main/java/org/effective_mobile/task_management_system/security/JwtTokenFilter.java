package org.effective_mobile.task_management_system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.jwt.cookieName}")
    private String jwtCookieName;

    public JwtTokenFilter(
        JwtUserDetailsService jwtUserDetailsService,
        JwtTokenComponent jwtTokenComponent
    ) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenComponent = jwtTokenComponent;
    }

    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final @NonNull HttpServletResponse response,
        final @NonNull FilterChain chain
    )
        throws ServletException, IOException
    {
        String jwtToken = jwtTokenComponent.getJwtFromCookies(request);
        if (jwtToken != null) {
            String username = jwtTokenComponent.validateJwtToken(jwtToken);
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}