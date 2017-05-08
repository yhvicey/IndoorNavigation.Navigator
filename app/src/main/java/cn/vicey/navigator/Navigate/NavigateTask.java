package cn.vicey.navigator.Navigate;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Nodes.GuideNode;
import cn.vicey.navigator.Models.Nodes.NodeBase;
import cn.vicey.navigator.Models.Nodes.UserNode;
import cn.vicey.navigator.Utils.Logger;

/**
 * Navigate task class, provides navigate path to {@link NavigateManager} by using {@link FloorNavigator}
 */
public class NavigateTask
{
    //region Constants

    private static final String LOGGER_TAG = "NavigateTask";

    private static final int MAX_ERROR_COUNT = 3;

    //endregion

    //region Fields

    private int                        mErrorCount;       // Task's error count
    private boolean                    mFinished;         // Whether the task has been finished yet
    private FloorNavigator.PathBuilder mPathBuilder;      // Task's guide path
    private GuideNode                  mTarget;           // Task's target node
    private int                        mTargetFloorIndex; // Task's target floor index

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link NavigateTask}
     *
     * @param endFloorIndex End floor index
     * @param end           End node
     */
    public NavigateTask(int endFloorIndex, final @NonNull GuideNode end)
    {
        mTargetFloorIndex = endFloorIndex;
        mTarget = end;
    }

    //endregion

    //region Accessors

    /**
     * Gets latest path from start node to end node
     *
     * @return Latest path's fork, or null if navigator isn't ready yet
     */
    public Path getPath()
    {
        if (mPathBuilder == null) return null;
        return mPathBuilder.build();
    }

    /**
     * Gets task's target floor index
     *
     * @return Task's target floor index
     */
    public int getTargetFloorIndex()
    {
        return mTargetFloorIndex;
    }

    //endregion

    //region Methods

    public void onFloorChanged(int newFloorIndex)
    {
        Floor floor = NavigateManager.getFloor(newFloorIndex);
        FloorNavigator navigator = NavigateManager.getNavigator(newFloorIndex);
        if (floor == null || navigator == null)
        {
            // Index out of range, finished task
            Logger.error(LOGGER_TAG, "Floor index out of range.");
            mFinished = true;
            return;
        }
        GuideNode nearestNode = NavigateManager.getNearestNode();
        if (nearestNode == null)
        {
            Logger.error(LOGGER_TAG, "No nearest node.");
            mFinished = true;
            return;
        }
        int x = NavigateManager.getCurrentLocation().x;
        int y = NavigateManager.getCurrentLocation().y;
        int diff = newFloorIndex - mTargetFloorIndex;
        if (diff < 0)
        {
            // Target is higher
            GuideNode nearestEntryNode = floor.findNearestNextEntryNode(x, y);
            if (nearestEntryNode == null)
            {
                // Target is unreachable
                Logger.error(LOGGER_TAG, "Target is unreachable.");
                mFinished = true;
                return;
            }
            mPathBuilder = navigator.navigate(nearestNode, nearestEntryNode);
        }
        else if (diff > 0)
        {
            // Target is lower
            GuideNode nearestEntryNode = floor.findNearestPrevEntryNode(x, y);
            if (nearestEntryNode == null)
            {
                // Target is unreachable
                Logger.error(LOGGER_TAG, "Target is unreachable.");
                mFinished = true;
                return;
            }
            mPathBuilder = navigator.navigate(nearestNode, nearestEntryNode);
        }
        else
        {
            // Same floor
            mPathBuilder = navigator.navigate(nearestNode, mTarget);
        }
    }

    /**
     * Update the task
     *
     * @return True if the task finished, otherwise false
     */
    public boolean update()
    {
        try
        {
            if (mFinished) return true;

            int floorIndex = UserNode.getInstance().getCurrentFloorIndex();
            FloorNavigator navigator = NavigateManager.getNavigator(floorIndex);
            if (navigator == null)
            {
                // Floor index out of range, finished task
                Logger.error(LOGGER_TAG, "Floor index out of range.");
                mFinished = true;
                return true;
            }
            if (mPathBuilder == null)
            {
                // No related path builder, finished task
                Logger.error(LOGGER_TAG, "No related path builder. Call onFloorChanged(int) first.");
                mFinished = true;
                return true;
            }

            if (floorIndex != mTargetFloorIndex) return false;
            // Already target floor, check if the nearest node is the target node
            NodeBase nearestNode = NavigateManager.getNearestNode();
            if (nearestNode == null)
            {
                Logger.error(LOGGER_TAG, "No nearest node.");
                mFinished = true;
                return true;
            }
            Path path = getPath();
            mFinished = path.contains(nearestNode) && path.isEnd(nearestNode);
            return mFinished;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to update task. Error count: " + mErrorCount + ".", t);
            return ++mErrorCount >= MAX_ERROR_COUNT;
        }
    }

    //endregion
}
