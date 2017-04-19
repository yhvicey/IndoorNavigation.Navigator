package cn.vicey.navigator.Models.Nodes;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class of nodes.
 */
public abstract class NodeBase
{
    public static class Link
    {
        private double mDistance;
        private NodeBase mTarget;

        public Link(@NonNull NodeBase target, double distance)
        {
            mTarget = target;
            mDistance = distance;
        }

        public double getDistance()
        {
            return mDistance;
        }

        public NodeBase getTarget()
        {
            return mTarget;
        }
    }

    private List<Link> mLinks = new ArrayList<>();
    private String mTag;
    protected int mX;
    protected int mY;

    /**
     * Initialize new instance of class NodeBase.
     *
     * @param x X position of the node.
     * @param y Y position of the node.
     */
    protected NodeBase(int x, int y)
    {
        mX = x;
        mY = y;
    }

    public List<Link> getLinks()
    {
        return mLinks;
    }

    /**
     * Gets node's type.
     *
     * @return Node's type.
     */
    public abstract NodeType getType();

    public String getTag()
    {
        return mTag;
    }

    /**
     * Gets node's x position.
     *
     * @return X position.
     */
    public int getX()
    {
        return mX;
    }

    /**
     * Gets node's y position.
     *
     * @return Y position.
     */
    public int getY()
    {
        return mY;
    }

    public void setTag(@NonNull String tag)
    {
        mTag = tag;
    }

    public double calcDistance(final @NonNull NodeBase other)
    {
        return Math.sqrt(Math.pow(mX - other.mX, 2) + Math.pow(mY - other.mY, 2));
    }

    public void clearTag()
    {
        mTag = null;
    }

    public void link(final @NonNull NodeBase other)
    {
        mLinks.add(new Link(other, calcDistance(other)));
    }

    @Override
    public String toString()
    {
        return getType().toString() + "(" + mX + ", " + mY + ")";
    }
}
