package cn.vicey.navigator.Managers;

public final class DebugManager
{
    private static boolean mUseFakeLocationEnabled;

    public static boolean isUseFakeLocation()
    {
        return mUseFakeLocationEnabled;
    }

    public static void disableUseFakeLocation()
    {
        mUseFakeLocationEnabled = false;
    }

    public static void enableUseFakeLocation()
    {
        mUseFakeLocationEnabled = true;
    }

    private DebugManager()
    {
        // no-op
    }
}
