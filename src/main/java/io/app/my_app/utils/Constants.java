package io.app.my_app.utils;

import java.time.ZoneOffset;
import java.util.Locale;

public interface Constants {
    /**
     * Default Pagination Page Number
     */
    String DEFAULT_PAGE_NUMBER = "1";


    /**
     * Default Pagination Page Size
     */
    String DEFAULT_PAGE_SIZE = "100";


    /**
     * Maximum Page Size
     */
    int MAX_PAGE_SIZE = 100;

    Locale DEFAULT_LOCALE = Locale.ROOT;

    ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.of("+02:00");
}