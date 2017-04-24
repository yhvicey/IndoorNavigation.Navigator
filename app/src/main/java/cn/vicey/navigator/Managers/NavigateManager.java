package cn.vicey.navigator.Managers;

import android.support.annotation.NonNull;
import android.util.SparseArray;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Map;
import cn.vicey.navigator.Models.Nodes.GuideNode;
import cn.vicey.navigator.Models.Nodes.UserNode;
import cn.vicey.navigator.Navigate.FloorNavigator;
import cn.vicey.navigator.Navigate.NavigateTask;
import cn.vicey.navigator.Navigate.Path;
import cn.vicey.navigator.Utils.Logger;

/**
 * Navigate manager, provides a set of methods to help navigate
 */
public final class NavigateManager
{
    //region Inner classes

    /**
     * Navigation indicator class, provides the ability to cancel navigation task in other thread
     */
    private static class NavigationIndicator
    {
        //region Field

        private boolean mIsRunning = true; // Whether the navigation is running

        //endregion

        //region Methods

        /**
         * Cancel the navigation
         */
        private void cancel()
        {
            mIsRunning = false;
        }

        //endregion
    }

    //endregion

    //region Constants

    private static final String LOGGER_TAG = "NavigateManager";

    private static final Object SYNC_LOCK       = new Object(); // Sync lock
    private static final long   UPDATE_INTERNAL = 1000;         // Update internal in milliseconds

    /**
     * Indicates no floor is selected
     */
    public static final int NO_SELECTED_FLOOR = -1;

    //endregion

    //region Static fields

    private static Path                mCurrentGuidePath; // Current guide path
    private static Map                 mCurrentMap;       // Current map object
    private static NavigateTask        mCurrentTask;      // Current navigate task
    private static NavigationIndicator mIndicator;        // Indicates whether the manager is navigation
    private static int                 mLastFloorIndex;   // Last floor index used for notifying floor changed event
    private static GuideNode           mNearestNode;      // Nearest node to user node

    private static SparseArray<FloorNavigator> mFloorNavigators    = new SparseArray<>();           // Floor navigators
    private static Runnable                    mNavigateHelperTask = new Runnable()                 // Navigate task
    {
        @Override
        public void run()
        {
            try
            {
                NavigationIndicator indicator = mIndicator;
                if (indicator == null) return;
                while (indicator.mIsRunning)
                {
                    // Ensure internal between two updates to avoid high cpu usage
                    Thread.sleep(UPDATE_INTERNAL);

                    // Check current task
                    NavigateTask task;
                    synchronized (SYNC_LOCK)
                    {
                        if (mCurrentTask == null) break;
                        task = mCurrentTask;
                    }

                    // Update the nearest node
                    updateNearestNode();

                    // Check floor index
                    int floorIndex = UserNode.getInstance().getCurrentFloorIndex();
                    if (mLastFloorIndex != floorIndex)
                    {
                        task.onFloorChanged(floorIndex);
                        mLastFloorIndex = floorIndex;
                    }

                    if (task.update())
                    {
                        // Current task finished
                        synchronized (SYNC_LOCK)
                        {
                            mCurrentTask = null;
                        }
                        break;
                    }

                    // Get guide path and append user node to it
                    mCurrentGuidePath = mCurrentTask.getPath().appendHead(UserNode.getInstance());
                }
                cancelNavigate();
            }
            catch (Throwable t)
            {
                Logger.error(LOGGER_TAG, "Failed to finish navigation.", t);
            }
        }
    };

    //endregion

    //region Static methods

    /**
     * Update nearest node
     */
    private static void updateNearestNode()
    {
        Floor floor = getCurrentFloor();
        if (floor == null)
        {
            mNearestNode = null;
            return;
        }
        int x = UserNode.getInstance().getX();
        int y = UserNode.getInstance().getY();
        mNearestNode = floor.findNearestGuideNode(x, y);
    }

    /**
     * Cancel all navigate task
     */
    public static void cancelNavigate()
    {
        if (!isNavigating()) return;
        mIndicator.cancel();
        synchronized (SYNC_LOCK)
        {
            mCurrentTask = null;
        }
    }

    /**
     * Gets current floor
     *
     * @return Current floor object, or null if no floor is selected
     */
    public static Floor getCurrentFloor()
    {
        return getFloor(UserNode.getInstance().getCurrentFloorIndex());
    }

    /**
     * Gets current map object
     *
     * @return Current map object, or null if no current map is set
     */
    public static Map getCurrentMap()
    {
        return mCurrentMap;
    }

    /**
     * Gets current floor's navigator
     *
     * @return Current floor's navigator, or null if no floor is selected
     */
    public static FloorNavigator getCurrentNavigator()
    {
        return getNavigator(UserNode.getInstance().getCurrentFloorIndex());
    }

    /**
     * Gets specified floor
     *
     * @param floorIndex Specified floor index
     * @return Specified floor object, or null if index is out of range
     */
    public static Floor getFloor(int floorIndex)
    {
        if (mCurrentMap == null) return null;
        if (floorIndex < 0) return null;
        if (floorIndex > mCurrentMap.getFloors().size() - 1) return null;
        return mCurrentMap.getFloors().get(floorIndex);
    }

    /**
     * Gets specified floor's navigator
     *
     * @param floorIndex Specified floor index
     * @return Specified floor's navigator object, or null if index is out of range
     */
    public static FloorNavigator getNavigator(int floorIndex)
    {
        Floor floor = getFloor(floorIndex);
        if (floor == null) return null;
        // If target floor's navigator didn't exist then create it
        if (mFloorNavigators.get(floorIndex) == null) mFloorNavigators.put(floorIndex, new FloorNavigator(floor));
        return mFloorNavigators.get(floorIndex);
    }

    /**
     * Gets the nearest node to user, or null if no floor is selected or error occurred
     *
     * @return The nearest node to user
     */
    public static GuideNode getNearestNode()
    {
        if (getCurrentFloor() == null) return null;
        return mNearestNode;
    }

    /**
     * Gets whether the NavigateManager is navigating
     *
     * @return Whether the NavigateManager is navigating
     */
    public static boolean isNavigating()
    {
        return mIndicator != null && mIndicator.mIsRunning;
    }

    /**
     * Set current map
     *
     * @param map Map to set
     */
    public static void setCurrentMap(final @NonNull Map map)
    {
        mCurrentMap = map;
    }

    /**
     * Create navigate task for specified start node and end node
     *
     * @param endFloor End floor
     * @param endNode  End node
     */
    public static void startNavigate(int endFloor, final @NonNull GuideNode endNode)
    {
        if (mIndicator != null) mIndicator.cancel();
        mIndicator = new NavigationIndicator();
        synchronized (SYNC_LOCK)
        {
            mCurrentTask = new NavigateTask(endFloor, endNode);
        }
        new Thread(mNavigateHelperTask).start();
    }

    //endregion

    //region Constructors

    /**
     * Hidden for static class design pattern
     */
    private NavigateManager()
    {
        // no-op
    }

    //endregion
}
