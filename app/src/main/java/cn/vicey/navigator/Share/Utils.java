package cn.vicey.navigator.Share;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Utils
{
    private static final String LOGGER_TAG = "Utils";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";

    private static long mStartTime = 0;

    public static boolean initialize()
    {
        mStartTime = new Date().getTime();
        return true;
    }

    private Utils()
    {
        // no-op
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

    /**
     * Get current datetime string in a format of "yyyy-MM-dd HH:mm:ss"
     *
     * @return
     */
    public static String getCurrentDateTimeString()
    {
        return new SimpleDateFormat(DATE_PATTERN + " " + TIME_PATTERN).format(new Date());
    }

    /**
     * Get elapsed time from application start.
     *
     * @return Elapsed time in milliseconds.
     */
    public static long getElapsedTime()
    {
        return new Date().getTime() - mStartTime;
    }
}
