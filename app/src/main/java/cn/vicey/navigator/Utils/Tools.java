package cn.vicey.navigator.Utils;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Navigator;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Tool class, provides a set of useful methods to use in various scenario
 */
public final class Tools
{
    //region Inner classes

    public interface OnDownloadListener
    {
        void onDownloadFailed();

        void onDownloadSucceed(final @NonNull File file);
    }

    //endregion

    //region Constants

    private static final String LOGGER_TAG = "Tools";

    private static final String DATE_PATTERN     = "yyyy-MM-dd"; // Date string pattern
    private static final double DOUBLE_PRECISION = 0.0000001;    // Double equality check precision
    private static final String TIME_PATTERN     = "HH:mm:ss";   // Time string pattern

    /**
     * File encoding
     */
    public static final String FILE_ENCODING = "utf-8";
    /**
     * New line string
     */
    public static final String NEW_LINE      = System.getProperty("line.separator");

    //endregion

    //region Static methods

    /**
     * Copy a file from source to destination
     *
     * @param src         Source file
     * @param dest        Destination
     * @param overwritten Whether the existing file should be overwritten
     * @return Whether the saving is succeed or not
     */
    public static boolean copyFile(final @NonNull File src, final @NonNull File dest, boolean overwritten)
    {
        try
        {
            if (!(src.exists() && src.isFile())) return false;
            if (dest.exists())
            {
                if (!(overwritten && dest.isFile())) return false;
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

    /**
     * Download a file from specified url
     *
     * @param urlString Url to download
     * @param listener  Download listener
     */
    public static void downloadFile(final @NonNull String urlString, final OnDownloadListener listener)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Logger.info(LOGGER_TAG, "Started trying to download " + urlString + ".");
                    long startTime = new Date().getTime();
                    URL url = new URL(urlString);
                    String fileName = url.getFile();
                    File dir = new File(Navigator.getCacheDirPath());
                    if (Tools.isStringEmpty(fileName))
                    {
                        int index = urlString.lastIndexOf('/');
                        if (index == -1 || index == urlString.length() - 1)
                            fileName = Tools.getAvailableDefaultName(dir);
                        else fileName = urlString.substring(index + 1);
                    }
                    File file = new File(dir + fileName);
                    if (!(file.exists() || file.createNewFile()))
                    {
                        Logger.error(LOGGER_TAG, "Failed to create download file.");
                        if (listener != null) listener.onDownloadFailed();
                        return;
                    }
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    StringBuilder stringBuilder = new StringBuilder();
                    InputStream stream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String tmp;
                    while ((tmp = reader.readLine()) != null)
                    {
                        stringBuilder.append(tmp).append(Tools.NEW_LINE);
                    }
                    FileOutputStream fos = new FileOutputStream(file, false);
                    fos.write(stringBuilder.toString().getBytes(Tools.FILE_ENCODING));
                    fos.close();
                    Logger.info(LOGGER_TAG, "Finished download " + urlString + ". Total time: " + (new Date().getTime() - startTime) + " ms.");
                    if (listener != null) listener.onDownloadSucceed(file);
                }
                catch (Throwable t)
                {
                    Logger.error(LOGGER_TAG, "Failed to download file. Url: " + urlString, t);
                    if (listener != null) listener.onDownloadFailed();
                }
            }
        }).start();
    }

    /**
     * Gets available default name in specified directory
     *
     * @param dir Specified directory
     * @return Available name
     */
    public static String getAvailableDefaultName(final @NonNull File dir)
    {
        return getAvailableDefaultName(dir, "");
    }

    /**
     * Gets available default name in specified directory
     *
     * @param dir        Specified directory
     * @param fileSuffix File suffix
     * @return Available name
     */
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
     * Gets current date string in a format of "yyyy-MM-dd"
     *
     * @return Date string
     */
    public static String getCurrentDateString()
    {
        return new SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(new Date());
    }

    /**
     * Gets current datetime string in a format of "yyyy-MM-dd HH:mm:ss"
     *
     * @return Datetime string
     */
    public static String getCurrentDateTimeString()
    {
        return getCurrentDateString() + " " + getCurrentTimeString();
    }

    /**
     * Gets current time string in a format of "HH:mm:ss"
     *
     * @return Time string
     */
    public static String getCurrentTimeString()
    {
        return new SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(new Date());
    }

    /**
     * Gets all directories under specified directory
     *
     * @param dir           Specified directory
     * @param includeParent Whether the list should include parent folder ("..")
     * @return List of directories, or null if dir is not a directory, or error occurred
     */
    public static List<File> getDirs(final @NonNull File dir, boolean includeParent)
    {
        if (!dir.isDirectory()) return null;
        try
        {
            List<File> result = new ArrayList<>();
            for (File file : dir.listFiles())
            {
                if (file.isDirectory()) result.add(file);
            }
            //noinspection ComparatorCombinators
            Collections.sort(result, new Comparator<File>()
            {
                @Override
                public int compare(File file1, File file2)
                {
                    return file1.getName().compareTo(file2.getName());
                }
            });
            if (dir.getParent() != null && includeParent) result.add(0, new File(".."));
            return result;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to get all files under directory " + dir + ".", t);
            return null;
        }
    }

    /**
     * Gets elapsed time from application start
     *
     * @return Elapsed time in milliseconds
     */
    public static long getElapsedTime()
    {
        return new Date().getTime() - Navigator.getStartTime();
    }

    /**
     * Gets all entries under specified directory
     *
     * @param dir           Specified directory
     * @param includeParent Whether the list should include parent folder ("..")
     * @return List of entries, or null if error occurred
     */
    public static List<File> getEntries(final @NonNull File dir, boolean includeParent)
    {
        if (!dir.isDirectory()) return null;
        try
        {
            List<File> result = new ArrayList<>();
            result.addAll(getDirs(dir, includeParent));
            result.addAll(getFiles(dir));
            return result;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to get all files under directory " + dir + ".", t);
            return null;
        }
    }

    /**
     * Gets all files under specified directory
     *
     * @param dir Specified directory
     * @return List of files, or null if dir is not a directory, or error occurred
     */
    public static List<File> getFiles(final @NonNull File dir)
    {
        if (!dir.isDirectory()) return null;
        try
        {
            List<File> result = new ArrayList<>();
            for (File file : dir.listFiles())
            {
                if (file.isFile()) result.add(file);
            }
            //noinspection ComparatorCombinators
            Collections.sort(result, new Comparator<File>()
            {
                @Override
                public int compare(File file1, File file2)
                {
                    return file1.getName().compareTo(file2.getName());
                }
            });
            return result;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to get all files under directory " + dir + ".", t);
            return null;
        }
    }

    /**
     * Check equality of two double value equal
     *
     * @param left  Left double value
     * @param right Right double value
     * @return Are these two double value equal or not
     */
    public static boolean isDoubleEqual(double left, double right)
    {
        return isDoubleEqual(left, right, DOUBLE_PRECISION);
    }

    /**
     * Check equality of two double value equal
     *
     * @param left      Left double value
     * @param right     Right double value
     * @param precision Equality check precision
     * @return Are these two double value equal or not
     */
    public static boolean isDoubleEqual(double left, double right, double precision)
    {
        return Math.abs(left - right) < precision;
    }

    /**
     * Check whether the string is empty
     *
     * @param str String to check
     * @return Whether the string is empty
     */
    public static boolean isStringEmpty(final String str)
    {
        return isStringEmpty(str, true);
    }


    /**
     * Check whether the string is empty
     *
     * @param str                    String to check
     * @param treatWhitespaceAsEmpty Treat whitespace string as empty string
     * @return Whether the string is empty
     */
    public static boolean isStringEmpty(final String str, boolean treatWhitespaceAsEmpty)
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

    //endregion

    //region Constructors

    /**
     * Hidden for static class design pattern
     */
    private Tools()
    {
        // no-op
    }

    //endregion
}
