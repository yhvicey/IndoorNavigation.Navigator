package cn.vicey.navigator.Models.Nodes;

public class EntryNode
        extends NodeBase
{
    private String mName;
    private Integer mNextEntry;
    private Integer mPrevEntry;

    public EntryNode(double x, double y, String name, Integer prevEntry, Integer nextEntry)
    {
        super(x, y);
        mName = name;
        mNextEntry = nextEntry;
        mPrevEntry = prevEntry;
    }

    public NodeType getType()
    {
        return NodeType.ENTRY_NODE;
    }

    public String getName()
    {
        return mName;
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
