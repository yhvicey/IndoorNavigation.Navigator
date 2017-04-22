package cn.vicey.navigator.Models.Nodes;

/**
 * Wall node
 */
public class WallNode
        extends NodeBase
{
    //region Constructors

    /**
     * Initialize new instance of class WallNode
     *
     * @param x X position of the node
     * @param y Y position of the node
     */
    public WallNode(int x, int y)
    {
        super(x, y);
    }

    //endregion

    //region Accessors

    /**
     * Gets the node's type
     *
     * @return Node's type
     */
    public NodeType getType()
    {
        return NodeType.WALL_NODE;
    }

    //endregion
}
