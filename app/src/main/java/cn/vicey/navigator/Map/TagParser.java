package cn.vicey.navigator.Map;

import android.support.annotation.NonNull;
import android.util.Xml;
import cn.vicey.navigator.Contracts.Nodes.NodeType;
import cn.vicey.navigator.Contracts.Tag;
import cn.vicey.navigator.Contracts.TagList;
import cn.vicey.navigator.Share.Logger;
import cn.vicey.navigator.Share.Utils;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TagParser
{
    private static final String LOGGER_TAG = "TagParser";
    private static final String SUPPORT_VERSIONS = "1.0";
    private static final String ATTR_VERSION = "Version";
    private static final String ATTR_NAME = "Name";
    private static final String ATTR_FLOOR = "Floor";
    private static final String ATTR_INDEX = "Index";
    private static final String ATTR_TYPE = "Type";
    private static final String ATTR_VALUE = "Value";
    private static final String ELEMENT_TAGS = "Tags";
    private static final String ELEMENT_TAG = "Tag";
    private static final String TYPE_ENTRY = "Entry";
    private static final String TYPE_GUIDE = "Guide";
    private static final String TYPE_WALL = "Wall";

    private TagParser()
    {
        // no-op
    }

    private static Tag generateTag(final @NonNull XmlPullParser parser)
    {
        String type = parser.getAttributeValue(null, ATTR_TYPE);
        if (Utils.isStringEmpty(type, true))
        {
            Logger.error(LOGGER_TAG, "Tag element must have valid type attribute. Line: " + parser.getLineNumber());
            return null;
        }
        int floor;
        int index;
        try
        {
            floor = Integer.parseInt(parser.getAttributeValue(null, ATTR_FLOOR));
            index = Integer.parseInt(parser.getAttributeValue(null, ATTR_INDEX));
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Tag element must have valid floor or index attribute. Line: " + parser.getLineNumber(), t);
            return null;
        }
        String value = parser.getAttributeValue(null, ATTR_VALUE);
        if (value == null)
        {
            Logger.error(LOGGER_TAG, "Tag element must have valid value attribute. Line: " + parser.getLineNumber());
            return null;
        }
        switch (type)
        {
            case TYPE_ENTRY:
            {
                return new Tag(floor, index, NodeType.ENTRY_NODE, value);
            }
            case TYPE_GUIDE:
            {
                return new Tag(floor, index, NodeType.GUIDE_NODE, value);
            }
            case TYPE_WALL:
            {
                return new Tag(floor, index, NodeType.WALL_NODE, value);
            }
            default:
            {
                Logger.error(LOGGER_TAG, "Node element must have valid type attribute. Line: " + parser.getLineNumber());
                return null;
            }
        }
    }

    private static TagList parseStream(final @NonNull InputStream stream)
    {
        try
        {
            String name = null;
            List<Tag> tagList = new ArrayList<>();

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream, Utils.FILE_ENCODING);
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
                            // Meet tags element, parse name and version, then check it
                            //region Tags element
                            case ELEMENT_TAGS:
                            {
                                String version = parser.getAttributeValue(null, ATTR_VERSION);
                                if (!version.equals(SUPPORT_VERSIONS))
                                {
                                    Logger.error(LOGGER_TAG, "Unsupported tag file version. Version: " + version);
                                    return null;
                                }
                                String mapNameStr = parser.getAttributeValue(null, ATTR_NAME);
                                if (Utils.isStringEmpty(mapNameStr, true))
                                {
                                    Logger.info(LOGGER_TAG, "Tag does not have a name.");
                                }
                                else
                                {
                                    name = mapNameStr;
                                }
                                break;
                            }
                            //endregion
                            // Meet tag element, add the tag to the floor
                            //region Node element
                            case ELEMENT_TAG:
                            {
                                Tag tag = generateTag(parser);
                                if (tag == null)
                                {
                                    Logger.error(LOGGER_TAG, "Failed in building tag. Line:" + parser.getLineNumber());
                                    return null;
                                }
                                tagList.add(tag);
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
            return new TagList(name, tagList);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to parse input stream.", t);
            return null;
        }
    }

    public static TagList parse(final @NonNull File file)
    {
        try
        {
            Logger.info(LOGGER_TAG, "Start parsing file: " + file.getPath());
            if (!file.exists() || !file.isFile())
            {
                Logger.error(LOGGER_TAG, "Can't find tag file. File path: " + file.getPath());
                return null;
            }
            TagList tagList = parseStream(new FileInputStream(file));
            Logger.info(LOGGER_TAG, "Finished parsing file: " + file.getPath());
            return tagList;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to parse tag file. File path:" + file.getPath(), t);
            return null;
        }
    }

    public static boolean validate(final @NonNull File file)
    {
        if (!file.exists() || !file.isFile()) return false;
        try
        {
            Logger.info(LOGGER_TAG, "Validating tag file " + file + ".");
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new FileInputStream(file), Utils.FILE_ENCODING);
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)
            {
                switch (event)
                {
                    case XmlPullParser.START_TAG:
                    {
                        boolean result = parser.getName().equals(ELEMENT_TAGS);
                        Logger.info(LOGGER_TAG, "Finished validating tag file. Result is " + result + ".");
                        return result;
                    }
                }
                event = parser.next();
            }
            Logger.info(LOGGER_TAG, "Invalid tag file.");
            return false;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to validate file.", t);
            return false;
        }
    }
}
