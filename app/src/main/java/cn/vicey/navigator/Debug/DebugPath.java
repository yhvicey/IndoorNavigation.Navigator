package cn.vicey.navigator.Debug;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Nodes.PathNode;
import cn.vicey.navigator.Navigate.Path;

import java.util.Date;

/**
 * Debug path, provides fake path for emulating user's walk path
 */
public class DebugPath
{
    //region Constants

    private static final int NOT_EMULATING_NOW = -1; // Not emulating now

    //endregion

    //region Fields

    private Path mPath;  // Move path
    private int  mSpeed; // Move speed

    private long mStartTime = NOT_EMULATING_NOW; // Elapsed time

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link DebugPath}
     *
     * @param path  Move path
     * @param speed Move speed
     */
    public DebugPath(final @NonNull Path path, int speed)
    {
        mPath = path;
        mSpeed = speed;
    }

    //endregion

    //region Accessors

    /**
     * Gets current node
     *
     * @return Current node
     */
    public PathNode getCurrentNode()
    {
        return getNode(getElapsedTime());
    }

    /**
     * Gets elapsed time from start emulating
     *
     * @return Elapsed time, or 0 if emulating is not started
     */
    public long getElapsedTime()
    {
        if (mStartTime == NOT_EMULATING_NOW) return 0;
        return new Date().getTime() - mStartTime;
    }

    /**
     * Gets whether the debug path is emulating or not
     *
     * @return Whether the debug path is emulating or not
     */
    public boolean isEmulating()
    {
        return mStartTime != NOT_EMULATING_NOW;
    }

    //endregion

    //region Functions

    /**
     * Gets node by time and speed
     *
     * @param time Elapsed time
     * @return Node
     */
    private PathNode getNode(long time)
    {
        if (mPath.getNodes().isEmpty()) return null;
        long distance = time * mSpeed;
        if (distance >= mPath.getLength()) return mPath.getEnd();
        int index = 1;
        PathNode prev = mPath.getStart();
        while (distance > 0 && index < mPath.getNodes().size() - 1)
        {
            PathNode cur = mPath.getNodes().get(index);
            distance -= cur.calcDistance(prev);
            prev = cur;
        }
        return prev;
    }

    /**
     * Starts emulating user's walk path
     */
    public void start()
    {
        mStartTime = new Date().getTime();
    }

    /**
     * Stops emulating user's walk path
     */
    public void stop()
    {
        mStartTime = NOT_EMULATING_NOW;
    }

    //endregion
}
