package org.effective_mobile.task_management_system.build;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Properties;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildFunctions {
    static String substitute(Properties properties, String propertyValue) {
        String regex = "\\$\\{([A-Za-z0-9.]+)}";
        String[] keys = Pattern.compile(regex)
            .matcher(propertyValue)
            .results()
            .map(MatchResult::group)
            .map(key -> key.replaceAll("[${}]", ""))
            .toArray(String[]::new);

        Object[] values = new String[keys.length];

        for (int i = 0; i < keys.length; i++) {
            values[i] = properties.get(keys[i]);
        }

        String template = propertyValue.replaceAll(regex, "%s");
        return template.formatted(values);
    }

    public static String getRegexForFormat(String format) {
        return format.replaceAll("[YMdHms]","\\\\d");
    }


    public static Integer lastMigrationNumber(Path path, String regex, String prefix) throws IOException {
        var migrationFilename = Files
            .walk(path)
            .filter(it -> it.toFile().getName().startsWith(prefix))
            .max(Comparator.naturalOrder())
            .orElseThrow(() -> new RuntimeException("There is no files start with %s in %s".formatted(prefix, path)))
            .getFileName()
            .toString();

        Matcher matcher = Pattern
            .compile(regex)
            .matcher(migrationFilename);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group(1));
        }

        throw new RuntimeException("There is no found match for: regex - %s, string - %s".formatted(regex, migrationFilename));
    }

    static class TagJsonPojo {
        public TagJsonPojo() {}
        public TagJsonPojo(String name, String date) {
            this.name = name;
            this.date = date;
        }

        public String getName() {
            return name;
        }

        @JsonProperty String name;
        @JsonProperty String date;
    }
}
