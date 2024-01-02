package org.effective_mobile.task_management_system.config;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.ContextComponent;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.component.UsernameProvider;
import org.effective_mobile.task_management_system.security.AuthTokenComponent;
import org.effective_mobile.task_management_system.security.AuthenticationComponent;
import org.effective_mobile.task_management_system.security.AuthenticationComponentImpl;
import org.effective_mobile.task_management_system.security.AuthenticationFilter;
import org.effective_mobile.task_management_system.security.AuthorizationComponent;
import org.effective_mobile.task_management_system.security.AuthorizationComponentImpl;
import org.effective_mobile.task_management_system.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfigurations {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UsernameProvider usernameProvider(UserComponent userComponent) {
        return new EmailAsUsernameProvider(userComponent);
    }

    @Bean
    public UserDetailsService userDetailsService(
        AuthorizationComponent authorizationComponent,
        UsernameProvider usernameProvider
    ) {
        return new CustomUserDetailsService(authorizationComponent, usernameProvider);
    }

    @Bean
    public AuthenticationManager authenticationManager(
        final AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(
        final HttpSecurity http,
        final AuthenticationFilter authenticationFilter) throws Exception
    {
        return http.cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthorizationComponent authorizationComponent(
        UserComponent userComponent,
        ContextComponent contextComponent,
        TaskComponent taskComponent
    ) {
        return new AuthorizationComponentImpl(userComponent, contextComponent, taskComponent);
    }

    @Bean
    public AuthenticationComponent authenticationComponent(
        AuthTokenComponent authTokenComponent,
        ContextComponent contextComponent
    ) {
        return new AuthenticationComponentImpl(authTokenComponent, contextComponent);
    }
}

