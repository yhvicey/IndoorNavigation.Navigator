package cn.vicey.navigator.Contracts.Nodes;

/**
 * Wall node.
 */
public class WallNode
        extends NodeBase
{
    /**
     * Initialize new instance of class WallNode.
     *
     * @param x X position of the node.
     * @param y Y position of the node.
     */
    public WallNode(double x, double y)
    {
        super(x, y);
    }

    /**
     * Gets the node's type.
     *
     * @return Node's type.
     */
    public NodeType getType()
    {
        return NodeType.WALL_NODE;
    }
}
