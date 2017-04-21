package cn.vicey.navigator.Models;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Nodes.NodeType;

public class Tag
{

    public Tag(int floor, int index, NodeType type, @NonNull String value)
    private int      mFloorIndex; // Tag's floor index
    private int      mNodeIndex; // Tag's node index
    private NodeType mNodeType;  // Tag's node type
    private String   mValue; // Tag's value
    public Tag(int floorIndex, int nodeIndex, NodeType nodeType, @NonNull String value)
    {
        mFloorIndex = floorIndex;
        mNodeIndex = nodeIndex;
        mNodeType = nodeType;
        mValue = value;
    }

    public int getFloor()
    public int getFloorIndex()
    {
        return mFloorIndex;
    }

    public int getNodeIndex()
    {
        return mNodeIndex;
    }

    public NodeType getNodeType()
    {
        return mNodeType;
    }

    public String getValue()
    {
        return mValue;
    }
}
