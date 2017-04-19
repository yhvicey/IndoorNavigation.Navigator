package cn.vicey.navigator.Models.Nodes;

import cn.vicey.navigator.Navigate.NavigateManager;

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
        mInstance.mX = NavigateManager.getCurrentLocation().x;
        mInstance.mY = NavigateManager.getCurrentLocation().y;
        return mInstance;
    }

    private UserNode()
    {
        super(0, 0);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.USER_NODE;
    }
}
