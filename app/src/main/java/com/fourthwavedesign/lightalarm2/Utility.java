package com.fourthwavedesign.lightalarm2;

/**
 * Created by reddog on 12/14/14.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fourthwavedesign.lightalarm2.data.AlarmContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Utility {

    static String formatDate(String dateString) {
        Date date = AlarmContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return a user-friendly representation of the date.
     */
    public static String getFriendlyDayString(Context context, String dateStr) {
        // The day string for forecast uses the following logic:
        // For today: "Today, June 8"
        // For tomorrow:  "Tomorrow"
        // For the next 5 days: "Wednesday" (just the day name)
        // For all days after that: "Mon Jun 8"

        Date todayDate = new Date();
        String todayStr = AlarmContract.getDbDateString(todayDate);
        Date inputDate = AlarmContract.getDateFromDb(dateStr);

        // If the date we're building the String for is today's date, the format
        // is "Today, June 24"
        if (todayStr.equals(dateStr)) {
            String today = context.getString(R.string.today);
            //int formatId = R.string.format_full_friendly_date;
            //return String.format(context.getString(
            //       formatId,
            //       today,
            //        getFormattedMonthDay(context, dateStr)));
            return today;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(todayDate);
            cal.add(Calendar.DATE, 7);
            String weekFutureString = AlarmContract.getDbDateString(cal.getTime());

            if (dateStr.compareTo(weekFutureString) < 0) {
                // If the input date is less than a week in the future, just return the day name.
                return getDayName(context, dateStr);
            } else {
                // Otherwise, use the form "Mon Jun 3"
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                return shortenedDateFormat.format(inputDate);
            }
        }
    }

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "wednesday".
     *
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return
     */
    public static String getDayName(Context context, String dateStr) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        try {
            Date inputDate = dbDateFormat.parse(dateStr);
            Date todayDate = new Date();
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.
            if (AlarmContract.getDbDateString(todayDate).equals(dateStr)) {
                return context.getString(R.string.today);
            } else {
                // If the date is set for tomorrow, the format is "Tomorrow".
                Calendar cal = Calendar.getInstance();
                cal.setTime(todayDate);
                cal.add(Calendar.DATE, 1);
                Date tomorrowDate = cal.getTime();
                if (AlarmContract.getDbDateString(tomorrowDate).equals(
                        dateStr)) {
                    return context.getString(R.string.tomorrow);
                } else {
                    // Otherwise, the format is just the day of the week (e.g "Wednesday".
                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                    return dayFormat.format(inputDate);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // It couldn't process the date correctly.
            return "";
        }
    }

    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(Context context, String dateStr) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        try {
            Date inputDate = dbDateFormat.parse(dateStr);
            SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
            String monthDayString = monthDayFormat.format(inputDate);
            return monthDayString;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method to provide the holiday message according to the date text returned
     * @param context Context to use for resource localization
     * @param dateText date value passed in from click
     * @return resource text for the corresponding date. -1 if no relation is found.
     */
    public static Map<String, String> getHoliday(Context context, String dateText) {
        final String LOG_TAG = Utility.class.getSimpleName();
        final String[] holidayList = context.getResources().getStringArray(R.array.holiday_dates);
        final String[] holidayMsgs = context.getResources().getStringArray(R.array.holiday_msgs);
        final String[] holidayImgs = context.getResources().getStringArray(R.array.holiday_imgs);
        // Create a hash map to hold holiday data
        HashMap holiday = new HashMap();

        String holidayMsg = "";
        String holidayImg = "";

        holiday.put("isholiday", "false");
        holiday.put("holidaydate", null);
        holiday.put("message", null);
        holiday.put("image", null);

        for (int i = 0; i < holidayList.length; i++) {

            if (dateText.equals(holidayList[i])) {
                // if there's a matching holiday Put elements to the map
                holiday.put("isholiday", "true");
                //get holiday date
                holiday.put("holidaydate", new String(holidayList[i]));
                //get holiday msg
                holiday.put("message", new String(holidayMsgs[i]));
                //get holiday image
                holiday.put("image", new String(holidayImgs[i]));

//                Log.v(LOG_TAG,"Holiday Date: " + holidayList[i]);
                //               Log.v(LOG_TAG,"Holiday Msg: " + holidayMsgs[i]);
                //              Log.v(LOG_TAG,"Holiday Img: " + holidayImgs[i]);

                break;
            }

        };

        //Log.v(LOG_TAG,"Holiday Return: " + holiday);

        return holiday;
    }
}
