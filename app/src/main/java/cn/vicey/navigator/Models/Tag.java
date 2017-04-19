package cn.vicey.navigator.Models;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Nodes.NodeType;

public class Tag
{
    private int mFloor;
    private int mIndex;
    private NodeType mType;
    private String mValue;

    public Tag(int floor, int index, NodeType type, @NonNull String value)
    {
        mFloor = floor;
        mIndex = index;
        mType = type;
        mValue = value;
    }

    public int getFloor()
    {
        return mFloor;
    }

    public int getIndex()
    {
        return mIndex;
    }

    public NodeType getType()
    {
        return mType;
    }

    public String getValue()
    {
        return mValue;
    }
}
