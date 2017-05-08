package cn.vicey.navigator.Navigate;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import cn.vicey.navigator.Debug.DebugManager;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Map;
import cn.vicey.navigator.Models.Nodes.GuideNode;
import cn.vicey.navigator.Models.Nodes.UserNode;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.AlertManager;
import cn.vicey.navigator.Utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Navigate manager, provides a set of methods to help navigate
 */
public final class NavigateManager
{
    //region Inner classes

    public interface OnUpdateListener
    {
        void onUpdate();
    }

    //endregion

    //region Constants

    private static final String LOGGER_TAG = "NavigateManager";

    private static final Object SYNC_LOCK_TASK = new Object(); // Sync lock for task
    private static final Object SYNC_LOCK_PATH = new Object(); // Sync lock for user path

    private static final int  MAX_ERROR_COUNT = 3;            // Max error count
    private static final long UPDATE_INTERNAL = 1000;         // Update internal in milliseconds

    /**
     * Higher priority
     */
    public static final int HIGHER_PRIORITY = 1;

    /**
     * Highest priority
     */
    public static final int HIGHEST_PRIORITY = 2;

    /**
     * Lower priority
     */
    public static final int LOWER_PRIORITY = -1;

    /**
     * Lowest priority
     */
    public static final int LOWEST_PRIORITY = -2;

    /**
     * Normal priority
     */
    public static final int NORMAL_PRIORITY = 0;

    /**
     * Indicates no floor is selected
     */
    public static final int NO_SELECTED_FLOOR = -1;

    //endregion

    //region Static fields

    private static int          mCurrentFloorIndex; // Current floor index
    private static Path         mCurrentGuidePath;  // Current guide path
    private static Point        mCurrentLocation;   // Current location
    private static Map          mCurrentMap;        // Current map object
    private static NavigateTask mCurrentTask;       // Current navigate task
    private static Path         mCurrentUserPath;   // Current user path
    private static int          mErrorCount;        // Error count
    private static boolean      mIsNavigating;      // Indicates whether the manager is navigating
    private static int          mLastFloorIndex;    // Last floor index used for notifying floor changed event
    private static GuideNode    mNearestNode;       // Nearest node to user node

    private static SparseArray<FloorNavigator>            mFloorNavigators         = new SparseArray<>();                         // Floor navigators
    private static FloorNavigator.OnBuildFinishedListener mOnBuildFinishedListener = new FloorNavigator.OnBuildFinishedListener() // Listener for build finished event
    {
        @Override
        public void onFailed()
        {
            AlertManager.alert(R.string.failed_to_build_path);
            cancelNavigate();
        }

        @Override
        public void onSucceed()
        {

        }
    };
    private static SparseArray<List<OnUpdateListener>>    mOnUpdateListeners       = new SparseArray<>();                         // Listeners for update event
    private static Runnable                               mUpdateTask              = new Runnable()                               // Update task
    {
        @Override
        public void run()
        {
            try
            {
                while (true)
                {
                    // Ensure internal between two updates to avoid high cpu usage
                    Thread.sleep(UPDATE_INTERNAL);

                    // Check whether floor changed
                    if (mLastFloorIndex != mCurrentFloorIndex) onFloorChanged(mCurrentFloorIndex);

                    // Notify all listeners
                    for (OnUpdateListener listener : mOnUpdateListeners.get(HIGHEST_PRIORITY)) listener.onUpdate();
                    for (OnUpdateListener listener : mOnUpdateListeners.get(HIGHER_PRIORITY)) listener.onUpdate();
                    for (OnUpdateListener listener : mOnUpdateListeners.get(NORMAL_PRIORITY)) listener.onUpdate();
                    for (OnUpdateListener listener : mOnUpdateListeners.get(LOWER_PRIORITY)) listener.onUpdate();
                    for (OnUpdateListener listener : mOnUpdateListeners.get(LOWEST_PRIORITY)) listener.onUpdate();
                }
            }
            catch (Throwable t)
            {
                Logger.error(LOGGER_TAG, "Error occurred when updating navigate manager. Error count: " + mErrorCount++ + ".", t);
                if (mErrorCount < MAX_ERROR_COUNT)
                {
                    Logger.info(LOGGER_TAG, "Trying to restart update task.");
                    new Thread(this).start();
                }
                else
                    Logger.error(LOGGER_TAG, "Navigate manager update task's crash count reaches its limit. Update function will be disabled.");
            }
        }
    };

    //endregion

    //region Static accessors

    /**
     * Gets current floor
     *
     * @return Current floor object, or null if no floor is selected
     */
    public static Floor getCurrentFloor()
    {
        return getFloor(getCurrentFloorIndex());
    }

    /**
     * Gets current floor index
     *
     * @return Current floor index
     */
    public static int getCurrentFloorIndex()
    {
        return mCurrentFloorIndex;
    }

    /**
     * Gets current guide path's fork
     *
     * @return Current guide path's fork, or null if no navigation now
     */
    public static Path getCurrentGuidePath()
    {
        return mCurrentGuidePath;
    }

    /**
     * Gets current location
     *
     * @return Current location
     */
    public static Point getCurrentLocation()
    {
        return mCurrentLocation;
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
        return getNavigator(NavigateManager.getCurrentFloorIndex());
    }

