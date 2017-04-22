package cn.vicey.navigator.Models.Nodes;


/**
 * Guide node
 */
public class GuideNode
        extends NodeBase
{
    //region Fields

    private String  mName; // Node name
    private Integer mNext; // Next floor's connected guide node index
    private Integer mPrev; // Previous floor's connected guide node index

    //endregion

    //region Constructors

    /**
     * Initial new instance of class {@link GuideNode}
     *
     * @param x    X position of the node
     * @param y    Y position of the node
     * @param name Name of the node
     * @param prev Previous floor's connected guide node index
     * @param next Next floor's connected guide node index
     */
    public GuideNode(int x, int y, String name, Integer prev, Integer next)
    {
        super(x, y);
        mName = name;
        mNext = next;
        mPrev = prev;
    }

    //endregion

    //region Accessors

    /**
     * Gets the node's name.
     *
     * @return Node's name, or null if node didn't has a name
     */
    public String getName()
    {
        return mName;
    }

    /**
     * Gets next floor's connected guide node's index
     *
     * @return Node's next floor's connected guide node's index, or null if node didn't have next floor's connected guide node
     */
    public Integer getNext()
    {
        return mNext;
    }

    /**
     * Gets previous floor's connected guide node's index
     *
     * @return Node's previous floor's connected guide node's index, or null if node didn't have previous floor's connected guide node
     */
    public Integer getPrev()
    {
        return mPrev;
    }

    /**
     * Gets the node's type.
     *
     * @return Node's type.
     */
    @Override
    public NodeType getType()
    {
        return NodeType.GUIDE_NODE;
    }

    //endregion

    //region Override methods

    @Override
    public String toString()
    {
        return super.toString() + (getName() == null ? "" : (" Name: " + getName())) + (mNext == null ? "" : (" Next: " + mNext)) + (mPrev == null ? "" : (" Prev: " + mPrev));

    }

    //endregion
}
