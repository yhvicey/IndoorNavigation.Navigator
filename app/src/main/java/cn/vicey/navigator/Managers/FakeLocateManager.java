package cn.vicey.navigator.Managers;

import android.graphics.Point;

import java.util.Random;

/**
 * Fake locate manager, provides a set of methods to help locate
 */
public final class FakeLocateManager
{
    //region Static methods

    /**
     * Gets current location provided by fake date
     *
     * @return Current location
     */
    public static Point getCurrentLocation()
    {
        Random random = new Random();
        return new Point(random.nextInt(), random.nextInt());
    }

    //endregion

    //region Constructors

    /**
     * Hidden for static class design pattern
     */
    private FakeLocateManager()
    {
        // no-op
    }

    //endregion
}
