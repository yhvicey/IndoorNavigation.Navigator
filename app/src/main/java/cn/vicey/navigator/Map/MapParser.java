package cn.vicey.navigator.Map;

import android.support.annotation.NonNull;
import android.util.Xml;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Link;
import cn.vicey.navigator.Models.Map;
import cn.vicey.navigator.Models.Nodes.GuideNode;
import cn.vicey.navigator.Models.Nodes.NodeBase;
import cn.vicey.navigator.Models.Nodes.NodeType;
import cn.vicey.navigator.Models.Nodes.WallNode;
import cn.vicey.navigator.Utils.Logger;
import cn.vicey.navigator.Utils.Tools;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Map parser class, provides a set of methods to parse map file
 */
public final class MapParser
{
    //region Constants

    private static final String LOGGER_TAG = "MapParser";

    private static final String ATTR_END_INDEX    = "EndIndex";   // EndIndex attribute name
    private static final String ATTR_END_TYPE     = "EndType";    // End type attribute name
    private static final String ATTR_NAME         = "Name";       // Name attribute name
    private static final String ATTR_NEXT         = "Next";       // Next attribute name
    private static final String ATTR_PREV         = "Prev";       // Prev attribute name
    private static final String ATTR_START_INDEX  = "StartIndex"; // Start index attribute name
    private static final String ATTR_START_TYPE   = "StartType";  // Start type attribute name
    private static final String ATTR_VERSION      = "Version";    // Version attribute name
    private static final String ATTR_X            = "X";          // X attribute name
    private static final String ATTR_Y            = "Y";          // Y attribute name
    private static final String DEFAULT_MAP_NAME  = "Untitled";   // Default map name
    private static final String ELEMENT_FLOOR     = "Floor";      // Floor element name
    private static final String ELEMENT_GUIDE     = "GuideNode";  // GuideNode element name
    private static final String ELEMENT_LINK      = "Link";       // Link element name
    private static final String ELEMENT_MAP       = "Map";        // Map element name
    private static final String ELEMENT_WALL      = "WallNode";   // WallNode element name
    private static final String SUPPORTED_VERSION = "1.1";        // Supported version of this parser

    //endregion

    //region Static methods

    /**
     * Generate link object from xml parser
     *
     * @param parser Xml parser
     * @return New link object, or null if errors occurred
     */
    private static Link generateLink(final @NonNull XmlPullParser parser)
    {
        try
        {
            NodeType startType = NodeType.parse(parser.getAttributeValue(null, ATTR_START_TYPE));
            int startIndex = Integer.parseInt(parser.getAttributeValue(null, ATTR_START_INDEX));
            NodeType endType = NodeType.parse(parser.getAttributeValue(null, ATTR_END_TYPE));
            int endIndex = Integer.parseInt(parser.getAttributeValue(null, ATTR_END_INDEX));
            return new Link(startType, startIndex, endType, endIndex);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Link element must have valid type or index attribute.", t);
            return null;
        }
    }

    /**
     * Generate node object from xml parser
     *
     * @param parser Xml parser
     * @return New node object, or null if error occurred
     */
    private static NodeBase generateNode(final @NonNull XmlPullParser parser)
    {
        NodeType type;
        try
        {
            type = NodeType.parse(parser.getName());
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Node element must have valid type attribute. Line: " + parser.getLineNumber());
            return null;
        }
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
        switch (type)
        {
            case GUIDE_NODE:
            {
                String name = parser.getAttributeValue(null, ATTR_NAME);
                Integer prev;
                Integer next;
                try
                {
                    String prevStr = parser.getAttributeValue(null, ATTR_PREV);
                    String nextStr = parser.getAttributeValue(null, ATTR_NEXT);
                    prev = prevStr == null ? null : Integer.parseInt(prevStr);
                    next = nextStr == null ? null : Integer.parseInt(nextStr);
                }
                catch (Throwable t)
                {
                    Logger.error(LOGGER_TAG, "Entry element must have valid PrevEntry or NextEntry attribute. Line: " + parser
                            .getLineNumber(), t);
                    return null;
                }
                return new GuideNode(x, y, name, prev, next);
            }
            case WALL_NODE:
            {
                return new WallNode(x, y);
            }
            default:
            {
                Logger.error(LOGGER_TAG, "Node element must have valid type attribute. Line: " + parser.getLineNumber());
                return null;
            }
        }
    }

