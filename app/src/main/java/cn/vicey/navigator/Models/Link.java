package cn.vicey.navigator.Models;

import cn.vicey.navigator.Models.Nodes.NodeType;

/**
 * Link class
 */
public class Link
{
    //region Fields

    private int      mEndIndex;   // Link's end index
    private int      mStartIndex; // Link's start index
    private NodeType mType;       // Link's type

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link Link}
     *
     * @param type       Link's type
     * @param startIndex Link's start index
     * @param endIndex   Link's end index
     */
    public Link(NodeType type, int startIndex, int endIndex)
    {
        mEndIndex = endIndex;
        mStartIndex = startIndex;
        mType = type;
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
     * Gets link's start index
     *
     * @return Link's start index
     */
    public int getStartIndex()
    {
        return mStartIndex;
    }

    /**
     * Gets link's type
     *
     * @return Link's type
     */
    public NodeType getType()
    {
        return mType;
    }

    //endregion

    //region Override methods

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (!(obj instanceof Link)) return false;
        Link link = (Link) obj;
        return mType == link.mType && (mStartIndex == link.mStartIndex && mEndIndex == link.mEndIndex || mStartIndex == link.mEndIndex && mEndIndex == link.mStartIndex);
    }

    @Override
    public int hashCode()
    {
        int hashCode = mEndIndex;
        hashCode = (hashCode * 397) ^ mStartIndex;
        hashCode = (hashCode * 397) ^ mType.getValue();
        return hashCode;
    }

    @Override
    public String toString()
    {
        return "Link Type = " + mType.toString() + " (" + mStartIndex + ", " + mEndIndex + ")";
    }

    //endregion
}
