package com.gpsgetwoweducation.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    // Define SimpleDateFormat as a static final field
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("h:mm a, dd MMM yyyy", Locale.getDefault());

    public static String formatDateTime(String inputDateTime) {
        try {
            // Parse the input date string
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(inputDateTime);

            // Format the date using the global DATE_FORMATTER
            return DATE_FORMATTER.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Handle the error or return a default value
        }
    }
}
