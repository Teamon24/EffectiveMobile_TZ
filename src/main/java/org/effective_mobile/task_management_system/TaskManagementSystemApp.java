package org.effective_mobile.task_management_system;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;


@PropertySource(
    value = {
        "classpath:custom.properties",
        "classpath:application.properties",
        "classpath:messages.properties"
    },
    encoding = "UTF-8"
)
@Log4j2
@SpringBootApplication
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