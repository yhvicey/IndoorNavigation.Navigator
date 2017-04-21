package cn.vicey.navigator.Models.Nodes;

import android.support.annotation.NonNull;

/**
 * Node types
 */
public enum NodeType
{
    /**
     * Wall node
     */
    WALL_NODE(0),
    /**
     * Guide node
     */
    GUIDE_NODE(1),
    /**
     * User node
     */
    USER_NODE(2),
    /**
     * Path node
     */
    PATH_NODE(3);

    //region Static fields

    private static final String GUIDE_NODE_TEXT = "GuideNode"; // Guide node text
    private static final String USER_NODE_TEXT  = "UserNode";  // User node text
    private static final String WALL_NODE_TEXT  = "WallNode";  // Wall node text
    private static final String PATH_NODE_TEXT  = "PathNode";  // Path node text

    //endregion

    //region Static methods

    /**
     * Parse node type from string
     *
     * @param nodeType String to parse
     * @return Node type
     */
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
            case PATH_NODE_TEXT:
                return PATH_NODE;
            default:
            {
                throw new IllegalArgumentException("Unsupported node type.");
            }
        }
    }

    //endregion

    //region Fields

    private int mValue; // Enum value

    //endregion

    //region Constructors

    /**
     * Initialize new instance of enum {@link NodeType}
     *
     * @param value Enum value
     */
    NodeType(int value)
    {
        mValue = value;
    }

    //endregion

    //region Accessors

    /**
     * Gets value of the enum
     *
     * @return Value of the enum
     */
    public int getValue()
    {
        return mValue;
    }

    //endregion

    //region Override methods

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
            case PATH_NODE:
                return PATH_NODE_TEXT;
        }
        return null;
    }

    //endregion
}
