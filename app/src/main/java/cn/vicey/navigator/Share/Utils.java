package cn.vicey.navigator.Share;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils
{
    private final static long START_TIME = new Date().getTime();
    private final static String DATE_PATTERN = "yyyy-MM-dd";
    private final static String TIME_PATTERN = "HH:mm:ss";

    private Utils()
    {
        // no-op
    }

    /**
     * Get elapsed time from application start.
     *
     * @return Elapsed time in milliseconds.
     */
    public static long getElapsedTime()
    {
        return new Date().getTime() - START_TIME;
    }

    /**
     * Get current date string in a format of "yyyy-MM-dd".
     *
     * @return Date string.
     */
    public static String getCurrentDateString()
    {
        return new SimpleDateFormat(DATE_PATTERN).format(new Date());
    }

    /**
     * Get current time string in a format of "HH:mm:ss"
     *
     * @return
     */
    public static String getCurrentTimeString()
    {
        return new SimpleDateFormat(TIME_PATTERN).format(new Date());
    }
}
