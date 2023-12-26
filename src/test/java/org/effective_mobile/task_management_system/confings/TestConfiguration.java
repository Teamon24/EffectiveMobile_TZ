package org.effective_mobile.task_management_system.confings;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {
    "classpath:messages.properties",
    "classpath:application.properties",
    "classpath:custom.properties"
}, encoding = "UTF-8")
public class TestConfiguration {}
