package org.effective_mobile.task_management_system.confing;

import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.security.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfigurations {

    public final JwtTokenFilter jwtTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        final AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
        return http.cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

}
 