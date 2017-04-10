package cn.vicey.navigator.Models;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Nodes.NodeType;

/**
 * Link class.
 */
public class Link
{
    private int mEndIndex;
    private NodeType mEndType;
    private Floor mParent;
    private int mStartIndex;
    private NodeType mStartType;

    public Link(NodeType startType, int startIndex, NodeType endType, int endIndex)
    {
        mStartType = startType;
        mStartIndex = startIndex;
        mEndType = endType;
        mEndIndex = endIndex;
    }

    public double getDistance()
    {
        return mParent.getDistance(mStartType, mStartIndex, mEndType, mEndIndex);
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

    public void onAdd(final @NonNull Floor parent)
    {
        mParent = parent;
    }

    @Override
    public String toString()
    {
        return "Link Start(" + mStartType.toString() + ", " + mStartIndex + ") End(" + mEndType.toString() + ", " + mEndIndex + ")";
    }
}
