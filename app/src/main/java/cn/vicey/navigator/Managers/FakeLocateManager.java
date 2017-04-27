package cn.vicey.navigator.Managers;

import android.graphics.Point;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Map;

import java.util.Date;
import java.util.Random;

/**
 * Fake locate manager, provides a set of methods to help locate
 */
public final class FakeLocateManager
{
    //region Static fields

    private static Point mLastLocation   = new Point(500, 500);   // Last location
    private static long  mLastUpdateTime = 0;                           // Last update time
    //region Static methods

    /**
     * Gets current floor index provided by fake data
     *
     * @return Current floor index
     */
    public static int getCurrentFloorIndex()
    {
        Map map = NavigateManager.getCurrentMap();
        if (map == null) return NavigateManager.NO_SELECTED_FLOOR;
        int range = map.getFloors().size();
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

        final int X_SPEED = 50;
        final int Y_SPEED = 50;

        if (mLastUpdateTime == 0) mLastUpdateTime = new Date().getTime();
        float xSpeed = (new Random().nextFloat() * X_SPEED * 2 - X_SPEED) / 1000;
        float ySpeed = (new Random().nextFloat() * Y_SPEED * 2 - Y_SPEED) / 1000;
        long currentTime = new Date().getTime();
        int xOffset = (int) (xSpeed * (currentTime - mLastUpdateTime));
        int yOffset = (int) (ySpeed * (currentTime - mLastUpdateTime));
        mLastUpdateTime = currentTime;
        mLastLocation.x += xOffset;
        mLastLocation.y += yOffset;
        return mLastLocation;
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
