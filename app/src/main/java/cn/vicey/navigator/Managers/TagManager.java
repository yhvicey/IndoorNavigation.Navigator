package cn.vicey.navigator.Managers;

import android.support.annotation.NonNull;
import cn.vicey.navigator.File.TagParser;
import cn.vicey.navigator.File.TagSaver;
import cn.vicey.navigator.Models.Tag;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.Utils.Logger;
import cn.vicey.navigator.Utils.Tools;

import java.io.File;
import java.util.List;

/**
 * Tag manager, provide a set of methods to help manage tag files
 */
public class TagManager
{
    //region Constants

    private static final String LOGGER_TAG = "TagManager";

    private static final String TAG_DIR = "/tags"; // Tag file directory name

    //endregion

    //region Static fields

    private static File mTagDir; // Tag file directory

    //endregion

    //region Static methods

    /**
     * Delete specified tag file
     *
     * @param tagFileName Tag file name
     * @return Whether the deletion is succeed or not
     */
    public static boolean deleteTagFile(final @NonNull String tagFileName)
    {
        File tag = new File(mTagDir + "/" + tagFileName);
        return !tag.exists() || tag.delete();
    }

    /**
     * Gets tag directory
     *
     * @return Tag directory
     */
    public static File getTagDir()
    {
        return mTagDir;
    }

    /**
     * Check specified map has tag or not
     *
     * @param mapName Name of the map to check
     * @return Whether specified map has tag or not
     */
    public static boolean hasTags(final @NonNull String mapName)
    {
        return new File(mTagDir + "/" + mapName).exists();
    }

    /**
     * Initialize manager
     *
     * @return Whether the initialization is succeed or not
     */
    public static boolean init()
    {
        try
        {
            mTagDir = new File(Navigator.getFilesDirPath() + TAG_DIR);
            if (!(mTagDir.exists() || mTagDir.mkdir()))
            {
                Logger.error(LOGGER_TAG, "Failed to init map manager. Can not init tag directory.");
                return false;
            }
            return true;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init map manager.", t);
            return false;
        }
    }

    /**
     * Load specified map's tag
     *
     * @param mapName Specified map name
     * @return Tags of specified map, or null if specified map didn't have tag
     */
    public static List<Tag> loadTags(final @NonNull String mapName)
    {
        if (!hasTags(mapName)) return null;
        return TagParser.parse(new File(mTagDir + "/" + mapName));
    }

    /**
     * Save specified map's tag
     *
     * @param mapName Specified map name
     * @param tags    Tags to save
     * @return Whether the saving is succeed or not
     */
    public static boolean saveTags(final @NonNull String mapName, List<Tag> tags)
    {
        File file = TagSaver.save(mapName, tags);
        return file != null && Tools.copyFile(file, new File(mTagDir + "/" + mapName), true);
    }

    //endregion

    //region Constructor

    /**
     * Hidden for static class design pattern
     */
    private TagManager()
    {
        // no-op
    }

    //endregion
}
