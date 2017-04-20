package cn.vicey.navigator.Navigate;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Managers.NavigateManager;
import cn.vicey.navigator.Models.Nodes.NodeBase;
import cn.vicey.navigator.Models.Nodes.UserNode;

public class NavigateTask
{
    private NodeBase mEnd;
    private int mFloorIndex;
    private FloorNavigator mNavigator;
    private NodeBase mStart;

    public NavigateTask(int floorIndex, final @NonNull NodeBase start, final @NonNull NodeBase end)
    {
        mFloorIndex = floorIndex;
        mNavigator = NavigateManager.getCurrentNavigator();
        mStart = start;
        mEnd = end;
    }

    public Path getPath()
    {
        FloorNavigator navigator = NavigateManager.getNavigator(mFloorIndex);
        if (navigator == null) return null;

        Path guidePath = mNavigator.navigate(mStart, mEnd);
        return guidePath.appendHead(UserNode.getInstance());
    }
}
