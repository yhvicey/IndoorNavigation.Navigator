package cn.vicey.navigator.Navigate;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Nodes.NodeBase;
import cn.vicey.navigator.Models.Nodes.PathNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Path class, represents a path in map
 */
public class Path
{
    //region Constants

    private static final String LOGGER_TAG = "Path";

    //endregion

    //region Fields

    private double mLength; // Path length

    private List<PathNode> mNodes = new ArrayList<>(); // Path nodes

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link Path}
     *
     * @param x X axis
     * @param y Y axis
     */
    public Path(int x, int y)
    {
        appendTail(new PathNode(x, y));
    }

    /**
     * Initialize new instance of class {@link Path}
     *
     * @param startNode Path's start node
     */
    public Path(NodeBase startNode)
    {
        if (startNode != null) appendTail(startNode);
    }

    //endregion

    //region Accessors

    /**
     * Gets path length
     *
     * @return Path length
     */
    public double getLength()
    {
        return mLength;
    }

    /**
     * Gets path's nodes
     *
     * @return Path's nodes
     */
    public List<PathNode> getNodes()
    {
        return mNodes;
    }

    /**
     * Gets path's node size
     *
     * @return Path's node size
     */
    public int getSize()
    {
        return mNodes.size();
    }

    //endregion

    //region Methods

    /**
     * Append a node to path's head
     *
     * @param node Node to append
     * @return This path
     */
    public Path appendHead(final @NonNull NodeBase node)
    {
        if (mNodes.isEmpty())
        {
            mNodes.add(new PathNode(node));
            return this;
        }
        NodeBase head = mNodes.get(0);
        for (NodeBase.Link link : head.getLinks())
        {
            if (link.getTarget() == node)
            {
                mNodes.add(0, new PathNode(node));
                mLength += link.getDistance();
                break;
            }
        }
        return this;
    }

    /**
     * Append nodes to path's head
     *
     * @param nodes Nodes to append
     * @return This path
     */
    public Path appendHead(final @NonNull List<NodeBase> nodes)
    {
        for (NodeBase node : nodes) appendHead(node);
        return this;
    }

    /**
     * Append a node to path's tail
     *
     * @param node Node to append
     * @return This path
     */
    public Path appendTail(final @NonNull NodeBase node)
    {
        if (mNodes.isEmpty())
        {
            mNodes.add(new PathNode(node));
            return this;
        }
        NodeBase tail = mNodes.get(mNodes.size() - 1);
        for (NodeBase.Link link : tail.getLinks())
        {
            if (link.getTarget() == node)
            {
                mNodes.add(new PathNode(node));
                mLength += link.getDistance();
                break;
            }
        }
        return this;
    }

    /**
     * Append nodes to path's tail
     *
     * @param nodes Nodes to append
     * @return This path
     */
    public Path appendTail(final @NonNull List<NodeBase> nodes)
    {
        for (NodeBase node : nodes) appendTail(node);
        return this;
    }

    /**
     * Indicate whether the path contains specified node
     *
     * @param node Specified node
     * @return Whether the path contains specified node
     */
    public boolean contains(final @NonNull NodeBase node)
    {
        return mNodes.contains(node);
    }

    /**
     * Fork a path from this path
     *
     * @return Forked path
     */
    public Path fork()
    {
        Path newPath = new Path(null);
        newPath.mLength = mLength;
        Collections.copy(newPath.mNodes, mNodes);
        return newPath;
    }

    /**
     * Gets the nearest node to target node in this path
     *
     * @param target Target node
     * @return The nearest node
     */
    public NodeBase getNearestNode(final @NonNull NodeBase target)
    {
        double distance = Double.MAX_VALUE;
        NodeBase result = null;
        for (NodeBase node : mNodes)
        {
            double newDistance = node.calcDistance(target);
            if (newDistance < distance)
            {
                result = node;
                distance = newDistance;
            }
        }
        return result;
    }

    /**
     * Gets the index of the specified node
     *
     * @param node Specified node
     * @return Index of the node
     */
    public int indexOf(final @NonNull NodeBase node)
    {
        return mNodes.indexOf(node);
    }

    /**
     * Check whether target node is the tail node of this path
     *
     * @param target Target node
     * @return Whether target node is the tail node of this path
     */
    public boolean isEnd(final @NonNull NodeBase target)
    {
        return !mNodes.isEmpty() && mNodes.get(mNodes.size() - 1) == target;
    }

    //endregion
}
