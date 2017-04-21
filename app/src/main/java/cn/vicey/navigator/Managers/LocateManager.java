package cn.vicey.navigator.Managers;

import android.graphics.Point;

/**
 * Locate manager, provides a set of methods to help locate
 */
public final class LocateManager
{
    //region Static methods

    /**
     * Gets current location provided by locate module
     *
     * @return Current location
     */
    public static Point getCurrentLocation()
    {
        return new Point();
    }

    //endregion

    //region Constructors

    /**
     * Hidden for static class design pattern
     */
    private LocateManager()
    {
        // no-op
    }

    //endregion
}
