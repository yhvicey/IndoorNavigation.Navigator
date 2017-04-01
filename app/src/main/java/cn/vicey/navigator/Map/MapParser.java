package cn.vicey.navigator.Map;

import android.support.annotation.NonNull;
import android.util.Xml;
import cn.vicey.navigator.Contracts.Floor;
import cn.vicey.navigator.Contracts.Link;
import cn.vicey.navigator.Contracts.Map;
import cn.vicey.navigator.Contracts.Nodes.EntryNode;
import cn.vicey.navigator.Contracts.Nodes.GuideNode;
import cn.vicey.navigator.Contracts.Nodes.NodeBase;
import cn.vicey.navigator.Contracts.Nodes.WallNode;
import cn.vicey.navigator.Share.Logger;
import cn.vicey.navigator.Share.Utils;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * MapParser class for parsing map file and tag file.
 */
public class MapParser
{
    private static final String LOGGER_TAG = "MapParser";
    private static final String MAP_FILE_ENCODING = "utf-8";
    private static final String SUPPORT_VERSIONS = "1.0";
    private static final String ATTR_VERSION = "Version";
    private static final String ATTR_SCALE = "Scale";
    private static final String ATTR_TYPE = "Type";
    private static final String ATTR_X = "X";
    private static final String ATTR_Y = "Y";
    private static final String ATTR_NAME = "Name";
    private static final String ATTR_ID = "Id";
    private static final String ATTR_PREV_ENTRY = "PrevEntry";
    private static final String ATTR_NEXT_ENTRY = "NextEntry";
    private static final String ATTR_START = "Start";
    private static final String ATTR_END = "End";
    private static final String ELEMENT_MAP = "Map";
    private static final String ELEMENT_NODE = "Node";
    private static final String ELEMENT_LINK = "Link";
    private static final String ELEMENT_FLOOR = "Floor";
    private static final String TYPE_ENTRY = "Entry";
    private static final String TYPE_GUIDE = "Guide";
    private static final String TYPE_WALL = "Wall";
    private static final String DEFAULT_MAP_NAME = "Untitled";
    private static int STANDARD_SCALE = 100000; // <pixel> * STANDARD_SCALE = <real centimeter>


    private MapParser()
    {
        // no-op
    }

