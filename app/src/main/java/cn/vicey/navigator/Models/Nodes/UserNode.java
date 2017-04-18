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
                    mInstance = new UserNode(NavigateManager.getCurrentLocation().x, NavigateManager.getCurrentLocation().y);
                }
            }
        }
        return mInstance;
    }

    private UserNode(int x, int y)
    {
        super(x, y);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.USER_NODE;
    }
}
