package cn.vicey.navigator.Models.Nodes;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base class of nodes.
 */
public abstract class NodeBase
{
    private List<NodeBase> mAdjacentNodes;
    private HashMap<NodeBase, Double> mDistanceTable;
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
        mTag = null;
        mX = x;
        mY = y;
    }

    public void addAdjacentNode(@NonNull final NodeBase node)
    {
        if (mAdjacentNodes == null) mAdjacentNodes = new ArrayList<>();
        if (mAdjacentNodes.contains(node)) return;
        getDistance(node);
        mAdjacentNodes.add(node);
    }

    public void clearTag()
    {
        mTag = null;
    }

    public List<NodeBase> getAdjacentNodes()
    {
        return mAdjacentNodes;
    }

    public double getDistance(@NonNull final NodeBase other)
    {
        if (mDistanceTable == null) mDistanceTable = new HashMap<>();
        if (mDistanceTable.containsKey(other)) return mDistanceTable.get(other);
        double distance = Math.sqrt(Math.pow(mX - other.mX, 2) + Math.pow(mY - other.mY, 2));
        mDistanceTable.put(other, distance);
        return distance;
    }

    public String getTag()
    {
        return mTag;
    }

    /**
     * Gets node's type.
     *
     * @return Node's type.
     */
    public abstract NodeType getType();

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
}
