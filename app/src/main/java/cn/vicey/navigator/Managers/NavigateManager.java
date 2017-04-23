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

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Navigate manager, provides a set of methods to help navigate
 */
public final class NavigateManager
{
    //region Constants

    private static final String LOGGER_TAG = "NavigateManager";

    private static final long UPDATE_INTERNAL = 1000; // Update internal in milliseconds

    /**
     * Indicates no floor is selected
     */
    public static final int NO_SELECTED_FLOOR = -1;

    //endregion

    //region Static fields

    private static Path      mCurrentGuidePath; // Current guide path
    private static Map       mCurrentMap;       // Current map object
    private static boolean   mIsNavigating;     // Whether the manager is navigation
    private static GuideNode mNearestNode;      // Nearest node to user node

    private static SparseArray<FloorNavigator> mFloorNavigators    = new SparseArray<>();           // Floor navigators
    private static Runnable                    mNavigateHelperTask = new Runnable()                 // Navigate task
    {
        @Override
        public void run()
        {
            try
            {
                while (mIsNavigating)
                {
                    // Ensure internal between two updates to avoid high cpu usage
                    Thread.sleep(UPDATE_INTERNAL);

                    // Calculate nearest node
                    updateNearestNode();

                    if (mNavigateTaskQueue.isEmpty()) continue;

                    // Gets the first navigate task and check it
                    NavigateTask task = mNavigateTaskQueue.peek();
                    if (task.getFloorIndex() != UserNode.getInstance().getCurrentFloorIndex()) continue;

                    if (task.update())
                    {
                        // Current task finished
                        mNavigateTaskQueue.remove();
                        continue;
                    }

                    // Get guide path and append user node to it
                    mCurrentGuidePath = task.getPath().appendHead(UserNode.getInstance());
                }
            }
            catch (Throwable t)
            {
                Logger.error(LOGGER_TAG, "Failed to finish navigation.", t);
            }
            finally
            {
                mIsNavigating = false;
            }
        }
    };
    private static Queue<NavigateTask>         mNavigateTaskQueue  = new ConcurrentLinkedQueue<>(); // Navigation task queue

    //endregion

    //region Static methods

    /**
     * Find the nearest downstairs entry node to user node
     *
     * @return The nearest downstairs entry node to user node
     */
    private static GuideNode findNearestDownstairsEntryNode()
    {
        Floor floor = getCurrentFloor();
        if (floor == null) return null;
        return findNearestNode(UserNode.getInstance().getX(), UserNode.getInstance().getY(), floor.getPrevEntryNodes());
    }

    /**
     * Find the nearest node to specified location
     *
     * @param x     X axis
     * @param y     Y axis
     * @param nodes Nodes to find
     * @return The nearest node to specified location
     */
    private static GuideNode findNearestNode(int x, int y, List<GuideNode> nodes)
    {
        if (getCurrentFloor() == null) return null;
        double distance = Double.MAX_VALUE;
        GuideNode result = null;
        for (GuideNode node : nodes)
        {
            double newDistance = node.calcDistance(x, y);
            if (newDistance > distance) continue;
            distance = newDistance;
            result = node;
        }
        return result;
    }

    /**
     * Find the nearest upstairs entry node to user node
     *
     * @return The nearest upstairs entry node to user node
     */
    private static GuideNode findNearestUpstairsEntry()
    {
        Floor floor = getCurrentFloor();
        if (floor == null) return null;
        return findNearestNode(UserNode.getInstance().getX(), UserNode.getInstance().getY(), floor.getNextEntryNodes());
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
        mNearestNode = findNearestNode(UserNode.getInstance().getX(), UserNode.getInstance()
                                                                              .getY(), floor.getGuideNodes());
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
     * @param startFloor Start floor
     * @param startNode  Start node
     * @param endFloor   End floor
     * @param endNode    End node
     */
    public static void startNavigate(int startFloor, final @NonNull GuideNode startNode, int endFloor, final @NonNull GuideNode endNode)
    {
        if (startFloor == endFloor)
        {
            mNavigateTaskQueue.add(new NavigateTask(startFloor, startNode, endNode));
        }
        else
        {
            // TODO: Finish different-floor navigation logic here
        }
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
