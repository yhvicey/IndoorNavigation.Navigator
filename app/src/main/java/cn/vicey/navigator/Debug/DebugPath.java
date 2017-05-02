package cn.vicey.navigator.Debug;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Nodes.DebugPathNode;
import cn.vicey.navigator.Navigate.Path;

/**
 * Debug path, provides fake path for emulating user's walk path
 */
public class DebugPath
{
    //region Constants

    private static final int STOPPED = -1; // Not emulating now

    //endregion

    //region Fields

    private int  mCurrentIndex = STOPPED;        // Current node index
    private Path mPath         = new Path(null); // Move path

    //endregion

    //region Accessors

    /**
     * Gets next node
     *
     * @return Current node, or null if it's not emulating now
     */
    public DebugPathNode getCurrentNode()
    {
        if (!isEmulating()) return null;
        return (DebugPathNode) mPath.getNodes().get(mCurrentIndex);
    }

    /**
     * Gets whether the debug path is emulating or not
     *
     * @return Whether the debug path is emulating or not
     */
    public boolean isEmulating()
    {
        return mCurrentIndex != STOPPED;
    }

    //endregion

    //region Methods

    public void addNode(final @NonNull DebugPathNode node)
    {
        mPath.appendTail(node);
    }

    /**
     * Move to next debug path node
     */
    public void moveNext()
    {
        if (!isEmulating()) return;
        if (mCurrentIndex < mPath.getSize()) mCurrentIndex++;
    }

    /**
     * Starts emulating user's walk path
     */
    public void start()
    {
        if (!mPath.getNodes().isEmpty()) mCurrentIndex = 0;
    }

    /**
     * Stops emulating user's walk path
     */
    public void stop()
    {
        mCurrentIndex = STOPPED;
    }

    //endregion
}
