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
    WALL_NODE,

    /**
     * Entry node.
     */
    ENTRY_NODE,

    /**
     * Guide node.
     */
    GUIDE_NODE;

    private static final String ENTRY_NODE_TEXT = "EntryNode";
    private static final String GUIDE_NODE_TEXT = "GuideNode";
    private static final String WALL_NODE_TEXT = "WallNode";

    public static NodeType parse(@NonNull String nodeType)
    {
        switch (nodeType)
        {
            case ENTRY_NODE_TEXT:
            {
                return ENTRY_NODE;
            }
            case GUIDE_NODE_TEXT:
            {
                return GUIDE_NODE;
            }
            case WALL_NODE_TEXT:
            {
                return WALL_NODE;
            }
            default:
            {
                throw new IllegalArgumentException("Unsupported node type.");
            }
        }
    }
}
