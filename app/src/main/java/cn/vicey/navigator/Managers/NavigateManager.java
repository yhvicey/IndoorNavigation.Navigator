package cn.vicey.navigator.Managers;

import android.support.annotation.NonNull;
import android.util.SparseArray;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Map;
import cn.vicey.navigator.Models.Nodes.NodeBase;
import cn.vicey.navigator.Models.Nodes.NodeType;
import cn.vicey.navigator.Navigate.FloorNavigator;

public final class NavigateManager
{
    public static final int NO_SELECTED_FLOOR = -1;

    private static int mCurrentFloorIndex = NO_SELECTED_FLOOR;
    private static Map mCurrentMap;
    private static SparseArray<FloorNavigator> mFloorNavigators = new SparseArray<>();

    public static Floor getCurrentFloor()
    {
        if (mCurrentMap == null) return null;
        if (mCurrentFloorIndex == NO_SELECTED_FLOOR || mCurrentFloorIndex >= mCurrentMap.getFloors().size())
            return null;
        return mCurrentMap.getFloors().get(mCurrentFloorIndex);
    }

    public static int getCurrentFloorIndex()
    {
        return mCurrentFloorIndex;
    }

    public static Map getCurrentMap()
    {
        return mCurrentMap;
    }

    public static FloorNavigator getCurrentNavigator()
    {
        return getNavigator(getCurrentFloorIndex());
    }

    public static Floor getFloor(int floorIndex)
    {
        if (mCurrentMap == null) return null;
        if (floorIndex < 0) return null;
        if (floorIndex > mCurrentMap.getFloors().size() - 1) return null;
        return mCurrentMap.getFloors().get(floorIndex);
    }

    public static FloorNavigator getNavigator(int floorIndex)
    {
        Floor floor = getFloor(floorIndex);
        if (floor == null) return null;
        // If target floor's navigator didn't exist then create it
        if (mFloorNavigators.get(floorIndex) == null) mFloorNavigators.put(floorIndex, new FloorNavigator(floor));
        return mFloorNavigators.get(floorIndex);
    }

    public static boolean goDownstairs()
    {
        if (mCurrentMap == null) return false;
        if (mCurrentFloorIndex <= 0) return false;
        mCurrentFloorIndex--;
        return true;
    }

    public static boolean goUpstairs()
    {
        if (mCurrentMap == null) return false;
        if (mCurrentFloorIndex >= mCurrentMap.getFloors().size() - 1) return false;
        mCurrentFloorIndex++;
        return true;
    }

    public static void setCurrentMap(final @NonNull Map map)
    {
        mCurrentFloorIndex = NO_SELECTED_FLOOR;
        mCurrentMap = map;
        goUpstairs();
    }

    public static void createNavigateTask(int startFloor, final @NonNull NodeBase startNode, int endFloor, final @NonNull NodeBase endNode)
    {
        if (startNode.getType() != NodeType.GUIDE_NODE || endNode.getType() != NodeType.GUIDE_NODE) return;
        // TODO: judge floor and create navigate tasks here
    }

    private NavigateManager()
    {
        // no-op
    }
}
