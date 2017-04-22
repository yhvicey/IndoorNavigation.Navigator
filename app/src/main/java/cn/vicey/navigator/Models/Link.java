package cn.vicey.navigator.Models;

import cn.vicey.navigator.Models.Nodes.NodeType;

/**
 * Link class
 */
public class Link
{
    //region Fields

    private int      mEndIndex;   // Link's end index
    private NodeType mEndType;    // Link's end type
    private int      mStartIndex; // Link's start index
    private NodeType mStartType;  // Link's start type

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link Link}
     *
     * @param startType  Link's start type
     * @param startIndex Link's start index
     * @param endType    Link's end type
     * @param endIndex   Link's end index
     */
    public Link(NodeType startType, int startIndex, NodeType endType, int endIndex)
    {
        mStartType = startType;
        mStartIndex = startIndex;
        mEndType = endType;
        mEndIndex = endIndex;
    }

    //endregion

    //region Accessors

    /**
     * Gets link's end index
     *
     * @return Link's end index
     */
    public int getEndIndex()
    {
        return mEndIndex;
    }

    /**
     * Gets link's end type
     *
     * @return Link's end type
     */
    public NodeType getEndType()
    {
        return mEndType;
    }

    /**
     * Gets link's start index
     *
     * @return Link's start index
     */
    public int getStartIndex()
    {
        return mStartIndex;
    }

    /**
     * Gets link's start type
     *
     * @return Link's start type
     */
    public NodeType getStartType()
    {
        return mStartType;
    }

    //endregion

    //region Override methods

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

    //endregion
}
