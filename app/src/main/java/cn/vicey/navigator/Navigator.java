package cn.vicey.navigator;

import android.app.Application;
import cn.vicey.navigator.Map.MapManager;
import cn.vicey.navigator.Share.Logger;
import cn.vicey.navigator.Share.TypefaceManager;
import cn.vicey.navigator.Share.Utils;

public class Navigator
        extends Application
{
    private static final String LOGGER_TAG = "Navigator";

    public static final int ERR_SUCCEED = 0;
    public static final int ERR_UNEXPECTED_ACTION = -1;
    public static final int ERR_INIT = -2;

    private static String mCacheDir;
    private static String mFilesDir;

    public boolean initialize()
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

    public static void exit()
    {
        Logger.flush();
        System.exit(ERR_SUCCEED);
    }

    public static void exitWithError()
    {
        exitWithError(ERR_UNEXPECTED_ACTION);
    }

    public static void exitWithError(int errorCode)
    {
        Logger.error(LOGGER_TAG, "Utils.exitWithError(int errorCode) has been called. Error code: " + errorCode);
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

    @Override
    public void onCreate()
    {
        try
        {
            // TODO: put global initialization code below.

            // Initialize files dir, which is important for other classes.
            if (!initialize())
            {
                Logger.error(LOGGER_TAG, "FATAL ERROR: Cannot initialize Navigator.");
                exitWithError(ERR_INIT);
            }
            // Initialize Utils to get start time.
            if (!Utils.initialize())
            {
                Logger.error(LOGGER_TAG, "FATAL ERROR: Cannot initialize Utils.");
                exitWithError(ERR_INIT);
            }
            if (!Logger.initialize())
            {
                Logger.error(LOGGER_TAG, "FATAL ERROR: Cannot initialize Logger.");
                exitWithError(ERR_INIT);
            }
            if (!MapManager.initialize())
            {
                Logger.error(LOGGER_TAG, "FATAL ERROR: Cannot initialize MapManager.");
                exitWithError(ERR_INIT);
            }
            if (!TypefaceManager.initialize(getAssets()))
            {
                Logger.error(LOGGER_TAG, "FATAL ERROR: Cannot initialize TypefaceManager.");
                exitWithError(ERR_INIT);
            }

            Logger.info(LOGGER_TAG, "Finished initialization. Application start.");
            super.onCreate();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize the app.", t);
            exitWithError();
        }
    }
}
