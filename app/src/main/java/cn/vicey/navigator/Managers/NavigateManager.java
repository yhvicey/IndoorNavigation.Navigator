package cn.vicey.navigator.Managers;

import android.support.annotation.NonNull;
import android.util.SparseArray;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Map;
import cn.vicey.navigator.Models.Nodes.NodeBase;
import cn.vicey.navigator.Models.Nodes.NodeType;
import cn.vicey.navigator.Navigate.FloorNavigator;

/**
 * Navigate manager, provides a set of methods to help navigate
 */
public final class NavigateManager
{
    //region Constants

    public static final int NO_SELECTED_FLOOR = -1; // Indicates no floor is selected

    //endregion

    //region Static fields

    private static Map mCurrentMap; // Current map object

    private static int                         mCurrentFloorIndex = NO_SELECTED_FLOOR;   // Current floor index
    private static SparseArray<FloorNavigator> mFloorNavigators   = new SparseArray<>(); // Floor navigators

    //endregion

    //region Static methods

    /**
     * Create navigate task for specified start node and end node
     *
     * @param startFloor Start floor
     * @param startNode  Start node
     * @param endFloor   End floor
     * @param endNode    End node
     */
    public static void createNavigateTask(int startFloor, final @NonNull NodeBase startNode, int endFloor, final @NonNull NodeBase endNode)
    {
        if (startNode.getType() != NodeType.GUIDE_NODE || endNode.getType() != NodeType.GUIDE_NODE) return;
        // TODO: judge floor and create navigate tasks here
    }

    /**
     * Gets current floor
     *
     * @return Current floor object, or null if no floor is selected
     */
    public static Floor getCurrentFloor()
    {
        return getFloor(mCurrentFloorIndex);
    }

    /**
     * Gets current floor index
     *
     * @return Current floor index, or NO_SELECTED_FLOOR if no floor is selected
     */
    public static int getCurrentFloorIndex()
    {
        return mCurrentFloorIndex;
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
        return getNavigator(getCurrentFloorIndex());
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
     * Try set current floor index to lower level
     *
     * @return True if didn't reach the ground floor, otherwise false
     */
    public static boolean goDownstairs()
    {
        if (mCurrentMap == null) return false;
        if (mCurrentFloorIndex <= 0) return false;
        mCurrentFloorIndex--;
        return true;
    }

    /**
     * Try set current floor index to higher level
     *
     * @return True if didn't reach the top floor, otherwise false
     */
    public static boolean goUpstairs()
    {
        if (mCurrentMap == null) return false;
        if (mCurrentFloorIndex >= mCurrentMap.getFloors().size() - 1) return false;
        mCurrentFloorIndex++;
        return true;
    }

    /**
     * Set current f
     *
     * @param map
     */
    public static void setCurrentMap(final @NonNull Map map)
    {
        mCurrentFloorIndex = NO_SELECTED_FLOOR;
        mCurrentMap = map;
        goUpstairs();
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
