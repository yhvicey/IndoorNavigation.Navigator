package cn.vicey.navigator.Navigate;

import cn.vicey.navigator.Models.Nodes.NodeBase;
import cn.vicey.navigator.Models.Nodes.PathNode;

import java.util.ArrayList;
import java.util.List;

public class Path
        implements Cloneable
{
    private static final String LOGGER_TAG = "Path";

    private double mLength;
    private List<PathNode> mNodes = new ArrayList<>();

    public Path(NodeBase startNode)
    {
        if (startNode != null) appendTail(startNode);
    }

    public double getLength()
    {
        return mLength;
    }

    public Path appendHead(NodeBase node)
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
                return this;
            }
        }
        return this;
    }

    public Path appendHead(List<NodeBase> nodes)
    {
        for (NodeBase node : nodes)
        {
            appendHead(node);
        }
        return this;
    }

    public Path appendTail(NodeBase node)
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
                return this;
            }
        }
        return this;
    }

    public Path appendTail(List<NodeBase> nodes)
    {
        for (NodeBase node : nodes)
        {
            appendTail(node);
        }
        return this;
    }

    public boolean contains(NodeBase node)
    {
        return mNodes.contains(node);
    }

    public Path fork()
    {
        Path newPath = new Path(null);
        for (NodeBase node : mNodes) newPath.appendTail(node);
        return newPath;
    }

    public NodeBase getNearestNode(NodeBase target)
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

    public int indexOf(NodeBase node)
    {
        return mNodes.indexOf(node);
    }

    public boolean isEnd(NodeBase target)
    {
        return !mNodes.isEmpty() && mNodes.get(mNodes.size() - 1) == target;
    }

    public Path removeHead()
    {
        if (!mNodes.isEmpty()) mNodes.remove(0);
        return this;
    }

    public Path removeHead(int count)
    {
        while (count-- > 0) removeHead();
        return this;
    }

    public Path removeTail()
    {
        if (!mNodes.isEmpty()) mNodes.remove(mNodes.size() - 1);
        return this;
    }

    public Path removeTail(int count)
    {
        while (count-- > 0) removeTail();
        return this;
    }
}
