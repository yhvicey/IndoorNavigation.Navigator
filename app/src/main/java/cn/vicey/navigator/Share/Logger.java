package cn.vicey.navigator.Share;


import android.util.Log;
import cn.vicey.navigator.Navigator;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Logger class.
 */
public class Logger
{
    private static final String LOGGER_TAG = "Logger";
    private static final String LOG_DIR = "/logs/";
    private static final String LOG_ENCODING = "utf-8";
    private static final String LOG_TEMPLATE = "[%s][%d][%s] %s";
    private static final String DEBUG_HEADER = "DEBUG";
    private static final String INFO_HEADER = "INFO";
    private static final String ERROR_HEADER = "ERROR";
    private static final String NEW_LINE = "\n";

    private static FileOutputStream mLogFile = null;

    public static boolean initialize()
    {
        try
        {
            String logDirPath = Navigator.getFilesDirPath() + LOG_DIR;
            String logFilePath = logDirPath + "Navigator.log";
            File logDir = new File(logDirPath);
            if (!(logDir.exists() || logDir.mkdir()))
            {
                error(LOGGER_TAG, "Failed to create log dir. File log disabled.");
                return false;
            }
            File logFile = new File(logFilePath);
            if (!(logFile.exists() || logFile.createNewFile()))
            {
                error(LOGGER_TAG, "Failed to create log file. File log disabled.");
                return false;
            }
            mLogFile = new FileOutputStream(logFile);
            info(LOGGER_TAG, "File log enabled.");
            return true;
        }
        catch (Throwable t)
        {
            error(LOGGER_TAG, "Failed to create log file.", t);
            return false;
        }
    }

    private Logger()
    {
        // no-op
    }

    private static void write(String message)
    {
        if (mLogFile == null) return;
        try
        {
            mLogFile.write(message.getBytes(LOG_ENCODING));
        }
        catch (Throwable t)
        {
            mLogFile = null;
            error(LOGGER_TAG, "Failed to write to file. File log disabled.", t);
        }
    }

    public static void flush()
    {
        if (mLogFile == null) return;
        try
        {
            mLogFile.flush();
        }
        catch (Throwable t)
        {
            mLogFile = null;
            error(LOGGER_TAG, "Failed to flush logger. File log disabled.", t);
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
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Settings.getCurrentLocale(), LOG_TEMPLATE, Utils.getCurrentDateTimeString(), Utils.getElapsedTime(), DEBUG_HEADER, message)).append(NEW_LINE);
        if (t != null)
        {
            sb.append(t).append(NEW_LINE);
        }
        String msg = sb.toString();
        Log.d(tag, msg);
        write(msg);
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
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Settings.getCurrentLocale(), LOG_TEMPLATE, Utils.getCurrentDateTimeString(), Utils.getElapsedTime(), INFO_HEADER, message)).append(NEW_LINE);
        if (t != null)
        {
            sb.append(t).append(NEW_LINE);
        }
        String msg = sb.toString();
        Log.i(tag, msg);
        write(msg);
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
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Settings.getCurrentLocale(), LOG_TEMPLATE, Utils.getCurrentDateTimeString(), Utils.getElapsedTime(), ERROR_HEADER, message)).append(NEW_LINE);
        if (t != null)
        {
            sb.append(t).append(NEW_LINE);
        }
        String msg = sb.toString();
        Log.e(tag, msg);
        write(msg);
    }
}
