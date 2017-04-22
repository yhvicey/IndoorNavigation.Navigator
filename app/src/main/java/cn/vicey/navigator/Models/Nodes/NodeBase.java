package cn.vicey.navigator.Models.Nodes;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class of nodes
 */
public abstract class NodeBase
{
    //region Inner classes

    /**
     * Link class, present a link between this node and target node
     */
    public static class Link
    {
        //region Fields

        private double   mDistance; // Distance to target node
        private NodeBase mTarget;   // Target node

        //endregion

        //region Constructors

        /**
         * Initialize new instance of class {@link Link}
         *
         * @param target   Target node
         * @param distance Distance to target node
         */
        public Link(@NonNull NodeBase target, double distance)
        {
            mTarget = target;
            mDistance = distance;
        }

        //endregion

        //region Accessors

        /**
         * Gets distance to target node
         *
         * @return Distance to target node
         */
        public double getDistance()
        {
            return mDistance;
        }

        /**
         * Gets target node
         *
         * @return Target node
         */
        public NodeBase getTarget()
        {
            return mTarget;
        }

        //endregion
    }

    //endregion

    //region Fields

    private String mTag; // Node's tag
    private int    mX;   // Node's x axis
    private int    mY;   // Node's y axis

    private List<Link> mLinks = new ArrayList<>(); // Node's links

    //endregion

    //region Constructor

    /**
     * Initialize new instance of class {@link NodeBase}
     *
     * @param x X position of the node
     * @param y Y position of the node
     */
    protected NodeBase(int x, int y)
    {
        mX = x;
        mY = y;
    }

    //endregion

    //region Accessors

    /**
     * Get node's links
     *
     * @return Node's links
     */
    public List<Link> getLinks()
    {
        return mLinks;
    }

    /**
     * Get node's tag
     *
     * @return Node's tag, or null if node didn't have a tag
     */
    public String getTag()
    {
        return mTag;
    }

    /**
     * Gets node's type
     *
     * @return Node's type
     */
    public abstract NodeType getType();

    /**
     * Gets node's x axis
     *
     * @return X axis
     */
    public int getX()
    {
        return mX;
    }

    /**
     * Gets node's y axis
     *
     * @return Y axis
     */
    public int getY()
    {
        return mY;
    }

    /**
     * Set node's tag
     *
     * @param tag Node's tag
     */
    public void setTag(@NonNull String tag)
    {
        mTag = tag;
    }

    //endregion

    //region Methods

    /**
     * Calculate distance to target node
     *
     * @param target Target node
     * @return Distance to target node
     */
    public double calcDistance(final @NonNull NodeBase target)
    {
        return Math.sqrt(Math.pow(mX - target.mX, 2) + Math.pow(mY - target.mY, 2));
    }

    /**
     * Clear node's tag
     */
    public void clearTag()
    {
        mTag = null;
    }

    /**
     * Create a link to target node
     *
     * @param target Target node
     */
    public void link(final @NonNull NodeBase target)
    {
        mLinks.add(new Link(target, calcDistance(target)));
    }

    //endregion

    //region Override methods

    @Override
    public String toString()
    {
        return getType().toString() + "(" + mX + ", " + mY + ")";
    }

    //endregion
}
