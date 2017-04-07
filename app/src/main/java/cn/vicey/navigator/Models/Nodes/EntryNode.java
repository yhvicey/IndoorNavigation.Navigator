package cn.vicey.navigator.Models.Nodes;

import cn.vicey.navigator.Models.Floor;

public class EntryNode
        extends NodeBase
{
    private String mName;
    private Integer mNext;
    private Integer mPrev;

    public EntryNode(Floor parent, double x, double y, String name, Integer prev, Integer next)
    {
        super(parent, x, y);
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
}
