package cn.vicey.navigator.Managers;

import android.graphics.Point;
import cn.vicey.navigator.Models.Floor;

import java.util.Random;

/**
 * Fake locate manager, provides a set of methods to help locate
 */
public final class FakeLocateManager
{
    //region Static methods

    /**
     * Gets current floor index provided by fake data
     *
     * @return Current floor index
     */
    public static int getCurrentFloorIndex()
    {
        int range = NavigateManager.getCurrentMap().getFloors().size();
        return new Random().nextInt(range);
    }

    /**
     * Gets current location provided by fake date
     *
     * @return Current location
     */
    public static Point getCurrentLocation()
    {
        Floor floor = NavigateManager.getCurrentFloor();
        if (floor == null) return new Point(-1, -1);
        Random random = new Random();
        return new Point(random.nextInt(floor.getWidth()), random.nextInt(floor.getHeight()));
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
