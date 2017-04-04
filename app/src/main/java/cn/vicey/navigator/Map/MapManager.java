package cn.vicey.navigator.Map;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Contracts.Map;
import cn.vicey.navigator.Contracts.TagList;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.Share.Logger;
import cn.vicey.navigator.Share.Utils;

import java.io.File;
import java.util.List;

public class MapManager
{
    private static final String LOGGER_TAG = "MapManager";
    private static final String MAP_DIR = "/maps/";
    private static final String TAG_DIR = "/tags/";

    private static String mMapDirFullPath;
    private static String mTagDirFullPath;

    public static boolean initialize()
    {
        try
        {
            mMapDirFullPath = Navigator.getFilesDirPath() + MAP_DIR;
            mTagDirFullPath = Navigator.getFilesDirPath() + TAG_DIR;
            File mapDir = new File(mMapDirFullPath);
            if (!(mapDir.exists() || mapDir.mkdir()))
            {
                Logger.error(LOGGER_TAG, "Failed to initialize map manager. Can not initialize map directory.");
                return false;
            }
            File tagDir = new File(mTagDirFullPath);
            if (!(tagDir.exists() || tagDir.mkdir()))
            {
                Logger.error(LOGGER_TAG, "Failed to initialize map manager. Can not initialize tag directory.");
                return false;
            }
            return true;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize map manager. Can not initialize map manager.", t);
            return false;
        }
    }

    private MapManager()
    {
        // no-op
    }

    public static boolean deleteMapFile(final @NonNull String mapFileName)
    {
        File map = new File(mMapDirFullPath + mapFileName);
        return !map.exists() || map.delete();
    }

    public static boolean deleteTagFile(final @NonNull String tagFileName)
    {
        File tag = new File(mTagDirFullPath + tagFileName);
        return !tag.exists() || tag.delete();
    }

    public static List<File> getAllMapFiles()
    {
        try
        {
            File mapDir = new File(mMapDirFullPath);
            List<File> files = Utils.getFiles(mapDir);
            if (files == null)
            {
                Logger.error(LOGGER_TAG, "Failed to get all map files.");
                return null;
            }
            for (File file : files)
            {
                if (!validateMapFile(file))
                {
                    deleteMapFile(file.getName());
                    files.remove(file);
                }
            }
            return files;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to get all map files.", t);
            return null;
        }
    }

    public static List<File> getAllTagFiles()
    {
        try
        {
            File tagDir = new File(mTagDirFullPath);
            List<File> files = Utils.getFiles(tagDir);
            if (files == null)
            {
                Logger.error(LOGGER_TAG, "Failed to get all tag files.");
                return null;
            }
            for (File file : files)
            {
                if (!validateTagFile(file))
                {
                    deleteTagFile(file.getName());
                    files.remove(file);
                }
            }
            return files;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to get all tag files.", t);
            return null;
        }
    }

    public static String getAvailableDefaultMapFileName()
    {
        return Utils.getAvailableDefaultName(new File(mMapDirFullPath), ".xml");
    }

    public static String getAvailableDefaultTagFileName()
    {
        return Utils.getAvailableDefaultName(new File(mTagDirFullPath), ".xml");
    }

    public static boolean hasMapFile(final @NonNull String mapFileName)
    {
        return validateMapFile(new File(mMapDirFullPath + mapFileName));
    }

    public static boolean hasTagFile(final @NonNull String tagFileName)
    {
        return validateMapFile(new File(mTagDirFullPath + tagFileName));
    }

    public static Map loadMapFile(final @NonNull String mapFileName)
    {
        return MapParser.parse(new File(mMapDirFullPath + mapFileName));
    }

    public static TagList loadTagFile(final @NonNull String tagFileName)
    {
        return TagParser.parse(new File(mTagDirFullPath + tagFileName));
    }

    public static boolean renameMapFile(final @NonNull String mapName, final @NonNull String newMapName)
    {
        if (!hasMapFile(mapName)) return false;
        if (hasMapFile(newMapName)) return false;
        File map = new File(mMapDirFullPath + mapName);
        return map.renameTo(new File(mMapDirFullPath + newMapName));
    }

    public static boolean renameTagFile(final @NonNull String tagName, final @NonNull String newMapName)
    {
        if (!hasTagFile(tagName)) return false;
        if (hasTagFile(newMapName)) return false;
        File tag = new File(mTagDirFullPath + tagName);
        return tag.renameTo(new File(mTagDirFullPath + newMapName));
    }

    public static boolean saveMapFile(final @NonNull File src, boolean overwrite)
    {
        if (!MapParser.validate(src)) return false;
        String fileName = src.getName();
        if (!overwrite && hasMapFile(fileName)) fileName = getAvailableDefaultMapFileName();
        return Utils.copyFile(src, new File(mMapDirFullPath + fileName), overwrite);
    }

    public static boolean saveTagFile(final @NonNull File src, boolean overwrite)
    {
        if (!TagParser.validate(src)) return false;
        String fileName = src.getName();
        if (!overwrite && hasTagFile(fileName)) fileName = getAvailableDefaultTagFileName();
        return Utils.copyFile(src, new File(mTagDirFullPath + fileName), overwrite);
    }

    public static boolean validateMapFile(final @NonNull File mapFile)
    {
        return MapParser.validate(mapFile);
    }

    public static boolean validateTagFile(final @NonNull File tagFile)
    {
        return TagParser.validate(tagFile);
    }
}
