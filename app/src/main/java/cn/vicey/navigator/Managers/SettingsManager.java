package cn.vicey.navigator.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cn.vicey.navigator.Utils.Logger;

/**
 * Settings manager, provides a set of methods to manage settings
 */
public final class SettingsManager
{
    //region Constants

    private static final String LOGGER_TAG = "SettingsManager";

    private static final String DEFAULT_INT     = "0";               // Default int settings item value
    private static final String DEFAULT_STRING  = "";                // Default string settings item value
    private static final String PREFERENCE_NAME = "SettingsManager"; // Preference name

    //endregion

    //region Static fields

    private static boolean           mDebugModeEnabled; // Whether the debug mode is enabled
    private static SharedPreferences mSharedPreference; // Shared preference object

    //endregion

    //region Static accessors

    public static boolean isDebugModeEnabled()
    {
        return mDebugModeEnabled;
    }

    /**
     * Sets whether the debug mode is enabled
     *
     * @param value Whether the debug mode is enabled
     */
    public static void setDebugModeEnabled(boolean value)
    {
        mDebugModeEnabled = value;
        if (value) Logger.debug(LOGGER_TAG, "Debug mode enabled.");
        else Logger.debug(LOGGER_TAG, "Debug mode disabled.");
    }

    //endregion

    //region Static methods

    /**
     * Gets settings item value from shared preference
     *
     * @param name         Item name
     * @param defaultValue Default item value
     * @return Item value, or default item value if the specified name didn't exist
     */
    private static String getSettingsItemValue(final @NonNull String name, final @NonNull String defaultValue)
    {
        try
        {
            String result = mSharedPreference.getString(name, null);
            return result == null ? defaultValue : result;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to get setting value.", t);
            return null;
        }
    }

    /**
     * Initialize manager
     *
     * @param context Related context
     * @return Whether the initialization is succeed or not
     */
    public static boolean init(Context context)
    {
        try
        {
            mSharedPreference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

            // TODO: Add settings item values initialization here

            return true;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to get shared preferences.", t);
            return false;
        }
    }

    /**
     * Sets settings item value to shared preference
     *
     * @param name  Item name
     * @param value Item value
     * @return Whether the saving is succeed or not
     */
    private static boolean setSettingValue(final @NonNull String name, final @NonNull String value)
    {
        try
        {
            if (mSharedPreference == null) return false;
            SharedPreferences.Editor editor = mSharedPreference.edit();
            editor.putString(name, value);
            editor.apply();
            return true;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to set setting value.", t);
            return false;
        }
    }

    //endregion

    //region Constructors

    /**
     * Hidden for static class design pattern
     */
    private SettingsManager()
    {
        // no-op
    }

    //endregion
}
