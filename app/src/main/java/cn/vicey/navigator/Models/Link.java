package cn.vicey.navigator.Models;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Nodes.NodeBase;
import cn.vicey.navigator.Models.Nodes.NodeType;

/**
 * Link class.
 */
public class Link
{
    private double mDistance;
    private NodeBase mParent;
    private NodeType mType;
    private int mIndex;

    public Link(@NonNull NodeBase parent, NodeType type, int index)
    {
        mParent = parent;
        mType = type;
        mIndex = index;
    }

    public double getDistance()
    {
        return mDistance;
    }

    public NodeType getType()
    {
        return mType;
    }

    public int getIndex()
    {
        return mIndex;
    }

    public void onLoadFinished()
    {
        mDistance = mParent.getDistance(mType, mIndex);
    }

    @Override
    public String toString()
    {
        return "Link(" + mType.toString() + ", " + mIndex + ")";
    }
}
