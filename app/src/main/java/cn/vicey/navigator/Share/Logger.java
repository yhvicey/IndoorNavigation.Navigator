package cn.vicey.navigator.Share;


import android.util.Log;
import cn.vicey.navigator.Navigator;

import java.io.*;

/**
 * Logger class.
 */
public class Logger
{
    private static final String LOGGER_TAG = "Logger";
    private static final String LOG_DIR = "/logs/";
    private static final String LOG_FILE = "Navigator.log";
    private static final String LOG_TEMPLATE = "[%s][%d][%s] %s";
    private static final String DEBUG_HEADER = "DEBUG";
    private static final String INFO_HEADER = "INFO";
    private static final String ERROR_HEADER = "ERROR";

    private static File mLogFile;
    private static FileOutputStream mLogFileStream;

    public static boolean initialize()
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

    private Logger()
    {
        // no-op
    }

    private static void write(String message)
    {
        if (mLogFileStream == null) return;
        try
        {
            mLogFileStream.write(message.getBytes(Utils.FILE_ENCODING));
        }
        catch (Throwable t)
        {
            mLogFileStream = null;
            error(LOGGER_TAG, "Failed to write to file. File log disabled.", t);
        }
    }

    private static void pause()
    {
        if (mLogFileStream == null) return;
        try
        {
            flush();
            mLogFileStream = null;
        }
        catch (Throwable t)
        {
            mLogFileStream = null;
            error(LOGGER_TAG, "Failed to pause logger. File log disabled.", t);
        }
    }

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
        sb.append(String.format(Settings.getCurrentLocale(), LOG_TEMPLATE, Utils.getCurrentDateTimeString(), Utils.getElapsedTime(), DEBUG_HEADER, message))
          .append(Utils.NEW_LINE);
        if (t != null)
        {
            sb.append(t).append(Utils.NEW_LINE);
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
        sb.append(String.format(Settings.getCurrentLocale(), LOG_TEMPLATE, Utils.getCurrentDateTimeString(), Utils.getElapsedTime(), INFO_HEADER, message))
          .append(Utils.NEW_LINE);
        if (t != null)
        {
            sb.append(t).append(Utils.NEW_LINE);
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
        sb.append(String.format(Settings.getCurrentLocale(), LOG_TEMPLATE, Utils.getCurrentDateTimeString(), Utils.getElapsedTime(), ERROR_HEADER, message))
          .append(Utils.NEW_LINE);
        if (t != null)
        {
            sb.append(t).append(Utils.NEW_LINE);
        }
        String msg = sb.toString();
        Log.e(tag, msg);
        write(msg);
    }

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
            while ((str = reader.readLine()) != null)
            {
                stringBuilder.append(str).append(Utils.NEW_LINE);
            }
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
}
