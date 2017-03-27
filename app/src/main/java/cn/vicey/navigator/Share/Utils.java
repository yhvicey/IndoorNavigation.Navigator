package cn.vicey.navigator.Share;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Utils
{
    private static final String LOGGER_TAG = "Utils";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";

    public static final int ERR_SUCCEED = 0;
    public static final int ERR_EXCEPTION = -1;

    private static final long mStartTime = new Date().getTime();

    private Utils()
    {
        // no-op
    }

    public static void exit()
    {
        Logger.saveToFile();
        System.exit(ERR_SUCCEED);
    }

    public static void exitWithError()
    {
        exitWithError(ERR_EXCEPTION);
    }

    public static void exitWithError(int errorCode)
    {
        Logger.error(LOGGER_TAG, "Utils.exitWithError(int errorCode) has been called. Error code: " + errorCode);
        Logger.saveToFile();
        System.exit(errorCode);
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
