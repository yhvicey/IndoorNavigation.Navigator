package cn.vicey.navigator.Map;

import android.support.annotation.NonNull;
import android.util.Xml;
import cn.vicey.navigator.Models.Tag;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.Utils.Logger;
import cn.vicey.navigator.Utils.Tools;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Tag saver class, provides a set of methods to save tags
 */
public final class TagSaver
{
    //region Constants

    private static final String LOGGER_TAG = "TagSaver";

    private static final String ATTR_FLOOR_INDEX  = "FloorIndex"; // Floor index attribute name
    private static final String ATTR_NODE_INDEX   = "NodeIndex";  // Node index attribute name
    private static final String ATTR_NODE_TYPE    = "NodeType";   // Node type attribute name
    private static final String ATTR_VALUE        = "Value";      // Value attribute name
    private static final String ATTR_VERSION      = "Version";    // Version attribute name
    private static final String ELEMENT_TAG       = "Tag";        // Tag element name
    private static final String ELEMENT_TAGS      = "Tags";       // Tags element name
    private static final String SUPPORTED_VERSION = "1.1";        // Supported version of this parser

    //endregion

    //region Static methods

    /**
     * Generate tag element
     *
     * @param tag        Tag to save
     * @param serializer Xml serializer
     */
    private static void generateTag(final @NonNull Tag tag, XmlSerializer serializer)
    {
        try
        {
            serializer.startTag(null, ELEMENT_TAG);
            serializer.attribute(null, ATTR_FLOOR_INDEX, Integer.toString(tag.getFloorIndex()));
            serializer.attribute(null, ATTR_NODE_INDEX, Integer.toString(tag.getNodeIndex()));
            serializer.attribute(null, ATTR_NODE_TYPE, tag.getNodeType().toString());
            serializer.attribute(null, ATTR_VALUE, tag.getValue());
            serializer.endTag(null, ELEMENT_TAG);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to save tag.", t);
        }
    }

    /**
     * Save specified map's tags
     *
     * @param mapName Name of specified map
     * @param tags    Tags to save
     * @return New file object if saving is succeed, or null if error occurred
     */
    public static File save(final @NonNull String mapName, final @NonNull List<Tag> tags)
    {
        try
        {
            File file = new File(Navigator.getCacheDirPath() + mapName);
            if (!(file.exists() || file.createNewFile()))
            {
                Logger.error(LOGGER_TAG, "Failed to create save file.");
                return null;
            }
            FileOutputStream fos = new FileOutputStream(file);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, Tools.FILE_ENCODING);
            serializer.startDocument(Tools.FILE_ENCODING, true);
            serializer.startTag(null, ELEMENT_TAGS);
            serializer.attribute(null, ATTR_VERSION, SUPPORTED_VERSION);
            for (Tag tag : tags)
            {
                if (tag != null) generateTag(tag, serializer);
            }
            serializer.endTag(null, ELEMENT_TAGS);
            serializer.endDocument();
            fos.flush();
            fos.close();
            return file;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to save tags.", t);
            return null;
        }
    }

    //endregion

    //region Constructors

    /**
     * Hidden for static class design pattern
     */
    private TagSaver()
    {
        // no-op
    }

    //endregion
}