    private static NodeBase generateNode(@NonNull final XmlPullParser parser, int scaleFactor)
    {
        String type = parser.getAttributeValue(null, ATTR_TYPE);
        if (Utils.isStringEmpty(type, true))
        {
            Logger.error(LOGGER_TAG, "Node element must have valid type attribute. Line: " + parser.getLineNumber());
            return null;
        }
        double x;
        double y;
        try
        {
            x = Double.parseDouble(parser.getAttributeValue(null, ATTR_X)) * scaleFactor;
            y = Double.parseDouble(parser.getAttributeValue(null, ATTR_Y)) * scaleFactor;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Node element must have valid x or y attribute. Line: " + parser.getLineNumber(), t);
            return null;
        }
        switch (type)
        {
            case TYPE_ENTRY:
            {
                int id;
                Integer prev;
                Integer next;
                try
                {
                    id = Integer.parseInt(parser.getAttributeValue(null, ATTR_ID));
                }
                catch (Throwable t)
                {
                    Logger.error(LOGGER_TAG, "Entry element must have valid id attribute. Line: " + parser.getLineNumber(), t);
                    return null;
                }
                try
                {
                    String prevStr = parser.getAttributeValue(null, ATTR_PREV_ENTRY);
                    String nextStr = parser.getAttributeValue(null, ATTR_NEXT_ENTRY);
                    prev = prevStr == null ? null : Integer.parseInt(prevStr);
                    next = nextStr == null ? null : Integer.parseInt(nextStr);
                }
                catch (Throwable t)
                {
                    Logger.error(LOGGER_TAG, "Entry element must have valid PrevEntry or NextEntry attribute. Line: " + parser
                            .getLineNumber(), t);
                    return null;
                }
                return new EntryNode(x, y, id, prev, next);
            }
            case TYPE_GUIDE:
            {
                String name = parser.getAttributeValue(null, ATTR_NAME);
                return new GuideNode(x, y, name);
            }
            case TYPE_WALL:
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

    private static Link generateLink(@NonNull final XmlPullParser parser)
    {
        try
        {
            int start = Integer.parseInt(parser.getAttributeValue(null, ATTR_START));
            int end = Integer.parseInt(parser.getAttributeValue(null, ATTR_END));
            return new Link(start, end);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Link element must have valid Start or End attribute.");
            return null;
        }
    }

    /**
     * Parse a map file from InputStream.
     *
     * @param stream Input stream to parse.
     * @return Parsed map file.
     */
    private static Map parseStream(@NonNull InputStream stream)
    {
        try
        {
            boolean isParsingFloor = false;
            int scaleFactor = 1;
            String mapName = DEFAULT_MAP_NAME;
            List<Floor> floors = new ArrayList<>();
            List<NodeBase> nodes = new ArrayList<>();
            List<Link> links = new ArrayList<>();

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream, MAP_FILE_ENCODING);
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
                                if (!version.equals(SUPPORT_VERSIONS))
                                {
                                    Logger.error(LOGGER_TAG, "Unsupported map file version. Version: " + version);
                                    return null;
                                }
                                String mapNameStr = parser.getAttributeValue(null, ATTR_NAME);
                                if (Utils.isStringEmpty(mapNameStr, true))
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
                            // Meet node element, if is parsing a floor, then add the node to the floor, else break the parsing
                            //region Node element
                            case ELEMENT_NODE:
                            {
                                if (!isParsingFloor)
                                {
                                    Logger.error(LOGGER_TAG, "Meet unexpected element while parsing map. Line: " + parser
                                            .getLineNumber());
                                    return null;
                                }
                                NodeBase node = generateNode(parser, scaleFactor);
                                if (node == null)
                                {
                                    Logger.error(LOGGER_TAG, "Failed in building node. Line:" + parser.getLineNumber());
                                    return null;
                                }
                                nodes.add(node);
                                break;
                            }
                            //endregion
                            // Meet link element, if is parsing a floor, then add the link to the floor, else break the parsing
                            //region Link element
                            case ELEMENT_LINK:
                            {
                                if (!isParsingFloor)
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
                            //endregion
                            // Meet floor element, if isn't parsing a floor, then clear all nodes and links and get its scale, else break parsing
                            //region Floor element
                            case ELEMENT_FLOOR:
                            {
                                if (isParsingFloor)
                                {
                                    Logger.error(LOGGER_TAG, "Meet unexpected element while parsing floor. Line: " + parser
                                            .getLineNumber());
                                    return null;
                                }
                                nodes.clear();
                                links.clear();
                                try
                                {
                                    int scale = Integer.parseInt(parser.getAttributeValue(null, ATTR_SCALE));
                                    scaleFactor = STANDARD_SCALE / scale;
                                }
                                catch (Throwable t)
                                {
                                    Logger.error(LOGGER_TAG, "Floor element must have a valid attribute Scale. Line: " + parser
                                            .getLineNumber());
                                    return null;
                                }
                                isParsingFloor = true;
                                break;
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
                            case ELEMENT_FLOOR:
                            {
                                if (!isParsingFloor)
                                {
                                    Logger.error(LOGGER_TAG, "Meet unexpected element while parsing floor. Line: " + parser
                                            .getLineNumber());
                                    return null;
                                }
                                Floor floor = new Floor(nodes, links);
                                floors.add(floor);
                                nodes.clear();
                                links.clear();
                                isParsingFloor = false;
                                break;
                            }
                            case ELEMENT_MAP:
                            {
                                if (isParsingFloor)
                                {
                                    Logger.error(LOGGER_TAG, "Meet unexpected element while parsing floor. Line: " + parser
                                            .getLineNumber());
                                    return null;
                                }
                            }
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
            Logger.error(LOGGER_TAG, "Failed to parse input stream.");
            return null;
        }
    }

    public static Map parse(@NonNull String filePath)
    {
        return parse(new File(filePath));
    }

    public static Map parse(@NonNull File file)
    {
        try
        {
            Logger.info(LOGGER_TAG, "Start parsing file: " + file.getPath());
            if (!file.exists())
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
}
