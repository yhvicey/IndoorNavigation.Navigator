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

public final class TagSaver
{
    private static final String LOGGER_TAG = "TagSaver";
    private static final String ATTR_VALUE = "Value";
    private static final String ATTR_VERSION = "Version";
    private static final String ELEMENT_TAG = "Tag";
    private static final String ELEMENT_TAGS = "Tags";
    private static final String SUPPORTED_VERSION = "1.1";

    private static void saveTag(final @NonNull Tag tag, XmlSerializer serializer)
    private static final String ATTR_FLOOR_INDEX  = "FloorIndex"; // Floor index attribute name
    private static final String ATTR_NODE_INDEX   = "NodeIndex";  // Node index attribute name
    private static final String ATTR_NODE_TYPE    = "NodeType";   // Node type attribute name
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
                if (tag != null) saveTag(tag, serializer);
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

    private TagSaver()
    {
        // no-op
    }
}
