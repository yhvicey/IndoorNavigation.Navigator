package cn.vicey.navigator.Models.Nodes;


/**
 * Guide node.
 */
public class GuideNode
        extends NodeBase
{
    private String mName;

    /**
     * Initial new instance of class GuideNode.
     *
     * @param x X position of the node.
     * @param y Y position of the node.
     */
    public GuideNode(int x, int y)
    {
        this(x, y, null);
    }

    /**
     * Initial new instance of class GuideNode.
     *
     * @param x    X position of the node.
     * @param y    Y position of the node.
     * @param name Name of the node.
     */
    public GuideNode(int x, int y, String name)
    {
        super(x, y);
        mName = name;
    }

    /**
     * Gets the node's name.
     *
     * @return Node's name.
     */
    public String getName()
    {
        return mName;
    }

    /**
     * Gets the node's type.
     *
     * @return Node's type.
     */
    @Override
    public NodeType getType()
    {
        return NodeType.GUIDE_NODE;
    }

    @Override
    public String toString()
    {
        return super.toString() + (mName == null ? "" : " Name: " + mName);
    }
}
