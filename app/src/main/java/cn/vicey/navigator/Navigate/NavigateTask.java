package cn.vicey.navigator.Navigate;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Managers.NavigateManager;
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

    private NodeBase       mEnd;        // Task's end node
    private int            mErrorCount; // Task's error count
    private boolean        mFinished;   // Whether the task has been finished yet
    private int            mFloorIndex; // Task's related floor index
    private FloorNavigator mNavigator;  // Task's related navigator
    private Path           mPath;       // Task's guide path
    private NodeBase       mStart;      // Task's start node

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link NavigateTask}
     *
     * @param floorIndex Related floor index
     * @param start      Start node
     * @param end        End node
     */
    public NavigateTask(int floorIndex, final @NonNull NodeBase start, final @NonNull NodeBase end)
    {
        mFloorIndex = floorIndex;
        mNavigator = NavigateManager.getNavigator(mFloorIndex);
        mStart = start;
        mEnd = end;
    }

    //endregion

    //region Accessors

    /**
     * Gets task's related floor index
     *
     * @return Task's related floor index
     */
    public int getFloorIndex()
    {
        return mFloorIndex;
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
        if (mPath == null) return null;
        return mPath.fork();
    }

    //endregion

    //region Methods

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

            // No navigator, finished the task
            if (mNavigator == null) return true;

            // Not current floor, skip
            if (mFloorIndex != UserNode.getInstance().getCurrentFloorIndex()) return false;

            // If the path hasn't been built yet then built it
            if (mPath == null) mPath = mNavigator.navigate(mStart, mEnd);
            if (mPath == null) return false;

            NodeBase nearestNode = NavigateManager.getNearestNode();
            if (nearestNode == null) throw new IllegalStateException("Can't find nearest node.");
            mStart = nearestNode;

            // If new node is in path, check whether the task is finished
            // Otherwise rebuild path
            if (mPath.contains(nearestNode)) mFinished = mPath.isEnd(nearestNode);
            else mPath = mNavigator.navigate(mStart, mEnd);

            return mFinished;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to update task. Error count: " + mErrorCount, t);
            return ++mErrorCount >= MAX_ERROR_COUNT;
        }
    }

    //endregion
}
