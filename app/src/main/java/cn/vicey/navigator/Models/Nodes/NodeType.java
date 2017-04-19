package cn.vicey.navigator.Models.Nodes;

import android.support.annotation.NonNull;

/**
 * Node types.
 */
public enum NodeType
{
    /**
     * Wall node.
     */
    WALL_NODE(0),
    /**
     * Guide node.
     */
    GUIDE_NODE(1),
    USER_NODE(2);

    private static final String GUIDE_NODE_TEXT = "GuideNode";
    private static final String WALL_NODE_TEXT = "WallNode";
    private static final String USER_NODE_TEXT = "UserNode";

    public static NodeType parse(@NonNull String nodeType)
    {
        switch (nodeType)
        {
            case GUIDE_NODE_TEXT:
                return GUIDE_NODE;
            case WALL_NODE_TEXT:
                return WALL_NODE;
            case USER_NODE_TEXT:
                return USER_NODE;
            default:
            {
                throw new IllegalArgumentException("Unsupported node type.");
            }
        }
    }

    private int mValue;

    NodeType(int value)
    {
        mValue = value;
    }

    public int getValue()
    {
        return mValue;
    }

    @Override
    public String toString()
    {
        switch (this)
        {
            case GUIDE_NODE:
                return GUIDE_NODE_TEXT;
            case WALL_NODE:
                return WALL_NODE_TEXT;
            case USER_NODE:
                return USER_NODE_TEXT;
        }
        return null;
    }
}
