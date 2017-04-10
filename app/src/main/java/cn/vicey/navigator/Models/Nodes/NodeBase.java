package cn.vicey.navigator.Models.Nodes;

import android.support.annotation.NonNull;

/**
 * Base class of nodes.
 */
public abstract class NodeBase
{
    private String mTag;
    private double mX;
    private double mY;

    /**
     * Initialize new instance of class NodeBase.
     *
     * @param x X position of the node.
     * @param y Y position of the node.
     */
    protected NodeBase(double x, double y)
    {
        mX = x;
        mY = y;
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
    public double getX()
    {
        return mX;
    }

    /**
     * Gets node's y position.
     *
     * @return Y position.
     */
    public double getY()
    {
        return mY;
    }

    public void setTag(@NonNull String tag)
    {
        mTag = tag;
    }

    public void clearTag()
    {
        mTag = null;
    }

    public double getDistance(final @NonNull NodeBase other)
    {
        return Math.sqrt(Math.pow(mX - other.mX, 2) + Math.pow(mY - other.mY, 2));
    }

    @Override
    public String toString()
    {
        return getType().toString() + "(" + mX + ", " + mY + ")";
    }
}
