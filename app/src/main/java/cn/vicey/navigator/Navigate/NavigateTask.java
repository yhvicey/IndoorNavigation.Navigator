package cn.vicey.navigator.Navigate;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Nodes.GuideNode;
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

    private int            mErrorCount;       // Task's error count
    private boolean        mIsFinished;       // Whether the task has been finished yet
    private FloorNavigator mNavigator;
    private GuideNode      mNearestEntryNode;
    private Path           mPath;
    private GuideNode      mTarget;           // Task's target node
    private int            mTargetFloorIndex; // Task's target floor index

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link NavigateTask}
     *
     * @param targetFloorIndex Target floor index
     * @param target           Target node
     */
    public NavigateTask(int targetFloorIndex, final @NonNull GuideNode target)
    {
        mTargetFloorIndex = targetFloorIndex;
        mTarget = target;
        mNavigator = NavigateManager.getNavigator(mTargetFloorIndex);
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

    /**
     * Gets task's target floor index
     *
     * @return Task's target floor index
     */
    public int getTargetFloorIndex()
    {
        return mTargetFloorIndex;
    }

    /**
     * Gets whether the task is finished
     *
     * @return Whether the task is finished
     */
    public boolean isFinished()
    {
        return mIsFinished;
    }

    //endregion

    //region Methods

    public void onFloorChanged()
    {
        mNavigator = null;
        mNearestEntryNode = null;

        Floor floor = NavigateManager.getCurrentFloor();
        if (floor == null)
        {
            mIsFinished = true;
            return;
        }

        mNavigator = NavigateManager.getCurrentNavigator();
        if (mNavigator == null)
        {
            // No navigator, finished task
            Logger.error(LOGGER_TAG, "No navigator.");
            mIsFinished = true;
            return;
        }
    }

    public void onNearestNodeChanged()
    {
        if (NavigateManager.getNearestNode() == mTarget)
        {
            mIsFinished = true;
            return;
        }
        Floor floor = NavigateManager.getCurrentFloor();
        if (floor == null)
        {
            mIsFinished = true;
            return;
        }
        int x = NavigateManager.getCurrentLocation().x;
        int y = NavigateManager.getCurrentLocation().y;
        int diff = NavigateManager.getCurrentFloorIndex() - mTargetFloorIndex;
        if (diff == 0)
        {
            // Target in same floor
            mNavigator.buildPathAsync(NavigateManager.getNearestNode(), mTarget);
            mPath = mNavigator.getPath(NavigateManager.getNearestNode(), mTarget);
        }
        else
        {
            if (mNearestEntryNode == null)
            {
                // Try to find new nearest entry node
                // diff < 0 -> target is higher
                // diff > 0 -> target is lower
                mNearestEntryNode = diff < 0 ? floor.findNearestNextEntryNode(x, y) : floor.findNearestPrevEntryNode(x, y);
                if (mNearestEntryNode == null)
                {
                    // Target is unreachable
                    Logger.error(LOGGER_TAG, "Target is unreachable.");
                    mIsFinished = true;
                    return;
                }
            }
            mNavigator.buildPathAsync(NavigateManager.getNearestNode(), mNearestEntryNode);
            mPath = mNavigator.getPath(NavigateManager.getNearestNode(), mNearestEntryNode);
        }
    }

    //endregion
}
