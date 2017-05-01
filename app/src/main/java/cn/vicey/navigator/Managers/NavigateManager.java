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
import cn.vicey.navigator.R;
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

    private static final Object SYNC_LOCK       = new Object(); // Sync lock
    private static final int    MAX_ERROR_COUNT = 3;            // Max error count
    private static final long   UPDATE_INTERNAL = 1000;         // Update internal in milliseconds

    /**
     * Indicates no floor is selected
     */
    public static final int NO_SELECTED_FLOOR = -1;

    //endregion

    //region Static fields

    private static Path         mCurrentGuidePath; // Current guide path
    private static Map          mCurrentMap;       // Current map object
    private static NavigateTask mCurrentTask;      // Current navigate task
    private static Path         mCurrentUserPath;  // Current user path
    private static int          mErrorCount;       // Error count
    private static boolean      mIsNavigating;     // Indicates whether the manager is navigating
    private static int          mLastFloorIndex;   // Last floor index used for notifying floor changed event
    private static GuideNode    mNearestNode;      // Nearest node to user node

    private static SparseArray<FloorNavigator> mFloorNavigators   = new SparseArray<>(); // Floor navigators
    private static List<OnUpdateListener>      mOnUpdateListeners = new ArrayList<>();   // Listeners for update event
    private static Runnable                    mUpdateTask        = new Runnable()       // Update task
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

                    int floorIndex = UserNode.getInstance().getCurrentFloorIndex();

                    // Check floor index
                    if (mLastFloorIndex != floorIndex) onFloorChanged(floorIndex);

                    // If is navigating then update the task
                    if (mIsNavigating)
                    {
                        // Check current task
                        NavigateTask task;
                        synchronized (SYNC_LOCK)
                        {
                            if (mCurrentTask == null) continue;
                            task = mCurrentTask;
                        }

                        // Update the nearest node
                        updateNearestNode();

                        if (task.update())
                        {
                            // Current task finished
                            cancelNavigate();
                            AlertManager.alert(R.string.navigation_finished);
                            continue;
                        }

                        // Get guide path and append user node to it
                        mCurrentGuidePath = task.getPath();
                        if (mCurrentGuidePath != null) mCurrentGuidePath.appendHead(UserNode.getInstance());
                    }

                    // Track user path if it's enabled
                    if (DebugManager.isTrackPathEnabled())
                    {
                        if (mCurrentUserPath == null)
                        {
                            mCurrentUserPath = new Path(UserNode.getInstance());
                            List<Path> paths = getUserPaths(floorIndex);
                            if (paths != null) paths.add(mCurrentUserPath);
                        }
                        else mCurrentUserPath.appendTail(UserNode.getInstance());
                    }
                    else if (mCurrentUserPath != null) mCurrentUserPath = null;

                    // Notified all listeners
                    for (OnUpdateListener listener : mOnUpdateListeners) listener.onUpdate();
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
    private static SparseArray<List<Path>>     mUserPaths         = new SparseArray<>(); // User paths

    //endregion

    //region Static accessors

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
     * Gets current guide path's fork
     *
     * @return Current guide path's fork, or null if no navigation now
     */
    public static Path getCurrentGuidePath()
    {
        return mCurrentGuidePath;
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
     * @return Specified floor's navigator, or null if index is out of range
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
     * Gets specified floor's user paths
     *
     * @param floorIndex Floor index
     * @return Specified floor's user paths, or null if index is out of range
     */
    public static List<Path> getUserPaths(int floorIndex)
    {
        if (mCurrentMap == null) return null;
        if (floorIndex < 0) return null;
        if (floorIndex > mCurrentMap.getFloors().size() - 1) return null;
        if (mUserPaths.get(floorIndex) == null) mUserPaths.put(floorIndex, new ArrayList<Path>());
        return mUserPaths.get(floorIndex);
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

    private static void onFloorChanged(int newFloorIndex)
    {
        if (mCurrentUserPath != null)
        {
            List<Path> paths = getUserPaths(mLastFloorIndex);
            if (paths != null) paths.add(mCurrentUserPath);
            mCurrentUserPath = null;
        }

        NavigateTask task;
        synchronized (SYNC_LOCK)
        {
            task = mCurrentTask;
        }
        if (task != null) task.onFloorChanged(newFloorIndex);

        mLastFloorIndex = newFloorIndex;
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
     * Add {@link OnUpdateListener} to navigate manager
     *
     * @param listener Listener to add
     */
    public static void addOnUpdateListener(final @NonNull OnUpdateListener listener)
    {
        mOnUpdateListeners.add(listener);
    }

    /**
     * Cancel all navigate task
     */
    public static void cancelNavigate()
    {
        if (!mIsNavigating) return;
        mIsNavigating = false;
        synchronized (SYNC_LOCK)
        {
            mCurrentTask = null;
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
            return true;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init navigate manager.", t);
            return false;
        }
    }

    /**
     * Remove {@link OnUpdateListener} from navigate manager
     *
     * @param listener Listener to remove
     */
    public static void removeOnUpdateListener(final @NonNull OnUpdateListener listener)
    {
        mOnUpdateListeners.remove(listener);
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
        synchronized (SYNC_LOCK)
        {
            mCurrentTask = new NavigateTask(endFloor, endNode);
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
