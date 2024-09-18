package tests.api.utils.wrappers;

import lombok.SneakyThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class ParseDate {
    @SneakyThrows
    public static Date parseDateFromStringToDate(String dateStr, String formatPattern){
        SimpleDateFormat formatter = new SimpleDateFormat(formatPattern);
        return formatter.parse(dateStr);
    }

    public static List<Date> parseListDateFromStringToDate(List<String> listStrings, String formatPattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatPattern);

        return listStrings.stream()
                .map(dateStr -> {
                    try {
                        return formatter.parse(dateStr);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
