package cn.vicey.navigator;

import android.app.Application;
import cn.vicey.navigator.Managers.MapManager;
import cn.vicey.navigator.Managers.SettingsManager;
import cn.vicey.navigator.Share.TypefaceManager;
import cn.vicey.navigator.Utils.Logger;

import java.util.Date;

public class Navigator
        extends Application
{
    private static final String LOGGER_TAG = "Navigator";

    public static final int ERR_INIT = -2;
    public static final int ERR_SUCCEED = 0;
    public static final int ERR_UNEXPECTED_ACTION = -1;

    private static String mCacheDir;
    private static String mFilesDir;
    private static long mStartTime = new Date().getTime();

    public static void exit()
    {
        Logger.info(LOGGER_TAG, "Exit application.");
        Logger.flush();
        System.exit(ERR_SUCCEED);
    }

    public static void exitWithError()
    {
        exitWithError(ERR_UNEXPECTED_ACTION);
    }

    public static void exitWithError(int errorCode)
    {
        Logger.error(LOGGER_TAG, "Tools.exitWithError(int errorCode) has been called. Error code: " + errorCode);
        Logger.flush();
        System.exit(errorCode);
    }

    public static String getCacheDirPath()
    {
        return mCacheDir;
    }

    public static String getFilesDirPath()
    {
        return mFilesDir;
    }

    public static long getStartTime()
    {
        return mStartTime;
    }

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

    @Override
    public void onCreate()
    {
        try
        {
            Logger.info(LOGGER_TAG, "Started app initialization.");

            // TODO: put global initialization code below.

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

            Logger.info(LOGGER_TAG, "Finished app initialization. Application start.");
            super.onCreate();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init the app.", t);
            exitWithError();
        }
    }
}
