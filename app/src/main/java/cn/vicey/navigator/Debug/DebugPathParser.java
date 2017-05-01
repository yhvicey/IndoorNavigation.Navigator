package cn.vicey.navigator.Debug;

import android.support.annotation.NonNull;
import android.util.Xml;
import cn.vicey.navigator.Models.Nodes.PathNode;
import cn.vicey.navigator.Navigate.Path;
import cn.vicey.navigator.Utils.Logger;
import cn.vicey.navigator.Utils.Tools;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Debug path parser, provides a set of methods to parse debug path
 */
public final class DebugPathParser
{

    //region Constants

    private static final String LOGGER_TAG = "DebugPathParser";

    private static final String ATTR_SPEED        = "Speed";   // Speed attribute name
    private static final String ATTR_X            = "X"; // X attribute name
    private static final String ATTR_Y            = "Y";  // Y attribute name
    private static final String ATTR_VERSION      = "Version";    // Version attribute name
    private static final String ELEMENT_NODE      = "Node";        // Tag element name
    private static final String ELEMENT_PATH      = "Path";       // Tags element name
    private static final String SUPPORTED_VERSION = "1.0";        // Supported version of this parser

    //endregion

    //region Static methods

    /**
     * Generate tag object from xml parser
     *
     * @param parser Xml parser
     * @return New tag object, or null if error occurred
     */
    private static PathNode generatePathNode(final @NonNull XmlPullParser parser)
    {
        int x;
        int y;
        try
        {
            x = Integer.parseInt(parser.getAttributeValue(null, ATTR_X));
            y = Integer.parseInt(parser.getAttributeValue(null, ATTR_Y));
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Node element must have valid x or y attribute. Line: " + parser.getLineNumber(), t);
            return null;
        }
        return new PathNode(x, y);
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
            Path path = null;
            int speed = 0;

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream, Tools.FILE_ENCODING);
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)
            {
                switch (event)
                {
                    //region Start tag
                    case XmlPullParser.START_TAG:
                    {
                        String elementName = parser.getName();
                        switch (elementName)
                        {
                            // Meet path element, parse speed and version, then check it
                            //region Path element
                            case ELEMENT_PATH:
                            {
                                String version = parser.getAttributeValue(null, ATTR_VERSION);
                                if (!SUPPORTED_VERSION.equals(version))
                                {
                                    Logger.error(LOGGER_TAG, "Unsupported tag file version. Version: " + version);
                                    return null;
                                }
                                try
                                {
                                    speed = Integer.parseInt(parser.getAttributeValue(null, ATTR_SPEED));
                                    if (speed < 0) throw new Exception("Speed must be greater than or equals to 0.");
                                }
                                catch (Throwable t)
                                {
                                    Logger.error(LOGGER_TAG, "Path element must have a valid speed attribute.", t);
                                    return null;
                                }
                                break;
                            }
                            //endregion
                            // Meet tag element, add the tag to the list
                            //region Tag element
                            case ELEMENT_NODE:
                            {
                                PathNode node = generatePathNode(parser);
                                if (node == null)
                                {
                                    Logger.error(LOGGER_TAG, "Failed in building path node. Line:" + parser.getLineNumber());
                                    return null;
                                }
                                if (path == null) path = new Path(node);
                                else path.appendTail(node);
                                break;
                            }
                            //endregion
                        }
                        break;
                    }
                    //endregion
                }
                event = parser.next();
            }
            return path == null ? null : new DebugPath(path, speed);
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
