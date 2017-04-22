package cn.vicey.navigator.Models.Nodes;

import cn.vicey.navigator.Managers.DebugManager;
import cn.vicey.navigator.Managers.FakeLocateManager;
import cn.vicey.navigator.Managers.LocateManager;

/**
 * User node
 */
public class UserNode
        extends NodeBase
{
    //region Constants

    private static final Object SYNC_LOCK = new Object(); // Sync lock

    //endregion

    //region Static fields

    private static UserNode mInstance; // Singleton instance of user node class

    //endregion

    //region Static methods

    /**
     * Gets the singleton instance of user node class
     *
     * @return Singleton instance of user node class
     */
    public static UserNode getInstance()
    {
        if (mInstance == null)
        {
            synchronized (SYNC_LOCK)
            {
                if (mInstance == null)
                {
                    mInstance = new UserNode();
                }
            }
        }
        return mInstance;
    }

    //endregion

    //region Constructors

    /**
     * Hidden for singleton design pattern
     */
    private UserNode()
    {
        super(0, 0);
    }

    //endregion

    //region Accessors

    /**
     * Gets node's type
     *
     * @return Node's type
     */
    @Override
    public NodeType getType()
    {
        return NodeType.USER_NODE;
    }

    /**
     * Gets node's x axis
     *
     * @return X axis
     */
    @Override
    public int getX()
    {
        return DebugManager.isUseFakeLocation() ? FakeLocateManager.getCurrentLocation().x : LocateManager.getCurrentLocation().x;
    }

    /**
     * Gets node's y axis
     *
     * @return Y axis
     */
    @Override
    public int getY()
    {
        return DebugManager.isUseFakeLocation() ? FakeLocateManager.getCurrentLocation().y : LocateManager.getCurrentLocation().y;
    }

    //endregion
}
