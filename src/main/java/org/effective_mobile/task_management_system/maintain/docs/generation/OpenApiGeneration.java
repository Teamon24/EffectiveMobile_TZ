package org.effective_mobile.task_management_system.maintain.docs.generation;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.effective_mobile.task_management_system.Packages;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;

@PropertySource(
    value = {
        "classpath:custom.properties",
        "classpath:application.properties",
        "classpath:messages.properties"
    },
    encoding = "UTF-8"
)
@Log4j2
@SpringBootApplication(
    scanBasePackages = {
        Packages.RESOURCE,
        Packages.SERVICE,
        Packages.SECURITY,
        Packages.MAINTAIN,
        Packages.CONFING
    },
    exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class }
)
public class OpenApiGeneration {

    private static final String INITIAL_MESSAGE =
        """
            \nThis is entrypoint for open api specification generation,
            that disables spring data configuration to fasten the generating process.
        """;

    public static void main(String[] args) {
        logInitialMessageForEachLevel();
        SpringApplication.run(OpenApiGeneration.class, args);
    }
    public static void logInitialMessageForEachLevel(){
        Arrays.stream(Level.values()).forEach(level -> log.log(level, INITIAL_MESSAGE));
    }
}