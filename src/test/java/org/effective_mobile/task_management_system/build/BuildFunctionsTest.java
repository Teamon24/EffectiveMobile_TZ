package org.effective_mobile.task_management_system.build;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

class BuildFunctionsTest {
    private final String separator = FileSystems.getDefault().getSeparator();

    @Test
    void name() throws IOException {
        String defaultApplicationProperties = "application.properties";
        List<Path> paths = IntStream.range(1, 20).mapToObj(i -> {
            String first = String.valueOf(i);
            if (first.toCharArray().length == 1) {
                first = "0" + first;
            }
            Path path = Paths.get(first);
            if (new Random().nextInt(0, 4) == 1) {
                return path.resolve(defaultApplicationProperties);
            }
            return path;
        }).collect(Collectors.toList());

        testAlwaysTopOrBottom(
            paths,
            (path) -> path.endsWith(defaultApplicationProperties),
            MAX_VALUE, MIN_VALUE,
            lastIndex(paths)
        );

        testAlwaysTopOrBottom(
            paths,
            (path) -> path.endsWith(defaultApplicationProperties),
            MIN_VALUE, MAX_VALUE,
            firstIndex(paths)
        );
    }

    private void testAlwaysTopOrBottom(
        List<Path> paths,
        Predicate<Path> pathPredicate,
        int value1,
        int value2,
        int index
    ) {
        Collections.shuffle(paths);
        paths.sort((path1, path2) -> {
            if (pathPredicate.test(path1) && pathPredicate.test(path2)) { return path1.compareTo(path2); }
            if (pathPredicate.test(path1)) { return value1; }
            if (pathPredicate.test(path2)) { return value2; }
            return path1.compareTo(path2);
        });
        paths.forEach(System.out::println);
        System.out.println();
        Assertions.assertTrue(pathPredicate.test(paths.get(index)));

        paths.stream()
            .filter(pathPredicate)
            .reduce(
                (path, path2) -> {
                    Assertions.assertTrue(path.compareTo(path2) < 0);
                    return paths.get(0);
                }
            );
    }

    private int firstIndex(List<Path> paths) { return 0; }
    private int lastIndex(List<Path> paths) { return paths.size() - 1; }

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