package org.effective_mobile.task_management_system.security;

import org.effective_mobile.task_management_system.pojo.TimeToLiveInfo;
import org.effective_mobile.task_management_system.security.authentication.JwtAuthTokenComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


/**
 * Test for class {@link JwtAuthTokenComponent}.
 */
class JwtAuthTokenComponentTest {

    private static final int millisInSecond = 1000;
    private static final int secondsInMinute = 60;
    private static final int minutesInHour = 60;
    private static final int hoursInDay = 24;

    private static final int millisInMinute = millisInSecond * secondsInMinute;
    private static final int millisInHour   = millisInMinute * minutesInHour;
    private static final int millisInDays   = millisInHour   * hoursInDay;

    /**
     * Test for {@link JwtAuthTokenComponent#toMillis(TimeToLiveInfo)}.
     */
    @ParameterizedTest
    @MethodSource("toMillisTestData")
    public void toMillisTest(TimeToLiveInfo timeToLiveInfo, long expected) {
        long actual = JwtAuthTokenComponent.toMillis(timeToLiveInfo);
        Assertions.assertEquals(expected, actual);
    }

    public static Stream<Arguments> toMillisTestData() {
        int days = 1;
        int hours = 1;
        int minutes = 5;
        return Stream.of(
            Arguments.of(new TimeToLiveInfo(TimeUnit.DAYS, days), daysToMillis(days)),
            Arguments.of(new TimeToLiveInfo(TimeUnit.HOURS, hours), hoursToMillis(hours)),
            Arguments.of(new TimeToLiveInfo(TimeUnit.MINUTES, minutes), minutesToMillis(minutes))
        );
    }

    private static int hoursToMillis(int hours) {
        return millisInHour * hours;
    }

    private static int minutesToMillis(int minutes) {
        return millisInMinute * minutes;
    }

    private static int daysToMillis(int days) {
        return millisInDays * days;
    }
}