package cn.vicey.navigator;

import android.app.Application;
import cn.vicey.navigator.Debug.FakeLocateManager;
import cn.vicey.navigator.File.MapManager;
import cn.vicey.navigator.Navigate.NavigateManager;
import cn.vicey.navigator.Share.SettingsManager;
import cn.vicey.navigator.Share.TypefaceManager;
import cn.vicey.navigator.Utils.Logger;

import java.util.Date;

/**
 * Extended application class, provides a set of methods to manage application
 */
public class Navigator
        extends Application
{
    //region Constants

    private static final String LOGGER_TAG = "Navigator";

    /**
     * Initialization failed error code
     */
    public static final int ERR_INIT              = -2;
    /**
     * Succeed exit error code
     */
    public static final int ERR_SUCCEED           = 0;
    /**
     * Unexpected action error code
     */
    public static final int ERR_UNEXPECTED_ACTION = -1;

    //endregion

    //region Static fields

    private static String mCacheDir; // Cache directory of this application
    private static String mFilesDir; // Files directory of this application

    private static long mStartTime = new Date().getTime(); // Application start time

    //endregion

    //region Static methods

    /**
     * Exit the application
     */
    public static void exit()
    {
        Logger.info(LOGGER_TAG, "Exit application.");
        Logger.flush();
        System.exit(ERR_SUCCEED);
    }

    /**
     * Exit the application with error
     */
    public static void exitWithError()
    {
        exitWithError(ERR_UNEXPECTED_ACTION);
    }

    /**
     * Exit application with error
     *
     * @param errorCode Error code
     */
    public static void exitWithError(int errorCode)
    {
        Logger.error(LOGGER_TAG, "Tools.exitWithError(int errorCode) has been called. Error code: " + errorCode);
        Logger.flush();
        System.exit(errorCode);
    }

    /**
     * Get application's cache directory path
     *
     * @return Application's cache directory path
     */
    public static String getCacheDirPath()
    {
        return mCacheDir;
    }

    /**
     * Get application's files directory path
     *
     * @return Application's files directory path
     */
    public static String getFilesDirPath()
    {
        return mFilesDir;
    }

    /**
     * Get application start time
     *
     * @return Application start time
     */
    public static long getStartTime()
    {
        return mStartTime;
    }

    /**
     * Initialize application
     *
     * @return Whether the initialization is succeed or not
     */
    public boolean init()
    {
        try
        {
            mCacheDir = getCacheDir().getAbsolutePath();
            mFilesDir = getFilesDir().getAbsolutePath();
            return true;
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            return false;
        }
    }

    //endregion

    //region Override methods

    @Override
    public void onCreate()
    {
        try
        {
            Logger.info(LOGGER_TAG, "Started app initialization.");

            // Initialize files dir, which is important for other classes.
            if (!init())
            {
                Logger.error(LOGGER_TAG, "FATAL ERROR: Can not init Navigator.");
                exitWithError(ERR_INIT);
            }
            if (!Logger.init())
            {
                Logger.error(LOGGER_TAG, "FATAL ERROR: Can not init Logger.");
                exitWithError(ERR_INIT);
            }
            if (!MapManager.init())
            {
                Logger.error(LOGGER_TAG, "FATAL ERROR: Can not init MapManager.");
                exitWithError(ERR_INIT);
            }
            if (!NavigateManager.init())
            {
                Logger.error(LOGGER_TAG, "FATAL ERROR: Can not init NavigateManager.");
                exitWithError(ERR_INIT);
            }
            if (!TypefaceManager.init(getAssets()))
            {
                Logger.error(LOGGER_TAG, "FATAL ERROR: Can not init TypefaceManager.");
                exitWithError(ERR_INIT);
            }
            if (!SettingsManager.init(this))
            {
                Logger.error(LOGGER_TAG, "FATAL ERROR: Can not init SettingsManager.");
                exitWithError(ERR_INIT);
            }
            if (!FakeLocateManager.init())
            {
                Logger.error(LOGGER_TAG, "FATAL ERROR: Can not init FakeLocateManager.");
                exitWithError(ERR_INIT);
            }

            Logger.info(LOGGER_TAG, "Finished app initialization. Application start.");
            super.onCreate();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init the app.", t);
            exitWithError();
        }
    }

    //endregion
}
