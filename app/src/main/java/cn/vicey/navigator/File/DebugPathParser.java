package cn.vicey.navigator.File;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Debug.DebugPath;
import cn.vicey.navigator.Models.Nodes.DebugPathNode;
import cn.vicey.navigator.Utils.Logger;

/**
 * Debug path parser, provides a set of methods to parse debug path
 */
public final class DebugPathParser
{

    //region Constants

    private static final String LOGGER_TAG = "DebugPathParser";

    private static final int SESSION_COUNT       = 3; // Session count
    private static final int SESSION_FLOOR_INDEX = 0; // Floor index session
    private static final int SESSION_X           = 1; // X session
    private static final int SESSION_Y           = 2; // Y session

    /**
     * Value delimiting character
     */
    public static final String SESSION_DELIM = ",";

    //endregion

    //region Static methods

    /**
     * Generate tag object from xml parser
     *
     * @param value Node value
     * @return New debug path node object, or null if error occurred
     */
    private static DebugPathNode generateDebugPathNode(final @NonNull String value)
    {
        String[] sessions = value.split(SESSION_DELIM);
        if (sessions.length != SESSION_COUNT)
        {
            Logger.error(LOGGER_TAG, "Value has invalid session count: " + sessions.length + ". Expected: " + SESSION_COUNT + ".");
            return null;
        }
        int floorIndex;
        int x;
        int y;
        try
        {
            floorIndex = Integer.parseInt(sessions[SESSION_FLOOR_INDEX]);
            x = Integer.parseInt(sessions[SESSION_X]);
            y = Integer.parseInt(sessions[SESSION_Y]);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Node element must have valid x or y attribute.", t);
            return null;
        }
        return new DebugPathNode(x, y, floorIndex);
    }

    /**
     * Parse debug path from lines
     *
     * @param lines Lines to parse
     * @return Debug path object, or null if error occurred
     */
    public static DebugPath parse(final @NonNull String[] lines)
    {
        try
        {
            DebugPath path = new DebugPath();

            for (int i = 0; i < lines.length; i++)
            {
                DebugPathNode node = generateDebugPathNode(lines[i]);
                if (node == null)
                {
                    Logger.error(LOGGER_TAG, "Failed in building path node. Line: " + i + ".");
                    return null;
                }
                path.addNode(node);
            }
            return path;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to parse lines.", t);
            return null;
        }
    }

    //endregion

    //region Constructors

    /**
     * Hidden for static class design pattern
     */
    private DebugPathParser()
    {
        // no-op
    }

    //endregion
}
