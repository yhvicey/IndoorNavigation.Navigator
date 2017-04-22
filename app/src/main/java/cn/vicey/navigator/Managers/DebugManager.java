package cn.vicey.navigator.Managers;

/**
 * Debug manager, provides a set of methods to help debug
 */
public final class DebugManager
{
    //region Static fields

    private static boolean mUseFakeLocationEnabled; // Whether the user node should use fake location

    //endregion

    //region Static accessors

    /**
     * Gets whether the user node should use fake location
     *
     * @return Whether the user node should use fake location
     */
    public static boolean isUseFakeLocation()
    {
        return mUseFakeLocationEnabled;
    }

    /**
     * Sets whether the user node should use fake location
     *
     * @param value Whether the user node should use fake location
     */
    public static void setUseFakeLocation(boolean value)
    {
        mUseFakeLocationEnabled = value;
    }

    //endregion

    //region Constructors

    /**
     * Hidden for static class design pattern
     */
    private DebugManager()
    {
        // no-op
    }

    //endregion
}
