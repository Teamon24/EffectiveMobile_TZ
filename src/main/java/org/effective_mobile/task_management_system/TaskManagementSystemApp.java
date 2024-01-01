package org.effective_mobile.task_management_system;

import lombok.extern.log4j.Log4j2;
import org.effective_mobile.task_management_system.maintain.docs.generation.OpenApiConfigurations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;


@PropertySource(
    value = {
        Properties.APPLICATION,
        Properties.CUSTOM,
        Properties.MESSAGES
    },
    encoding = Properties.ENCODING
)
@Log4j2
@Configuration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters =
    @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        value = OpenApiConfigurations.class
    )
)
public class TaskManagementSystemApp {
    public static void main(String[] args) {
        SpringApplication.run(TaskManagementSystemApp.class, args);
        logAppInitialized();
    }

    public static void logAppInitialized(){
        log.info("application is initialized (info)");
        log.warn("application is initialized (warn)");
        log.debug("application is initialized (debug)");
        log.error("application is initialized (error)");
        log.fatal("application is initialized (fatal)");
    }
}