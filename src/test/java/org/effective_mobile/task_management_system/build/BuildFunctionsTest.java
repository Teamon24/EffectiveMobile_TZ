package org.effective_mobile.task_management_system.build;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @Test
    void lastMigrationNumberTest() throws IOException {
        String separator = FileSystems.getDefault().getSeparator();
        var path = Paths.get(String.join(separator, "src", "test", "resources", "migrations", "up"));
        Assertions.assertEquals(4, BuildFunctions.lastMigrationNumber(path, "migration-(\\d*)--\\[.*].*", "migration-"));
    }

    @Test
    void lastTag() {
        ArrayList<BuildFunctions.TagJsonPojo> tags = new ArrayList<>() {{
            add(new BuildFunctions.TagJsonPojo("db-1", new SimpleDateFormat("YYYY-MM-dd HH-mm-ss").format(new Date(System.currentTimeMillis()))));
            add(new BuildFunctions.TagJsonPojo("db-2", new SimpleDateFormat("YYYY-MM-dd HH-mm-ss").format(new Date(System.currentTimeMillis()))));
            add(new BuildFunctions.TagJsonPojo("db-3", new SimpleDateFormat("YYYY-MM-dd HH-mm-ss").format(new Date(System.currentTimeMillis()))));
            add(new BuildFunctions.TagJsonPojo("db-4", new SimpleDateFormat("YYYY-MM-dd HH-mm-ss").format(new Date(System.currentTimeMillis()))));
        }};
    }
}