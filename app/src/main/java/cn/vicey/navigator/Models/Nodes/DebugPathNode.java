package cn.vicey.navigator.Models.Nodes;

/**
 * Debug path node, provides information for emulating user's walk path
 */
public class DebugPathNode
        extends PathNode
{
    //region Fields

    private int mFloorIndex; // Floor index

    //endregion

    //region Constructor

    /**
     * Initialize new instance of class {@link DebugPathNode}
     *
     * @param x          X axis
     * @param y          Y axis
     * @param floorIndex Floor index
     */
    public DebugPathNode(int x, int y, int floorIndex)
    {
        super(x, y);
        mFloorIndex = floorIndex;
    }

    //endregion

    //region Accessors

    /**
     * Gets debug path node's floor index
     *
     * @return Debug path node's floor index
     */
    public int getFloorIndex()
    {
        return mFloorIndex;
    }

    //endregion
}
