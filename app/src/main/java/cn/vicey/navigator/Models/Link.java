package cn.vicey.navigator.Models;

import cn.vicey.navigator.Models.Nodes.NodeType;

/**
 * Link class.
 */
public class Link
{
    private int mEndIndex;
    private NodeType mEndType;
    private int mStartIndex;
    private NodeType mStartType;

    public Link(NodeType startType, int startIndex, NodeType endType, int endIndex)
    {
        mStartType = startType;
        mStartIndex = startIndex;
        mEndType = endType;
        mEndIndex = endIndex;
    }

    public int getEndIndex()
    {
        return mEndIndex;
    }

    public NodeType getEndType()
    {
        return mEndType;
    }

    public int getStartIndex()
    {
        return mStartIndex;
    }

    public NodeType getStartType()
    {
        return mStartType;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (!(obj instanceof Link)) return false;
        Link link = (Link) obj;
        return mStartType == link.mStartType && mStartIndex == link.mStartIndex && mEndType == link.mEndType && mEndIndex == link.mEndIndex || mStartType == link.mEndType && mStartIndex == link.mEndIndex && mEndType == link.mStartType && mEndIndex == link.mStartIndex;
    }

    @Override
    public int hashCode()
    {
        int leftHashCode = mStartIndex;
        leftHashCode = (leftHashCode * 397) ^ mStartType.getValue();
        int rightHashCode = mEndIndex;
        rightHashCode = (rightHashCode * 397) ^ mEndType.getValue();
        return leftHashCode ^ rightHashCode;
    }

    @Override
    public String toString()
    {
        return "Link Start(" + mStartType.toString() + ", " + mStartIndex + ") End(" + mEndType.toString() + ", " + mEndIndex + ")";
    }
}
