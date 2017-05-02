package cn.vicey.navigator.Utils;

import android.util.Log;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.Share.SettingsManager;

import java.io.*;
import java.util.Locale;

/**
 * Logger class, provides a set of methods to log message
 */
public final class Logger
{
    //region Constants

    private static final String LOGGER_TAG = "Logger";

    private static final String DEBUG_HEADER = "DEBUG";           // Debug header
    private static final String ERROR_HEADER = "ERROR";           // Error header
    private static final String INFO_HEADER  = "INFO";            // Info header
    private static final String LOG_DIR      = "/logs/";          // Log directory name
    private static final String LOG_FILE     = "Navigator.log";   // Log file name
    private static final String LOG_TEMPLATE = "[%s][%d][%s] %s"; // Log template

    //endregion

    //region Static fields

    private static File             mLogFile;       // Log file object
    private static FileOutputStream mLogFileStream; // Log file stream

    //endregion

    //region Static methods

    /**
     * Pause the logger
     */
    private static void pause()
    {
        if (mLogFileStream == null) return;
        try
        {
            flush();
            mLogFileStream.close();
            mLogFileStream = null;
        }
        catch (Throwable t)
        {
            mLogFileStream = null;
            error(LOGGER_TAG, "Failed to pause logger. File log disabled.", t);
        }
    }

    /**
     * Resume the logger
     */
    private static void resume()
    {
        if (mLogFileStream != null) return;
        try
        {
            mLogFileStream = new FileOutputStream(mLogFile, true);
        }
        catch (Throwable t)
        {
            mLogFileStream = null;
            error(LOGGER_TAG, "Failed to resume logger. File log disabled.", t);
        }
    }

    /**
     * Write a message to log file
     *
     * @param message Message to write
     */
    private static void write(String message)
    {
        if (mLogFileStream == null) return;
        try
        {
            mLogFileStream.write(message.getBytes(Tools.FILE_ENCODING));
        }
        catch (Throwable t)
        {
            mLogFileStream = null;
            error(LOGGER_TAG, "Failed to write to file. File log disabled.", t);
        }
    }

    /**
     * Log debug messages with timestamp
     *
     * @param tag     Log tag
     * @param message Message to log
     */
    public static void debug(String tag, String message)
    {
        debug(tag, message, null);
    }

    /**
     * Log debug messages with timestamp
     *
     * @param tag     Log tag
     * @param message Message to log
     * @param t       Error or exception to log
     */
    public static void debug(String tag, String message, Throwable t)
    {
        if (!SettingsManager.isDebugModeEnabled()) return;
        String msg = String.format(Locale.getDefault(), LOG_TEMPLATE, Tools.getCurrentDateTimeString(), Tools.getElapsedTime(), DEBUG_HEADER, message) + Tools.NEW_LINE;
        Log.d(tag, msg);
        write(msg);
        if (t == null) return;
        debug(tag, t.getMessage());
        for (StackTraceElement stackTraceElement : t.getStackTrace()) debug(tag, stackTraceElement.toString());
    }

    /**
     * Log error messages with timestamp
     *
     * @param tag     Log tag
     * @param message Message to log
     */
    public static void error(String tag, String message)
    {
        error(tag, message, null);
    }

    /**
     * Log error messages with timestamp
     *
     * @param tag     Log tag
     * @param message Message to log
     * @param t       Error or exception to log
     */
    public static void error(String tag, String message, Throwable t)
    {
        String msg = String.format(Locale.getDefault(), LOG_TEMPLATE, Tools.getCurrentDateTimeString(), Tools.getElapsedTime(), ERROR_HEADER, message) + Tools.NEW_LINE;
        Log.e(tag, msg);
        write(msg);
        if (t == null) return;
        error(tag, t.getMessage());
        for (StackTraceElement stackTraceElement : t.getStackTrace()) error(tag, stackTraceElement.toString());
    }

    /**
     * Flush the log file stream
     */
    public static void flush()
    {
        if (mLogFileStream == null) return;
        try
        {
            mLogFileStream.flush();
        }
        catch (Throwable t)
        {
            mLogFileStream = null;
            error(LOGGER_TAG, "Failed to flush logger. File log disabled.", t);
        }
    }

    /**
     * Gets log content in disk
     *
     * @return Log content
     */
    public static String getLogContent()
    {
        try
        {
            pause();
            FileInputStream fis = new FileInputStream(mLogFile);
            InputStreamReader inputReader = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(inputReader);
            String str;
            StringBuilder stringBuilder = new StringBuilder();
            while ((str = reader.readLine()) != null) stringBuilder.append(str).append(Tools.NEW_LINE);
            reader.close();
            inputReader.close();
            fis.close();
            resume();
            return stringBuilder.toString();
        }
        catch (Throwable t)
        {
            error(LOGGER_TAG, "Failed to get log content.", t);
            resume();
            return null;
        }
    }

    /**
     * Log info messages with timestamp
     *
     * @param tag     Log tag
     * @param message Message to log
     */
    public static void info(String tag, String message)
    {
        info(tag, message, null);
    }

    /**
     * Log info messages with timestamp
     *
     * @param tag     Log tag
     * @param message Message to log
     * @param t       Error or exception to log
     */
    public static void info(String tag, String message, Throwable t)
    {
        String msg = String.format(Locale.getDefault(), LOG_TEMPLATE, Tools.getCurrentDateTimeString(), Tools.getElapsedTime(), INFO_HEADER, message) + Tools.NEW_LINE;
        Log.i(tag, msg);
        write(msg);
        if (t == null) return;
        info(tag, t.getMessage());
        for (StackTraceElement stackTraceElement : t.getStackTrace()) info(tag, stackTraceElement.toString());
    }

    /**
     * Initialize logger
     *
     * @return Whether the initialization is succeed or not
     */
    public static boolean init()
    {
        try
        {
            String logDirPath = Navigator.getFilesDirPath() + LOG_DIR;
            String logFilePath = logDirPath + LOG_FILE;
            File logDir = new File(logDirPath);
            if (!(logDir.exists() || logDir.mkdir()))
            {
                error(LOGGER_TAG, "Failed to create log dir. File log disabled.");
                return false;
            }
            mLogFile = new File(logFilePath);
            if (!(mLogFile.exists() || mLogFile.createNewFile()))
            {
                error(LOGGER_TAG, "Failed to create log file. File log disabled.");
                return false;
            }
            resume();
            return true;
        }
        catch (Throwable t)
        {
            error(LOGGER_TAG, "Failed to create log file.", t);
            return false;
        }
    }

    //endregion

    //region Constructors

    /**
     * Hidden for static class design pattern
     */
    private Logger()
    {
        // no-op
    }

    //endregion
}
