package org.effective_mobile.task_management_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;


@PropertySource(value = {
    "classpath:custom.properties",
    "classpath:application.properties"}, encoding = "UTF-8")
@SpringBootApplication
public class TaskManagementSystemApp {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementSystemApp.class, args);
    }
}