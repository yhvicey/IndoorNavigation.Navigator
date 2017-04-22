package cn.vicey.navigator.Managers;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import cn.vicey.navigator.Utils.Logger;

/**
 * Typeface manager, provides a set of methods to manage typefaces
 */
public final class TypefaceManager
{
    //region Constants

    private static final String LOGGER_TAG = "TypefaceManager";

    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf"; // Canaro extra bold typeface file path

    //endregion

    //region Static fields

    private static Typeface mCanaroExtraBold; // Canaro extra bold typeface

    //endregion

    //region Static accessors

    /**
     * Gets canaro extra bold typeface
     *
     * @return Canaro extra bold typeface
     */
    public static Typeface getCanaroExtraBold()
    {
        return mCanaroExtraBold;
    }

    //endregion

    //region Static methods

    /**
     * Initialize manager
     *
     * @param assetManager Related asset manager
     * @return Whether the initialization is succeed or not
     */
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

    //endregion

    //region Constructors

    /**
     * Hidden for static class design pattern
     */
    private TypefaceManager()
    {
        // no-op
    }

    //endregion
}
