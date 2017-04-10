package cn.vicey.navigator.Models.Nodes;

public class EntryNode
        extends NodeBase
{
    private String mName;
    private Integer mNext;
    private Integer mPrev;

    public EntryNode(double x, double y, String name, Integer prev, Integer next)
    {
        super(x, y);
        mName = name;
        mNext = next;
        mPrev = prev;
    }

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

    public NodeType getType()
    {
        return NodeType.ENTRY_NODE;
    }

    @Override
    public String toString()
    {
        return super.toString() + (mName == null ? "" : (" Name: " + mName)) + (mNext == null ? "" : (" Next: " + mNext)) + (mPrev == null ? "" : (" Prev: " + mPrev));
    }
}
