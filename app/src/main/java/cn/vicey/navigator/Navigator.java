package cn.vicey.navigator;

import android.app.Application;
import android.os.Environment;
import cn.vicey.navigator.Share.Logger;

import java.io.File;

public class Navigator
        extends Application
{
    private static final String LOGGER_TAG = "Navigator";
    private static final String DATA_DIR = "/Navigator/";

    private static String mDataDirFullPath = Environment.getExternalStorageDirectory() + DATA_DIR;

    private static boolean mInitialized = false;

    public boolean initialize()
    {
        synchronized (this)
        {
            if (mInitialized) return true;
            try
            {
                boolean initialized = true;

                // TODO: put global initialization code here.
                File appDataDir = new File(mDataDirFullPath);
                if (!(appDataDir.exists() || appDataDir.mkdir()))
                {
                    Logger.error(LOGGER_TAG, "Failed to create data folder.");
                    initialized &= false;
                }

                return initialized;
            }
            catch (Throwable t)
            {
                Logger.error(LOGGER_TAG, "Failed to initialize the app.");
                t.printStackTrace();
                Logger.saveToFile();
                System.exit(-1);
                return false;
            }
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInitialized = initialize();
    }

    public static String getDataDirFullPath()
    {
        return mDataDirFullPath;
    }

    public static boolean getInitialized()
    {
        return mInitialized;
    }
}
