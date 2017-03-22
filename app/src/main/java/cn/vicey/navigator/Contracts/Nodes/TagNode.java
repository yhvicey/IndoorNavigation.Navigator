package cn.vicey.navigator.Contracts.Nodes;

/**
 * Tag node.
 */
public class TagNode
        extends NodeBase
{
    private String mTag = null;

    /**
     * Initialize new instance of class TagNode.
     *
     * @param x   X position of the node.
     * @param y   Y position of the node.
     * @param tag Tag of the node.
     */
    public TagNode(double x, double y, String tag)
    {
        super(x, y);
        mTag = tag;
    }

    /**
     * Gets the node's type.
     *
     * @return Node's type.
     */
    public NodeType getType()
    {
        return NodeType.TAG_NODE;
    }

    /**
     * Gets the node's tag.
     *
     * @return Node's tag.
     */
    public String getTag()
    {
        return mTag;
    }

    /**
     * Sets the node's tag.
     *
     * @param value Node's tag.
     */
    public void setTag(String value)
    {
        mTag = value;
    }
}
