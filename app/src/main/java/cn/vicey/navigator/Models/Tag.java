package cn.vicey.navigator.Models;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Nodes.NodeType;

/**
 * Tag class, provides a tag to attach to node
 */
public class Tag
{
    //region Fields

    private int      mFloorIndex; // Tag's floor index
    private int      mNodeIndex; // Tag's node index
    private NodeType mNodeType;  // Tag's node type
    private String   mValue; // Tag's value

    //endregion

    //region Constructor

    /**
     * Initialize new instance of class {@link Tag}
     *
     * @param floorIndex Tag's floor index
     * @param nodeIndex  Tag's node index
     * @param nodeType   Tag's node type
     * @param value      Tag's value
     */
    public Tag(int floorIndex, int nodeIndex, NodeType nodeType, @NonNull String value)
    {
        mFloorIndex = floorIndex;
        mNodeIndex = nodeIndex;
        mNodeType = nodeType;
        mValue = value;
    }

    //endregion

    //region Accessors

    /**
     * Gets tag's floor
     *
     * @return Tag's floor index
     */
    public int getFloorIndex()
    {
        return mFloorIndex;
    }

    /**
     * Gets tag's node index
     *
     * @return Tag's node index
     */
    public int getNodeIndex()
    {
        return mNodeIndex;
    }

    /**
     * Gets tag's node type
     *
     * @return Tag's node type
     */
    public NodeType getNodeType()
    {
        return mNodeType;
    }

    /**
     * Gets tag's value
     *
     * @return Tag's value
     */
    public String getValue()
    {
        return mValue;
    }

    //endregion
}
