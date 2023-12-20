package org.effective_mobile.task_management_system.confing;

import org.effective_mobile.task_management_system.component.ContextComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class Configurations {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public ContextComponent contextComponent() {
        return new ContextComponent();
    }
}
