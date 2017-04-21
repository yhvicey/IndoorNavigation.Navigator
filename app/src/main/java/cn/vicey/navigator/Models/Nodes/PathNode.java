package cn.vicey.navigator.Models.Nodes;

import android.support.annotation.NonNull;

/**
 * Path node
 */
public class PathNode
        extends NodeBase
{
    // region Constructors

    /**
     * Initialize new instance of class {@link PathNode}
     *
     * @param x X axis
     * @param y Y axis
     */
    public PathNode(int x, int y)
    {
        super(x, y);
    }

    /**
     * Initialize new instance of class {@link PathNode} from a existing node
     *
     * @param node Existing node
     */
    public PathNode(final @NonNull NodeBase node)
    {
        super(node.getX(), node.getY());
    }

    //endregion

    //region Accessors

    /**
     * Gets node's type
     *
     * @return Node's type
     */
    @Override
    public NodeType getType()
    {
        return NodeType.PATH_NODE;
    }

    //endregion
}
