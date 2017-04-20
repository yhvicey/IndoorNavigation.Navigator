package cn.vicey.navigator.Models.Nodes;

import cn.vicey.navigator.Debug.LocationProvider;
import cn.vicey.navigator.Managers.DebugManager;

public class UserNode
        extends NodeBase
{
    private static final Object SYNC_ROOT = new Object();

    private static UserNode mInstance;

    public static UserNode getInstance()
    {
        if (mInstance == null)
        {
            synchronized (SYNC_ROOT)
            {
                if (mInstance == null)
                {
                    mInstance = new UserNode();
                }
            }
        }
        return mInstance;
    }

    private UserNode()
    {
        super(0, 0);
    }

    @Override
    public NodeType getType()
    {
        _ return NodeType.USER_NODE;
    }

    @Override
    public int getX()
    {
        // TODO: Integrate location module and use its position here
        int x = 0;
        return DebugManager.isUseFakeLocation() ? LocationProvider.getCurrentLocation().x : x;
    }

    @Override
    public int getY()
    {
        // TODO: Integrate location module and use its position here
        int y = 0;
        return DebugManager.isUseFakeLocation() ? LocationProvider.getCurrentLocation().y : y;
    }
}
