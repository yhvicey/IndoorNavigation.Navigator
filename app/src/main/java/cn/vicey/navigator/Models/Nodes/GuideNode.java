package cn.vicey.navigator.Models.Nodes;


import cn.vicey.navigator.Models.Floor;

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
    public GuideNode(Floor parent, double x, double y)
    {
        this(parent, x, y, null);
    }

    /**
     * Initial new instance of class GuideNode.
     *
     * @param x    X position of the node.
     * @param y    Y position of the node.
     * @param name Name of the node.
     */
    public GuideNode(Floor parent, double x, double y, String name)
    {
        super(parent, x, y);
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

    @Override
    public String toString()
    {
        return super.toString() + (mName == null ? "" : "Name: " + mName);
    }
}
