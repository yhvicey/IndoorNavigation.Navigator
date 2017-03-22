package cn.vicey.navigator.Share;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Logger class.
 */
public class Logger
{
    private final static String LOG_TEMPLATE = "[%d][%s] %s";
    private final static String DEBUG_HEADER = "DEBUG";
    private final static String INFO_HEADER = "INFO";
    private final static String ERROR_HEADER = "ERROR";
    private final static String LOG_DIR = Environment.getDataDirectory() + "/log";
    private final static String LOG_FILE = LOG_DIR + Utils.getCurrentDateString() + ".log";
    private final static int SAVE_THRESHOLD = 100;

    private final static List<String> mBuffer = new ArrayList<>();

    private static boolean mFileLogEnabled = false;

    static
    {
        try
        {
            File file = new File(LOG_DIR);
            if (!(file.exists() || file.mkdir()))
            {
                Log.e("Logger", "Failed to create log dir.");
                mFileLogEnabled = false;
            }
            mFileLogEnabled = true;
        }
        catch (Throwable t)
        {
            Log.e("Logger", "Failed to create log dir.");
            t.printStackTrace();
        }
    }

    private Logger()
    {
        // no-op
    }

    private static void saveToFile()
    {
        if (!mFileLogEnabled) return;
        try
        {
            synchronized (mBuffer)
            {
                File file = new File(LOG_FILE);
                if (!(file.exists() || file.createNewFile()))
                {
                    Log.e("Logger", "Failed to create log file.");
                }

                StringBuilder sb = new StringBuilder(mBuffer.size());
                for (String str : mBuffer)
                {
                    sb.append(str);
                }

                FileOutputStream out = new FileOutputStream(file, true);
                out.write(sb.toString().getBytes("utf-8"));
                out.close();

                mBuffer.clear();
            }
        }
        catch (Throwable t)
        {
            Log.e("Logger", "Failed to save log record.");
            t.printStackTrace();
        }
    }

    /**
     * Log debug messages with timestamp.
     *
     * @param tag     Log tag.
     * @param message Message to log.
     */
    public static void debug(String tag, String message)
    {
        debug(tag, message, null);
    }

    /**
     * Log debug messages with timestamp.
     *
     * @param tag     Log tag.
     * @param message Message to log.
     * @param t       Error or exception to log.
     */
    public static void debug(String tag, String message, Throwable t)
    {
        String msg = String.format(LOG_TEMPLATE, Utils.getElapsedTime(), DEBUG_HEADER, message);
        if (t == null)
        {
            Log.d(tag, msg);
        }
        else
        {
            Log.d(tag, msg, t);
        }
        if (mFileLogEnabled)
        {
            StringBuilder sb = new StringBuilder(2);
            sb.append(msg);
            sb.append(t);
            mBuffer.add(sb.toString());
            if (mBuffer.size() >= SAVE_THRESHOLD)
            {
                saveToFile();
            }
        }
    }

    /**
     * Log info messages with timestamp.
     *
     * @param tag     Log tag.
     * @param message Message to log.
     */
    public static void info(String tag, String message)
    {
        info(tag, message, null);
    }

    /**
     * Log info messages with timestamp.
     *
     * @param tag     Log tag.
     * @param message Message to log.
     * @param t       Error or exception to log.
     */
    public static void info(String tag, String message, Throwable t)
    {
        String msg = String.format(LOG_TEMPLATE, Utils.getElapsedTime(), INFO_HEADER, message);
        if (t == null)
        {
            Log.i(tag, msg);
        }
        else
        {
            Log.i(tag, msg, t);
        }
        if (mFileLogEnabled)
        {
            StringBuilder sb = new StringBuilder(2);
            sb.append(msg);
            sb.append(t);
            mBuffer.add(sb.toString());
            if (mBuffer.size() >= SAVE_THRESHOLD)
            {
                saveToFile();
            }
        }
    }

    /**
     * Log error messages with timestamp.
     *
     * @param tag     Log tag.
     * @param message Message to log.
     */
    public static void error(String tag, String message)
    {
        error(tag, message, null);
    }

    /**
     * Log error messages with timestamp.
     *
     * @param tag     Log tag.
     * @param message Message to log.
     * @param t       Error or exception to log.
     */
    public static void error(String tag, String message, Throwable t)
    {
        String msg = String.format(LOG_TEMPLATE, Utils.getElapsedTime(), ERROR_HEADER, message);
        if (t == null)
        {
            Log.i(tag, msg);
        }
        else
        {
            Log.i(tag, msg, t);
        }
        if (mFileLogEnabled)
        {
            StringBuilder sb = new StringBuilder(2);
            sb.append(msg);
            sb.append(t);
            mBuffer.add(sb.toString());
            if (mBuffer.size() >= SAVE_THRESHOLD)
            {
                saveToFile();
            }
        }
    }
}