    /**
     * Parse a map from InputStream
     *
     * @param stream Input stream to parse
     * @return New map object, or null if error occurred
     */
    private static Map parseStream(final @NonNull InputStream stream)
    {
        try
        {
            String mapName = DEFAULT_MAP_NAME;

            Floor currentFloor = null;
            List<Link> links = new ArrayList<>();
            List<Floor> floors = new ArrayList<>();

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
                            // Meet map element, parse map name and version, then check it
                            //region Map element
                            case ELEMENT_MAP:
                            {
                                String version = parser.getAttributeValue(null, ATTR_VERSION);
                                if (!SUPPORTED_VERSION.equals(version))
                                {
                                    Logger.error(LOGGER_TAG, "Unsupported map file version. Version: " + version);
                                    return null;
                                }
                                String mapNameStr = parser.getAttributeValue(null, ATTR_NAME);
                                if (Tools.isStringEmpty(mapNameStr, true))
                                {
                                    Logger.info(LOGGER_TAG, "Map does not have a name. Will use default name.");
                                }
                                else
                                {
                                    mapName = mapNameStr;
                                }
                                break;
                            }
                            //endregion
                            // Meet floor element, if isn't parsing a floor, then clear all nodes and links and get its scale, else break parsing
                            //region Floor element
                            case ELEMENT_FLOOR:
                            {
                                if (currentFloor != null)
                                {
                                    Logger.error(LOGGER_TAG, "Meet unexpected element while parsing floor. Line: " + parser
                                            .getLineNumber());
                                    return null;
                                }
                                currentFloor = new Floor();
                                break;
                            }
                            //endregion
                            // Meet node element, if is parsing a floor, then add the node to the floor, else break the parsing
                            //region Node element
                            case ELEMENT_GUIDE:
                            case ELEMENT_WALL:
                            {
                                if (currentFloor == null)
                                {
                                    Logger.error(LOGGER_TAG, "Meet unexpected element while parsing map. Line: " + parser
                                            .getLineNumber());
                                    return null;
                                }
                                NodeBase node = generateNode(parser);
                                if (node == null)
                                {
                                    Logger.error(LOGGER_TAG, "Failed in building node. Line:" + parser.getLineNumber());
                                    return null;
                                }
                                currentFloor.addNode(node);
                                break;
                            }
                            //endregion
                            // Meet link element, if is parsing a floor, then add the link to the floor, else break the parsing
                            //region Link element
                            case ELEMENT_LINK:
                            {
                                try
                                {
                                    if (currentFloor == null)
                                    {
                                        Logger.error(LOGGER_TAG, "Meet unexpected element while parsing map. Line: " + parser
                                                .getLineNumber());
                                        return null;
                                    }
                                    Link link = generateLink(parser);
                                    if (link == null)
                                    {
                                        Logger.error(LOGGER_TAG, "Failed in building link. Line:" + parser.getLineNumber());
                                        return null;
                                    }
                                    links.add(link);
                                    break;
                                }
                                catch (Throwable t)
                                {
                                    Logger.error(LOGGER_TAG, "Failed in building link. Line:" + parser.getLineNumber(), t);
                                    return null;
                                }
                            }
                            //endregion
                        }
                        break;
                    }
                    //endregion
                    //region End tag
                    case XmlPullParser.END_TAG:
                    {
                        String elementName = parser.getName();
                        switch (elementName)
                        {
                            //region Map element
                            case ELEMENT_MAP:
                            {
                                if (currentFloor != null)
                                {
                                    Logger.error(LOGGER_TAG, "Meet unexpected element while parsing floor. Line: " + parser
                                            .getLineNumber());
                                    return null;
                                }
                                return new Map(mapName, floors);
                            }
                            //endregion
                            //region Floor element
                            case ELEMENT_FLOOR:
                            {
                                if (currentFloor == null)
                                {
                                    Logger.error(LOGGER_TAG, "Meet unexpected element while parsing floor. Line: " + parser
                                            .getLineNumber());
                                    return null;
                                }
                                currentFloor.addLinks(links);
                                floors.add(currentFloor);
                                links = new ArrayList<>();
                                currentFloor = null;
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
            return new Map(mapName, floors);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to parse input stream.", t);
            return null;
        }
    }

    /**
     * Parse a map from file
     *
     * @param file File to parse
     * @return New map object, or null if error occurred
     */
    public static Map parse(final @NonNull File file)
    {
        try
        {
            Logger.info(LOGGER_TAG, "Start parsing file: " + file.getPath());
            if (!file.exists() || !file.isFile())
            {
                Logger.error(LOGGER_TAG, "Can't find map file. File path: " + file.getPath());
                return null;
            }
            Map map = parseStream(new FileInputStream(file));
            Logger.info(LOGGER_TAG, "Finished parsing file: " + file.getPath());
            return map;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to parse map file. File path:" + file.getPath(), t);
            return null;
        }
    }

    //endregion

    //region Constructors

    /**
     * Hidden for static class design pattern
     */
    private MapParser()
    {
        // no-op
    }

    //endregion
}
