package cn.vicey.navigator.Debug;

import android.graphics.Point;
import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Map;
import cn.vicey.navigator.Models.Nodes.DebugPathNode;
import cn.vicey.navigator.Navigate.NavigateManager;

import java.util.Date;
import java.util.Random;

/**
 * Fake locate manager, provides a set of methods to help locate
 */
public final class FakeLocateManager
{
    //region Constants

    private static final int X_SPEED = 50; // X speed in pixel per sec
    private static final int Y_SPEED = 50; // Y speed in pixel per sec

    //endregion

    //region Static fields

    private static DebugPath mDebugPath; // Debug path

    private static Point mLastLocation   = new Point(500, 500); // Last location
    private static long  mLastUpdateTime = 0;                         // Last update time

    //endregion

    //region Static accessors

    /**
     * Gets current floor index provided by fake data
     *
     * @return Current floor index
     */
    public static int getCurrentFloorIndex()
    {
        Map map = NavigateManager.getCurrentMap();
        if (map == null) return NavigateManager.NO_SELECTED_FLOOR;

        if (DebugManager.isUseRandomLocationEnabled())
        {
            int range = map.getFloors().size();
            return new Random().nextInt(range);
        }
        else
        {
            if (mDebugPath == null) return NavigateManager.NO_SELECTED_FLOOR;
            DebugPathNode currentNode = mDebugPath.getCurrentNode();
            if (currentNode == null) return NavigateManager.NO_SELECTED_FLOOR;
            int floorIndex = currentNode.getFloorIndex();
            if (NavigateManager.getFloor(floorIndex) == null) return NavigateManager.NO_SELECTED_FLOOR;
            return floorIndex;
        }
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

        if (DebugManager.isUseRandomLocationEnabled())
        {
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
        else
        {
            if (mDebugPath == null) return new Point(0, 0);
            DebugPathNode currentNode = mDebugPath.getCurrentNode();
            if (currentNode == null) return new Point(0, 0);
            return new Point(currentNode.getX(), currentNode.getY());
        }
    }

    /**
     * Sets related debug path
     *
     * @param debugPath Debug path to set
     */
    public static void setDebugPath(final @NonNull DebugPath debugPath)
    {
        mDebugPath = debugPath;
    }

    //endregion

    //region Static methods

    /**
     * Initialize manager
     *
     * @return Whether the initialization is succeed or not
     */
    public static boolean init()
    {
        try
        {
            NavigateManager.addOnUpdateListener(NavigateManager.HIGHER_PRIORITY, new NavigateManager.OnUpdateListener()
            {
                @Override
                public void onUpdate()
                {
                    if (!DebugManager.isUseFakeLocationEnabled()) return;
                    if (DebugManager.isUseRandomLocationEnabled()) return;
                    if (mDebugPath == null) return;
                    mDebugPath.moveNext();
                }
            });
            return true;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize fake location manager.", t);
            return false;
        }
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
