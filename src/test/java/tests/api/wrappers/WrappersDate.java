package tests.api.wrappers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class WrappersDate {

    public static Date parseDateFromStringToDate(String dateStr, String formatPattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatPattern);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("Ошибка преобразования даты: " + e.getMessage(), e);
        }
    }

    public static List<Date> parseListDateFromStringToDate(List<String> listStrings, String formatPattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatPattern);

        return listStrings.stream()
                .map(dateString -> {
                    try {
                        return formatter.parse(dateString);
                    } catch (ParseException e) {
                        throw new RuntimeException("Ошибка преобразования даты: " + e.getMessage(), e);
                    }
                })
                .toList();
    }
}
