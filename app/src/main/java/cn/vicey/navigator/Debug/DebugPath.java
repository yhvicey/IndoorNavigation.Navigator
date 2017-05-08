package cn.vicey.navigator.Debug;

import android.support.annotation.NonNull;
import android.util.SparseIntArray;
import cn.vicey.navigator.File.DebugPathParser;
import cn.vicey.navigator.Models.Nodes.DebugPathNode;
import cn.vicey.navigator.Models.Nodes.PathNode;
import cn.vicey.navigator.Navigate.Path;
import cn.vicey.navigator.Utils.Tools;

/**
 * Debug path, provides fake path for emulating user's walk path
 */
public class DebugPath
{
    //region Constants

    private static final int READY   = -1; // Ready for emulating
    private static final int STOPPED = -2; // Not emulating now

    //endregion

    //region Fields

    private int            mCurrentIndex    = STOPPED;                 // Current node index
    private SparseIntArray mFloorIndexTable = new SparseIntArray();    // Floor index table
    private Path           mPath            = new Path(null); // Move path

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
        if (mCurrentIndex == READY) mCurrentIndex = 0;
        PathNode node = mPath.getNodes().get(mCurrentIndex);
        return new DebugPathNode(node.getX(), node.getY(), mFloorIndexTable.get(mCurrentIndex));
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

    /**
     * Add node to debug path
     *
     * @param node Node to add
     */
    public void addNode(final @NonNull DebugPathNode node)
    {
        mPath.appendTail(node);
        mFloorIndexTable.put(mPath.getSize() - 1, node.getFloorIndex());
    }

    /**
     * Move to next debug path node
     */
    public void moveNext()
    {
        if (!isEmulating()) return;
        if (mCurrentIndex < mPath.getSize() - 1) mCurrentIndex++;
    }

    /**
     * Starts emulating user's walk path
     */
    public void start()
    {
        if (!mPath.getNodes().isEmpty()) mCurrentIndex = READY;
    }

    /**
     * Stops emulating user's walk path
     */
    public void stop()
    {
        mCurrentIndex = STOPPED;
    }

    //endregion

    //region Override methods

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mPath.getSize(); i++)
        {
            PathNode node = mPath.getNodes().get(i);
            stringBuilder.append(mFloorIndexTable.get(i))
                         .append(DebugPathParser.SESSION_DELIM)
                         .append(node.getX())
                         .append(DebugPathParser.SESSION_DELIM)
                         .append(node.getY());
            if (i != mPath.getSize() - 1) stringBuilder.append(Tools.NEW_LINE);
        }
        return stringBuilder.toString();
    }

    //endregion
}
