package cn.vicey.navigator.Models.Nodes;

import cn.vicey.navigator.Models.Floor;

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
    public WallNode(Floor parent, double x, double y)
    {
        super(parent, x, y);
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
