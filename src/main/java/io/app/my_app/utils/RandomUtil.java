package io.app.my_app.utils;

import java.time.LocalDateTime;

public class RandomUtil {
    public static String randomRefNumber(String prefix) {
        long epochMilli = LocalDateTime.now().atZone(Constants.DEFAULT_ZONE_OFFSET).toInstant().toEpochMilli();
        return prefix + epochMilli;
    }

    public static String randomRefNumber() {
        long epochMilli = LocalDateTime.now().atZone(Constants.DEFAULT_ZONE_OFFSET).toInstant().toEpochMilli();
        return String.valueOf(epochMilli);
    }
}
