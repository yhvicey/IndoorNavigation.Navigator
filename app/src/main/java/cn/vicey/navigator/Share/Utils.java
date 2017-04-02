package cn.vicey.navigator.Share;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Navigator;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class Utils
{
    public interface DownloadCallback
    {
        void onDownloadSucceed(final @NonNull String filePath);

        void onDownloadFailed();
    }

    private static final String LOGGER_TAG = "Utils";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";

    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String FILE_ENCODING = "utf-8";

    private Utils()
    {
        // no-op
    }

    public static boolean copyFile(final @NonNull File src, final @NonNull File dest, boolean overwrite)
    {
        try
        {
            if (!(src.exists() && src.isFile())) return false;
            if (dest.exists())
            {
                if (!(overwrite && dest.isFile())) return false;
            }
            FileInputStream fis = new FileInputStream(src);
            FileOutputStream fos = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int len;
            while ((len = fis.read(buf)) > 0)
            {
                fos.write(buf, 0, len);
            }
            fis.close();
            fos.close();
            return true;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to copy file. Src: " + src.getAbsolutePath() + ", dest: " + dest.getAbsolutePath() + ".", t);
            return false;
        }
    }

    public static void downloadFile(final @NonNull String urlString, final DownloadCallback callback)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                DownloadCallback realCallback = callback != null ? callback : new DownloadCallback()
                {
                    @Override
                    public void onDownloadSucceed(@NonNull String filePath)
                    {

                    }

                    @Override
                    public void onDownloadFailed()
                    {

                    }
                };
                try
                {
                    Logger.info(LOGGER_TAG, "Started trying to download " + urlString + ".");
                    long startTime = new Date().getTime();
                    URL url = new URL(urlString);
                    String fileName = url.getFile();
                    File dir = new File(Navigator.getCacheDirPath());
                    if (Utils.isStringEmpty(fileName))
                    {
                        int index = urlString.lastIndexOf('/');
                        if (index == -1 || index == urlString.length() - 1)
                            fileName = Utils.getAvailableDefaultName(dir);
                        else fileName = urlString.substring(index + 1);
                    }
                    File file = new File(dir + fileName);
                    if (!(file.exists() || file.createNewFile()))
                    {
                        Logger.error(LOGGER_TAG, "Failed to create download file.");
                        realCallback.onDownloadFailed();
                        return;
                    }
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    StringBuilder stringBuilder = new StringBuilder();
                    InputStream stream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String tmp;
                    while ((tmp = reader.readLine()) != null)
                    {
                        stringBuilder.append(tmp).append(Utils.NEW_LINE);
                    }
                    FileOutputStream fos = new FileOutputStream(file, false);
                    fos.write(stringBuilder.toString().getBytes(Utils.FILE_ENCODING));
                    fos.close();
                    Logger.info(LOGGER_TAG, "Finished download " + urlString + ". Total time: " + (new Date().getTime() - startTime) + " ms.");
                    realCallback.onDownloadSucceed(file.getAbsolutePath());
                }
                catch (Throwable t)
                {
                    Logger.error(LOGGER_TAG, "Failed to download file. Url: " + urlString, t);
                    realCallback.onDownloadFailed();
                }
            }
        }).start();
    }

    public static boolean isDoubleEqual(double left, double right)
    {
        return isDoubleEqual(left, right, 0.0000001);
    }

    public static boolean isDoubleEqual(double left, double right, double precision)
    {
        return Math.abs(left - right) < precision;
    }

    public static boolean isStringEmpty(final @NonNull String str)
    {
        return isStringEmpty(str, true);
    }

    public static boolean isStringEmpty(final @NonNull String str, boolean treatWhitespaceAsEmpty)
    {
        if (str.length() == 0) return true;
        if (!treatWhitespaceAsEmpty) return false;
        for (int i = 0; i < str.length(); i++)
        {
            if (str.charAt(i) != ' ') return false;
        }
        return true;
    }

    public static String getAvailableDefaultName(final @NonNull File dir)
    {
        return getAvailableDefaultName(dir, "");
    }

    public static String getAvailableDefaultName(final @NonNull File dir, final @NonNull String fileSuffix)
    {
        int suffix = 1;
        while (new File(dir.getAbsolutePath() + "untitled" + suffix + fileSuffix).exists())
        {
            suffix++;
        }
        return "untitled" + suffix + fileSuffix;
    }

    /**
     * Get current date string in a format of "yyyy-MM-dd".
     *
     * @return Date string.
     */
    public static String getCurrentDateString()
    {
        return new SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(new Date());
    }

    /**
     * Get current time string in a format of "HH:mm:ss"
     *
     * @return
     */
    public static String getCurrentTimeString()
    {
        return new SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(new Date());
    }

    /**
     * Get current datetime string in a format of "yyyy-MM-dd HH:mm:ss"
     *
     * @return
     */
    public static String getCurrentDateTimeString()
    {
        return getCurrentDateString() + " " + getCurrentTimeString();
    }

    public static List<String> getDirs(final @NonNull File dir)
    {
        if (!dir.isDirectory()) return null;
        try
        {
            List<String> result = new ArrayList<>();
            for (File file : dir.listFiles())
            {
                if (file.isDirectory()) result.add(file.getName());
            }
            return result;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to get all files under directory " + dir + ".", t);
            return null;
        }
    }

    /**
     * Get elapsed time from application start.
     *
     * @return Elapsed time in milliseconds.
     */
    public static long getElapsedTime()
    {
        return new Date().getTime() - Navigator.getStartTime();
    }

    public static List<String> getEntries(final @NonNull File dir)
    {
        if (!dir.isDirectory()) return null;
        try
        {
            List<String> result = new ArrayList<>();
            for (File file : dir.listFiles())
            {
                result.add(file.getName());
            }
            return result;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to get all files under directory " + dir + ".", t);
            return null;
        }
    }

    public static List<String> getFiles(final @NonNull File dir)
    {
        if (!dir.isDirectory()) return null;
        try
        {
            List<String> result = new ArrayList<>();
            for (File file : dir.listFiles())
            {
                if (file.isFile()) result.add(file.getName());
            }
            return result;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to get all files under directory " + dir + ".", t);
            return null;
        }
    }
}
