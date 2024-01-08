package org.effective_mobile.task_management_system.build;

import java.util.Properties;
import java.util.regex.MatchResult;
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
}
