package cn.vicey.navigator.Navigate;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Managers.NavigateManager;
import cn.vicey.navigator.Models.Nodes.NodeBase;

/**
 * Navigate task class, provides navigate path to {@link NavigateManager} by using {@link FloorNavigator}
 */
public class NavigateTask
{
    //region Fields

    private NodeBase       mEnd;        // Task's end node
    private FloorNavigator mNavigator;  // Task's related navigator
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
        mNavigator = NavigateManager.getNavigator(floorIndex);
        mStart = start;
        mEnd = end;
    }

    //endregion

    //region Methods

    /**
     * Gets latest path from start node to end node
     *
     * @return Latest path
     */
    public Path getPath()
    {
        if (mNavigator == null) return null;
        return mNavigator.navigate(mStart, mEnd);
    }

    //endregion
}
