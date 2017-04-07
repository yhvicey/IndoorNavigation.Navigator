package cn.vicey.navigator.Models.Nodes;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class of nodes.
 */
public abstract class NodeBase
{
    private List<Link> mLinks = new ArrayList<>();
    private Floor mParent;
    private String mTag;
    private double mX;
    private double mY;

    /**
     * Initialize new instance of class NodeBase.
     *
     * @param x X position of the node.
     * @param y Y position of the node.
     */
    protected NodeBase(Floor parent, double x, double y)
    {
        mParent = parent;
        mX = x;
        mY = y;
    }

    public Floor getParent()
    {
        return mParent;
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

    /**
     * Set node's x position.
     *
     * @param value X position.
     */
    public void setX(double value)
    {
        mX = value;
    }

    /**
     * Set node's y position.
     *
     * @param value Y position.
     */
    public void setY(double value)
    {
        mY = value;
    }

    public void clearTag()
    {
        mTag = null;
    }

    public double getDistance(NodeType type, int index)
    {
        NodeBase target = mParent.getNode(type, index);
        return getDistance(target);
    }

    public double getDistance(final @NonNull NodeBase other)
    {
        return Math.sqrt(Math.pow(mX - other.mX, 2) + Math.pow(mY - other.mY, 2));
    }

    public void link(NodeType type, int index)
    {
        for (Link link : mLinks)
        {
            if (link.getType() == type && link.getIndex() == index) return;
        }
        mLinks.add(new Link(this, type, index));
    }

    public void link(@NonNull NodeBase node)
    {
        int index = mParent.getNodeIndex(node);
        link(node.getType(), index);
    }

    public void onLoadFinished()
    {
        for (Link link : mLinks)
        {
            link.onLoadFinished();
        }
    }

    @Override
    public String toString()
    {
        return getType().toString() + "(" + mX + ", " + mY + ")";
    }
}
