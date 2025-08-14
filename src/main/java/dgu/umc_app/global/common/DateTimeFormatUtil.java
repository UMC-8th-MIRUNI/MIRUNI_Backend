package dgu.umc_app.global.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateTimeFormatUtil {

    // 오전/오후 hh:mm formatter
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREAN);

    public static String formatTime(LocalDateTime dateTime) {
        return dateTime.format(TIME_FORMATTER);
    }

    // yyyy.MM.dd formatter
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

    // yyyy.MM.dd 오전/오후 hh:mm formatter
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd a hh:mm", Locale.KOREAN);

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
