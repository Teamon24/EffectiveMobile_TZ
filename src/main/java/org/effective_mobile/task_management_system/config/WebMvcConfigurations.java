package org.effective_mobile.task_management_system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.maintain.logging.HttpExchangeLoggingComponent;
import org.effective_mobile.task_management_system.maintain.logging.HttpExchangeLoggingInterceptor;
import org.effective_mobile.task_management_system.security.CookieComponent;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@AllArgsConstructor
public class WebMvcConfigurations implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;
    private final CookieComponent cookieComponent;
    private final HttpExchangeLoggingComponent httpExchangeLoggingComponent;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
            new HttpExchangeLoggingInterceptor(cookieComponent, httpExchangeLoggingComponent));
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
            .filter(x -> x instanceof MappingJackson2HttpMessageConverter)
            .forEach(x -> ((MappingJackson2HttpMessageConverter) x).setObjectMapper(objectMapper));
    }
}