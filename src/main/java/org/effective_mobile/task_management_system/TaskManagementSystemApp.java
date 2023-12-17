package org.effective_mobile.task_management_system;

import org.effective_mobile.task_management_system.repository.TaskRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@PropertySource(
    value = {
        "classpath:custom.properties",
        "classpath:application.properties",
        "classpath:messages.properties"
    },
    encoding = "UTF-8"
)
@SpringBootApplication
@EntityScan(basePackages = "org.effective_mobile.task_management_system.entity")
@EnableJpaRepositories(
    basePackages = "org.effective_mobile.task_management_system.repository",
    basePackageClasses = TaskRepository.class
)
public class TaskManagementSystemApp {
    public static void main(String[] args) {
        SpringApplication.run(TaskManagementSystemApp.class, args);
    }
}