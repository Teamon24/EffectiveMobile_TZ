package org.effective_mobile.task_management_system.config;

import org.effective_mobile.task_management_system.security.ContextComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class AppConfigurations {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public ContextComponent contextComponent() {
        return new ContextComponent();
    }
}
