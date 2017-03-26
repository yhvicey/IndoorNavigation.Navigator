package cn.vicey.navigator.Share;


import android.util.Log;
import cn.vicey.navigator.Navigator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Logger class.
 */
public class Logger
{
    private static final String LOGGER_TAG = "Logger";
    private static final String LOG_TEMPLATE = "[%s][%d][%s] %s";
    private static final String DEBUG_HEADER = "DEBUG";
    private static final String INFO_HEADER = "INFO";
    private static final String ERROR_HEADER = "ERROR";
    private static final String LOG_DIR = "/logs/";
    private static final String NEW_LINE = "\n";
    private static final int SAVE_INTERNAL = 1000;

    private static String mLogDirFullPath = Navigator.getDataDirFullPath() + LOG_DIR;
    private static String mLogFileFullPath = mLogDirFullPath + "Navigator.log";
    private static final List<String> mBuffer = new ArrayList<>();
    private static final Timer mAutoSaver = new Timer();
    private static final TimerTask mAutoSaveTask = new TimerTask()
    {
        @Override
        public void run()
        {
            saveToFile();
        }
    };

    private static boolean mFileLogEnabled = false;

    static
    {
        initialize();
    }

    private static void initialize()
    {
        try
        {
            File logDir = new File(mLogDirFullPath);
            File logFile = new File(mLogFileFullPath);
            if (!(logDir.exists() || logDir.mkdir()))
            {
                error(LOGGER_TAG, "Failed to create log dir. File log disabled.");
                mFileLogEnabled = false;
            }
            if (!(logFile.exists() || logFile.createNewFile()))
            {
                error(LOGGER_TAG, "Failed to create log file. File log disabled.");
                mFileLogEnabled = false;
            }
            mFileLogEnabled = true;
            info(LOGGER_TAG, "File log enabled.");
            mAutoSaver.schedule(mAutoSaveTask, 0, SAVE_INTERNAL);
        }
        catch (Throwable t)
        {
            error(LOGGER_TAG, "Failed to create log file.");
            t.printStackTrace();
        }
    }

    private Logger()
    {
        // no-op
    }

    public static void saveToFile()
    {
        if (!mFileLogEnabled) return;
        try
        {
            synchronized (mBuffer)
            {
                StringBuilder sb = new StringBuilder(mBuffer.size());
                for (String str : mBuffer)
                {
                    sb.append(str + "\n");
                }

                FileOutputStream out = new FileOutputStream(mLogFileFullPath, true);
                out.write(sb.toString().getBytes("utf-8"));
                out.close();

                mBuffer.clear();
            }
        }
        catch (Throwable t)
        {
            error(LOGGER_TAG, "Failed to save log record.");
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
        String msg = String.format(LOG_TEMPLATE, Utils.getCurrentDateTimeString(), Utils.getElapsedTime(), DEBUG_HEADER, message);
        StringBuilder sb = new StringBuilder(2);
        sb.append(msg).append(NEW_LINE);
        if (t != null)
        {
            sb.append(t).append(NEW_LINE);
        }
        Log.d(tag, sb.toString());
        if (mFileLogEnabled)
        {
            mBuffer.add(sb.toString());
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
        String msg = String.format(LOG_TEMPLATE, Utils.getCurrentDateTimeString(), Utils.getElapsedTime(), INFO_HEADER, message);
        StringBuilder sb = new StringBuilder(2);
        sb.append(msg).append(NEW_LINE);
        if (t != null)
        {
            sb.append(t).append(NEW_LINE);
        }
        Log.i(tag, sb.toString());
        if (mFileLogEnabled)
        {
            mBuffer.add(sb.toString());
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
        String msg = String.format(LOG_TEMPLATE, Utils.getCurrentDateTimeString(), Utils.getElapsedTime(), ERROR_HEADER, message);
        StringBuilder sb = new StringBuilder(2);
        sb.append(msg).append(NEW_LINE);
        if (t != null)
        {
            sb.append(t).append(NEW_LINE);
        }
        Log.e(tag, sb.toString());
        if (mFileLogEnabled)
        {
            mBuffer.add(sb.toString());
        }
    }
}
