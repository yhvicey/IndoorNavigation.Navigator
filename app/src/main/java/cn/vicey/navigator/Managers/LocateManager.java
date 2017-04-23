package cn.vicey.navigator.Managers;

import android.graphics.Point;

/**
 * Locate manager, provides a set of methods to help locate
 */
public final class LocateManager
{
    //region Static methods

    /**
     * Gets current floor index provided by fake data
     *
     * @return Current floor index
     */
    public static int getCurrentFloorIndex()
    {
        // TODO: Integrate locate module here
        return NavigateManager.NO_SELECTED_FLOOR;
    }

    /**
     * Gets current location provided by locate module
     *
     * @return Current location
     */
    public static Point getCurrentLocation()
    {
        // TODO: Integrate locate module here
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
