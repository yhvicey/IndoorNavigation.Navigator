package cn.vicey.navigator.Share;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import cn.vicey.navigator.Utils.Logger;

public class TypefaceManager
{
    private static final String LOGGER_TAG = "TypefaceManager";
    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf";

    private static Typeface mCanaroExtraBold;

    public static boolean init(AssetManager assetManager)
    {
        try
        {
            mCanaroExtraBold = Typeface.createFromAsset(assetManager, CANARO_EXTRA_BOLD_PATH);
            return true;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to load typeface.", t);
            return false;
        }
    }

    private TypefaceManager()
    {
        // no-op
    }

    public static final Typeface getCanaroExtraBold()
    {
        return mCanaroExtraBold;
    }
}
