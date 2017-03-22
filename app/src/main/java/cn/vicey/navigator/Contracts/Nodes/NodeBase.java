package cn.vicey.navigator.Contracts.Nodes;

/**
 * Base class of nodes.
 */
public abstract class NodeBase
{
    private double mX = 0;
    private double mY = 0;

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
