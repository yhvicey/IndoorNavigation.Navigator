package cn.vicey.navigator.Contracts;

import cn.vicey.navigator.Contracts.Nodes.NodeBase;

import java.util.List;

/**
 * Floor class.
 */
public class Floor
{
    private int mScale = 0;
    private List<NodeBase> mNodes = null;
    private List<Link> mLinks = null;

    /**
     * Initialize new instance of class Floor.
     *
     * @param scale Scale of the floor.
     * @param nodes Nodes of the floor.
     * @param links Links of the floor.
     */
    public Floor(int scale, List<NodeBase> nodes, List<Link> links)
    {
        mScale = scale;
        mNodes = nodes;
        mLinks = links;
    }

    /**
     * Gets the floor's scale.
     *
     * @return Floor's scale.
     */
    public int getScale()
    {
        return mScale;
    }

    /**
     * Gets the floor's nodes.
     *
     * @return Floor's nodes.
     */
    public List<NodeBase> getNodes()
    {
        return mNodes;
    }

    /**
     * Gets the floor's links.
     *
     * @return Floor's links.
     */
    public List<Link> getLinks()
    {
        return mLinks;
    }

    /**
     * Sets the floor's scale.
     *
     * @param value Floor's scale.
     */
    public void setScale(int value)
    {
        mScale = value;
    }
}
