package org.effective_mobile.task_management_system.build;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

class BuildFunctionsTest {

    @Test
    void substitute() {
        String urlTemplate = "jdbc:postgresql://${address}:${port}/${name}";
        String port = "5432";
        String address = "localhost";
        String database = "test_db";
        String[] args = { address, port, database };
        String expected = "jdbc:postgresql://%s:%s/%s".formatted(args);

        Properties properties = new Properties() {{

            put("port", port);
            put("address", address);
            put("name", database);
            put("url", urlTemplate);

        }};
        String actual = BuildFunctions.substitute(properties, ((String) properties.get("url")));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getRegexForFormat() {
        String format = "YYYY-MM-dd--HH-mm-ss";
        String expectedFormat = "\\d\\d\\d\\d-\\d\\d-\\d\\d--\\d\\d-\\d\\d-\\d\\d";
        String regexForFormat = BuildFunctions.getRegexForFormat(format);
        Assertions.assertEquals(expectedFormat, regexForFormat);
    }
}