package cn.vicey.navigator.Contracts.Nodes;

public class EntryNode
        extends NodeBase
{
    private int mId;
    private Integer mNextEntry;
    private Integer mPrevEntry;

    public EntryNode(double x, double y, int id, Integer prevEntry, Integer nextEntry)
    {
        super(x, y);
        mId = id;
        mNextEntry = nextEntry;
        mPrevEntry = prevEntry;
    }

    public NodeType getType()
    {
        return NodeType.ENTRY_NODE;
    }

    public int getId()
    {
        return mId;
    }

    public Integer getNextEntry()
    {
        return mNextEntry;
    }

    public Integer getPrevEntry()
    {
        return mPrevEntry;
    }
}
