package cn.vicey.navigator.Models.Nodes;


/**
 * Guide node.
 */
public class GuideNode
        extends NodeBase
{
    private String mName;
    private Integer mNext;
    private Integer mPrev;

    /**
     * Initial new instance of class GuideNode.
     *
     * @param x X position of the node.
     * @param y Y position of the node.
     */
    public GuideNode(int x, int y, String name, Integer prev, Integer next)
    {
        super(x, y);
        mName = name;
        mNext = next;
        mPrev = prev;
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

    public Integer getNext()
    {
        return mNext;
    }

    public Integer getPrev()
    {
        return mPrev;
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
        return super.toString() + (getName() == null ? "" : (" Name: " + getName())) + (mNext == null ? "" : (" Next: " + mNext)) + (mPrev == null ? "" : (" Prev: " + mPrev));

    }
}
