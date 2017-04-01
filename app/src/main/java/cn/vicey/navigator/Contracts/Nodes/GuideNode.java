package cn.vicey.navigator.Contracts.Nodes;


import android.support.annotation.NonNull;

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
    public GuideNode(double x, double y)
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
    public GuideNode(double x, double y, @NonNull String name)
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

    /**
     * Sets the node's name.
     *
     * @param value Node's name..
     */
    public void setName(String value)
    {
        mName = value;
    }
}
