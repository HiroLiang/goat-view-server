package com.hiro.util.methods;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    /**
     * transfer from string to LocalDataTime with pattern
     * @param dateStr date time string
     * @param pattern pa
     * @return LocalDateTime
     */
    public static LocalDateTime fromStrWithPattern(String dateStr, String pattern) {
        if ((pattern.contains("H") || pattern.contains("h")) && pattern.contains("m")) {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        } else {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern)).atStartOfDay();
        }
    }

}