    /**
     * Gets current user path's fork
     *
     * @return Current user path's fork, or null if there is no user path
     */
    public static Path getCurrentUserPath()
    {
        Path path = null;
        synchronized (SYNC_LOCK_PATH)
        {
            if (mCurrentUserPath != null) path = mCurrentUserPath.fork();
        }
        return path;
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
     * @return Specified floor's navigator, or null if index is out of range
     */
    public static FloorNavigator getNavigator(int floorIndex)
    {
        Floor floor = getFloor(floorIndex);
        if (floor == null) return null;
        // If target floor's navigator didn't exist then create it
        if (mFloorNavigators.get(floorIndex) == null)
        {
            FloorNavigator navigator = new FloorNavigator(floor);
            navigator.setOnBuildFinishedListener(mOnBuildFinishedListener);
            mFloorNavigators.put(floorIndex, navigator);
        }
        return mFloorNavigators.get(floorIndex);
    }

    /**
     * Gets the nearest node to user, or null if no floor is selected or error occurred
     *
     * @return The nearest node to user
     */
    public static GuideNode getNearestNode()
    {
        return mNearestNode;
    }

    /**
     * Gets whether the NavigateManager is navigating
     *
     * @return Whether the NavigateManager is navigating
     */
    public static boolean isNavigating()
    {
        return mIsNavigating;
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

    //endregion

    //region Static methods

    /**
     * Floor changed event handler
     *
     * @param newFloorIndex New floor index
     */
    private static void onFloorChanged(int newFloorIndex)
    {
        clearUserPath();

        NavigateTask task;
        synchronized (SYNC_LOCK_TASK)
        {
            task = mCurrentTask;
        }
        if (task != null) task.onFloorChanged(newFloorIndex);

        mLastFloorIndex = newFloorIndex;
    }

    private static void updateLocation()
    {
        mCurrentFloorIndex = UserNode.getInstance().getCurrentFloorIndex();
        mCurrentLocation = new Point(UserNode.getInstance().getX(), UserNode.getInstance().getY());
    }

    /**
     * Update navigation
     */
    private static void updateNavigation()
    {
        // If is navigating then update the task
        if (mIsNavigating)
        {
            // Check current task
            NavigateTask task;
            synchronized (SYNC_LOCK_TASK)
            {
                if (mCurrentTask == null) return;
                task = mCurrentTask;
            }

            // Update the nearest node
            updateNearestNode();

            if (task.update())
            {
                // Current task finished
                cancelNavigate();
                AlertManager.alert(R.string.navigation_finished);
                return;
            }

            // Get guide path and append user node to it
            mCurrentGuidePath = task.getPath();
            if (mCurrentGuidePath != null) mCurrentGuidePath.appendHead(UserNode.getInstance());
        }
    }

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
     * Update user path
     */
    private static void updateUserPath()
    {
        // Track user path if it's enabled
        if (DebugManager.isTrackPathEnabled())
        {
            synchronized (SYNC_LOCK_PATH)
            {
                // If no current user path then create one
                if (mCurrentUserPath == null) mCurrentUserPath = new Path(null);
                mCurrentUserPath.appendTail(UserNode.getInstance());
            }
        }
    }

    /**
     * Add {@link OnUpdateListener} to navigate manager
     *
     * @param priority Task priority
     * @param listener Listener to add
     */
    public static void addOnUpdateListener(int priority, final @NonNull OnUpdateListener listener)
    {
        if (priority > 0) mOnUpdateListeners.get(HIGHER_PRIORITY).add(listener);
        else if (priority < 0) mOnUpdateListeners.get(LOWER_PRIORITY).add(listener);
        else mOnUpdateListeners.get(NORMAL_PRIORITY).add(listener);
    }

    /**
     * Cancel all navigate task
     */
    public static void cancelNavigate()
    {
        if (!mIsNavigating) return;
        mIsNavigating = false;
        synchronized (SYNC_LOCK_TASK)
        {
            mCurrentTask = null;
        }
    }

    /**
     * Clear user paths
     */
    public static void clearUserPath()
    {
        synchronized (SYNC_LOCK_PATH)
        {
            mCurrentUserPath = null;
        }
    }

    /**
     * Initialize manager
     *
     * @return Whether the initialization is succeed or not
     */
    public static boolean init()
    {
        try
        {
            new Thread(mUpdateTask).start();

            mOnUpdateListeners.put(HIGHEST_PRIORITY, new ArrayList<OnUpdateListener>());
            mOnUpdateListeners.put(HIGHER_PRIORITY, new ArrayList<OnUpdateListener>());
            mOnUpdateListeners.put(NORMAL_PRIORITY, new ArrayList<OnUpdateListener>());
            mOnUpdateListeners.put(LOWER_PRIORITY, new ArrayList<OnUpdateListener>());
            mOnUpdateListeners.put(LOWEST_PRIORITY, new ArrayList<OnUpdateListener>());

            addOnUpdateListener(HIGHER_PRIORITY, new OnUpdateListener()
            {
                @Override
                public void onUpdate()
                {
                    updateLocation();
                }
            });

            addOnUpdateListener(NORMAL_PRIORITY, new OnUpdateListener()
            {
                @Override
                public void onUpdate()
                {
                    updateUserPath();
                }
            });

            addOnUpdateListener(NORMAL_PRIORITY, new OnUpdateListener()
            {
                @Override
                public void onUpdate()
                {
                    updateNavigation();
                }
            });

            return true;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init navigate manager.", t);
            return false;
        }
    }

    /**
     * Create navigate task for specified start node and end node
     *
     * @param endFloor End floor
     * @param endNode  End node
     */
    public static void startNavigate(int endFloor, final @NonNull GuideNode endNode)
    {
        if (mIsNavigating) cancelNavigate();
        mIsNavigating = true;
        synchronized (SYNC_LOCK_TASK)
        {
            mCurrentTask = new NavigateTask(endFloor, endNode);
            onFloorChanged(NavigateManager.getCurrentFloorIndex());
        }
        AlertManager.alert(R.string.starting_navigation);
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
