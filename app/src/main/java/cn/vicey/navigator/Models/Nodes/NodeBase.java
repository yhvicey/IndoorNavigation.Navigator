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

    private int mX;   // Node's x axis
    private int mY;   // Node's y axis

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

    //endregion

    //region Methods

    /**
     * Calculate distance to target point
     *
     * @param x Target point's x axis
     * @param y Target point's y axis
     * @return Distance to target node
     */
    public double calcDistance(int x, int y)
    {
        return Math.sqrt(Math.pow(mX - x, 2) + Math.pow(mY - y, 2));
    }

    /**
     * Calculate distance to target node
     *
     * @param target Target node
     * @return Distance to target node
     */
    public double calcDistance(final @NonNull NodeBase target)
    {
        return calcDistance(target.mX, target.mY);
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
