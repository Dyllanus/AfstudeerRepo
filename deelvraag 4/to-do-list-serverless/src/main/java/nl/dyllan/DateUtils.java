package nl.dyllan;

import nl.dyllan.domain.exceptions.ConvertDateException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static Date convertStringToDate(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new ConvertDateException("The string could not be converted to a date (" + date + ")");
        }
    }

    public static String convertDateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return formatter.format(date);
    }

}
