package org.effective_mobile.task_management_system.confing;

import org.effective_mobile.task_management_system.component.ContextComponent;
import org.effective_mobile.task_management_system.logging.CustomRequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

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

    public CommonsRequestLoggingFilter logFilter(ContextComponent contextComponent) {
        CommonsRequestLoggingFilter filter = new CustomRequestLoggingFilter(contextComponent);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        return filter;
    }
}
