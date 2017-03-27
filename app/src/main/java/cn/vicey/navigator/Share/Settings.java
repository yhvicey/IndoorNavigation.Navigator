package cn.vicey.navigator.Share;

import java.util.Locale;

public class Settings
{
    private static final String LOGGER_TAG = "Settings";

    public static final Locale[] SUPPORTED_LOCALES = {
            Locale.SIMPLIFIED_CHINESE,
            Locale.ENGLISH
    };

    private static Locale mCurrentLocale = SUPPORTED_LOCALES[0];

    private Settings()
    {
        // no-op
    }

    public static Locale getCurrentLocale()
    {
        return mCurrentLocale;
    }

    public static void setCurrentLocale(Locale locale)
    {
        for (Locale supportedLocale : SUPPORTED_LOCALES)
        {
            if (!supportedLocale.equals(locale))
            {
                continue;
            }
            Logger.info(LOGGER_TAG, "Locale changed. Previous locale: " + mCurrentLocale.getDisplayName() + ", current local: " + locale.getDisplayName());
            mCurrentLocale = locale;
            return;
        }
    }
}
