package cn.vicey.navigator.File;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Debug.DebugPath;
import cn.vicey.navigator.Models.Nodes.DebugPathNode;
import cn.vicey.navigator.Utils.Logger;

import java.io.*;

/**
 * Debug path parser, provides a set of methods to parse debug path
 */
public final class DebugPathParser
{

    //region Constants

    private static final String LOGGER_TAG = "DebugPathParser";

    private static final String DELIM               = ",";   // Value delimiting character
    private static final int    SESSION_COUNT       = 3;     // Session count
    private static final int    SESSION_FLOOR_INDEX = 0;     // Floor index session
    private static final int    SESSION_X           = 1;     // X session
    private static final int    SESSION_Y           = 2;     // Y session
    private static final String SUPPORTED_VERSION   = "1.0"; // Supported version of this parser

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
        String[] sessions = value.split(DELIM);
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
     * Parse tags from InputStream
     *
     * @param stream InputStream to parse
     * @return Debug path object, or null if error occurred
     */
    private static DebugPath parseStream(final @NonNull InputStream stream)
    {
        try
        {
            DebugPath path = new DebugPath();

            InputStreamReader inReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(inReader);

            boolean versionChecked = false;
            String str;
            while ((str = reader.readLine()) != null)
            {
                // Check version
                if (!versionChecked)
                {
                    if (!SUPPORTED_VERSION.equals(str))
                    {
                        Logger.error(LOGGER_TAG, "Unsupported tag file version. Version: " + str);
                        return null;
                    }
                    versionChecked = true;
                }
                DebugPathNode node = generateDebugPathNode(str);
                if (node == null)
                {
                    Logger.error(LOGGER_TAG, "Failed in building path node.");
                    return null;
                }
                path.addNode(node);
            }
            return path;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to parse input stream.", t);
            return null;
        }
    }

    /**
     * Parse debug path from file
     *
     * @param file File to parse
     * @return Debug path object, or null if error occurred
     */
    public static DebugPath parse(final @NonNull File file)
    {
        try
        {
            Logger.info(LOGGER_TAG, "Start parsing file: " + file.getPath());
            if (!file.exists() || !file.isFile())
            {
                Logger.error(LOGGER_TAG, "Can't find debug path file. File path: " + file.getPath());
                return null;
            }
            DebugPath debugPath = parseStream(new FileInputStream(file));
            Logger.info(LOGGER_TAG, "Finished parsing file: " + file.getPath());
            return debugPath;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to parse tag file. File path:" + file.getPath(), t);
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
