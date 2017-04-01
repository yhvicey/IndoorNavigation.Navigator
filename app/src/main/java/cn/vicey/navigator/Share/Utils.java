package cn.vicey.navigator.Share;

import android.support.annotation.NonNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Utils
{
    public interface DownloadCallback
    {
        void onDownloadFinished(boolean succeed);
    }

    private static final String LOGGER_TAG = "Utils";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String NEW_LINE = "\n";
    private static final String FILE_ENCODING = "utf-8";

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

    public static void downloadFile(final @NonNull String urlString, final @NonNull String filePath, final @NonNull DownloadCallback callback)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    StringBuilder stringBuilder = new StringBuilder();
                    InputStream stream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String tmp;
                    while ((tmp = reader.readLine()) != null)
                    {
                        stringBuilder.append(tmp).append(NEW_LINE);
                    }
                    File file = new File(filePath);
                    if (!file.exists()) file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file, false);
                    fos.write(stringBuilder.toString().getBytes(FILE_ENCODING));
                    fos.close();
                    callback.onDownloadFinished(true);
                }
                catch (Throwable t)
                {
                    Logger.error(LOGGER_TAG, "Failed to download file. Url: " + urlString, t);
                    callback.onDownloadFinished(false);
                }
            }
        }).start();
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

    public static boolean isDoubleEqual(double left, double right)
    {
        return isDoubleEqual(left, right, 0.0000001);
    }

    public static boolean isDoubleEqual(double left, double right, double precision)
    {
        return Math.abs(left - right) < precision;
    }

    public static boolean isStringEmpty(String str)
    {
        return isStringEmpty(str, true);
    }

    public static boolean isStringEmpty(String str, boolean treatWhitespaceAsEmpty)
    {
        if (str == null) return true;
        if (str.length() == 0) return true;
        if (!treatWhitespaceAsEmpty) return false;
        for (int i = 0; i < str.length(); i++)
        {
            if (str.charAt(i) != ' ') return false;
        }
        return true;
    }
}
