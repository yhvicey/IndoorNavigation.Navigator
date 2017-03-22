package cn.vicey.navigator.Contracts;

/**
 * Link class.
 */
public class Link
{
    private int mStart;
    private int mEnd;
    private Integer mEndFloor;

    /**
     * Initialize new instance of class Link.
     *
     * @param start Link's start node's index.
     * @param end   Link's end node's index.
     */
    public Link(int start, int end)
    {
        this(start, end, null);
    }

    /**
     * Initialize new instance of class Link.
     *
     * @param start    Link's start node's index.
     * @param end      Link's end node's index.
     * @param endFloor Link's end node's floor's index.
     */
    public Link(int start, int end, Integer endFloor)
    {
        mStart = start;
        mEnd = end;
        mEndFloor = endFloor;
    }

    /**
     * Gets link's start node's index.
     *
     * @return Link's start node's index.
     */
    public int getStart()
    {
        return mStart;
    }

    /**
     * Gets link's end node's index.
     *
     * @return Link's end node's index.
     */
    public int getEnd()
    {
        return mEnd;
    }

    /**
     * Gets link's end node's floor's index.
     *
     * @return Link's end node's floor's index.
     */
    public Integer getEndFloor()
    {
        return mEndFloor;
    }
}
