package cn.vicey.navigator.Contracts;

/**
 * Link class.
 */
public class Link
{
    private int mStart;
    private int mEnd;

    /**
     * Initialize new instance of class Link.
     *
     * @param start Link's start node's index.
     * @param end   Link's end node's index.
     */
    public Link(int start, int end)
    {
        mStart = start;
        mEnd = end;
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
}
