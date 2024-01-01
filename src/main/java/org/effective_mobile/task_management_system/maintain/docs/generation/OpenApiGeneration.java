package org.effective_mobile.task_management_system.maintain.docs.generation;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.effective_mobile.task_management_system.Packages;
import org.effective_mobile.task_management_system.Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;

@PropertySource(
    value = {
        Properties.APPLICATION,
        Properties.CUSTOM,
        Properties.MESSAGES
    },
    encoding = Properties.ENCODING
)
@Log4j2
@ComponentScan(
    basePackages = {
        Packages.CONFING,
        Packages.SECURITY,
        Packages.RESOURCE,
        Packages.SERVICE,
        Packages.MAINTAIN
    }
)
@EnableAutoConfiguration(
    exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class }
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
    public static void logInitialMessageForEachLevel() {
        Arrays.stream(Level.values()).forEach(level -> log.log(level, INITIAL_MESSAGE));
    }
}