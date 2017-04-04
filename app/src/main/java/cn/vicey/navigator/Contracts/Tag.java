package cn.vicey.navigator.Contracts;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Contracts.Nodes.NodeType;

public class Tag
{
    private static final String ENTRY_NODE_TEXT = "Entry";
    private static final String GUIDE_NODE_TEXT = "Guide";
    private static final String WALL_NODE_TEXT = "Wall";
    private static final String UNKNOWN_NODE_TEXT = "Unknown";

    public static final String getNodeText(NodeType type)
    {
        switch (type)
        {
            case ENTRY_NODE:
            {
                return ENTRY_NODE_TEXT;
            }
            case GUIDE_NODE:
            {
                return GUIDE_NODE_TEXT;
            }
            case WALL_NODE:
            {
                return WALL_NODE_TEXT;
            }
            default:
            {
                return UNKNOWN_NODE_TEXT;
            }
        }
    }

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
