package cn.vicey.navigator.Map;

import android.util.Xml;
import cn.vicey.navigator.Contracts.Floor;
import cn.vicey.navigator.Contracts.Link;
import cn.vicey.navigator.Contracts.Map;
import cn.vicey.navigator.Contracts.Nodes.GuideNode;
import cn.vicey.navigator.Contracts.Nodes.NodeBase;
import cn.vicey.navigator.Contracts.Nodes.WallNode;
import cn.vicey.navigator.Share.Logger;
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
    private static final String ELEMENT_MAP = "Map";
    private static final String ELEMENT_NODE = "Node";
    private static final String ELEMENT_LINK = "Link";
    private static final String ELEMENT_FLOOR = "Floor";
    private static final String ATTR_VERSION = "Version";
    private static final String ATTR_SCALE = "Scale";
    private static final String ATTR_TYPE = "Type";
    private static final String ATTR_X = "X";
    private static final String ATTR_Y = "Y";
    private static final String ATTR_NAME = "Name";
    private static final String ATTR_START = "Start";
    private static final String ATTR_END = "End";
    private static final String ATTR_END_FLOOR = "EndFloor";


    private MapParser()
    {
        // no-op
    }

    private static NodeBase generateNode(double x, double y, String type)
    {
        switch (type)
        {
            case "Wall":
            {
                return new WallNode(x, y);
            }
            case "Guide":
            {
                return new GuideNode(x, y);
            }
            default:
            {
                return null;
            }
        }
    }

    /**
     * Parse a map file from InputStream.
     *
     * @param stream Input stream to parse.
     * @return Parsed map file.
     */
    private static Map parseStream(InputStream stream)
    {
        try
        {
            boolean isParsingFloor = false;
            int scale = -1;
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
                    // Meet START_TAG
                    case XmlPullParser.START_TAG:
                    {
                        String elementName = parser.getName();
                        switch (elementName)
                        {
                            // Element map, parse map version and check it
                            case ELEMENT_MAP:
                            {
                                String version = parser.getAttributeValue(null, ATTR_VERSION);
                                if (!version.equals(SUPPORT_VERSIONS))
                                {
                                    Logger.error(LOGGER_TAG, "Unsupported map file version. Version: " + version);
                                    return null;
                                }
                                break;
                            }
                            // Element node, if is parsing a floor, then add the node to the floor, else break the parsing
                            case ELEMENT_NODE:
                            {
                                if (!isParsingFloor)
                                {
                                    Logger.error(LOGGER_TAG, "Meet unexpected element while parsing map. Line: " + parser.getLineNumber());
                                    return null;
                                }
                                String type = parser.getAttributeValue(null, ATTR_TYPE);
                                double x = Double.parseDouble(parser.getAttributeValue(null, ATTR_X));
                                double y = Double.parseDouble(parser.getAttributeValue(null, ATTR_Y));
                                NodeBase node = generateNode(x, y, type);
                                if (node == null)
                                {
                                    Logger.error(LOGGER_TAG, "Failed in building node. Line:" + parser.getLineNumber());
                                    return null;
                                }
                                if (node instanceof GuideNode)
                                {
                                    String name = parser.getAttributeValue(null, ATTR_NAME);
                                    ((GuideNode) node).setName(name);
                                }
                                nodes.add(node);
                                break;
                            }
                            // Element link, if is parsing a floor, then add the link to the floor, else break the parsing
                            case ELEMENT_LINK:
                            {
                                if (!isParsingFloor)
                                {
                                    Logger.error(LOGGER_TAG, "Meet unexpected element while parsing map. Line: " + parser.getLineNumber());
                                    return null;
                                }
                                int start = Integer.parseInt(parser.getAttributeValue(null, ATTR_START));
                                int end = Integer.parseInt(parser.getAttributeValue(null, ATTR_END));
                                String endFloorString = parser.getAttributeValue(null, ATTR_END_FLOOR);
                                Link link = endFloorString == null ? new Link(start, end) : new Link(start, end, Integer.parseInt(endFloorString));
                                links.add(link);
                                break;
                            }
                            // Element floor, if isn't parsing a floor, then clear all nodes and links and get its scale, else break parsing
                            case ELEMENT_FLOOR:
                            {
                                if (isParsingFloor)
                                {
                                    Logger.error(LOGGER_TAG, "Meet unexpected element while parsing floor. Line: " + parser.getLineNumber());
                                    return null;
                                }
                                nodes.clear();
                                links.clear();
                                String scaleString = parser.getAttributeValue(null, ATTR_SCALE);
                                if (scaleString == null)
                                {
                                    Logger.error(LOGGER_TAG, "Floor element does not has attribute \"Scale\". Line: " + parser.getLineNumber());
                                    return null;
                                }
                                scale = Integer.parseInt(scaleString);
                                isParsingFloor = true;
                                break;
                            }
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG:
                    {
                        String elementName = parser.getName();
                        switch (elementName)
                        {
                            case "Floor":
                            {
                                if (!isParsingFloor)
                                {
                                    Logger.error(LOGGER_TAG, "Meet unexpected element while parsing floor. Line: " + parser.getLineNumber());
                                    return null;
                                }
                                Floor floor = new Floor(scale, nodes, links);
                                floors.add(floor);
                                nodes.clear();
                                links.clear();
                                isParsingFloor = false;
                            }
                        }
                        break;
                    }
                }
                event = parser.next();
            }
            return new Map(floors);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to parse input stream.");
            return null;
        }
    }

    public static Map parse(String filePath)
    {
        if (filePath == null) return null;
        return parse(new File(filePath));
    }

    public static Map parse(File file)
    {
        try
        {
            if (file == null) return null;
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
            Logger.error(LOGGER_TAG, "Failed to parse map file. File path:" + file.getPath());
            t.printStackTrace();
            return null;
        }
    }
}
