package cn.vicey.navigator.Managers;

import android.support.annotation.NonNull;
import cn.vicey.navigator.File.MapParser;
import cn.vicey.navigator.Models.Map;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.Utils.Logger;
import cn.vicey.navigator.Utils.Tools;

import java.io.File;

/**
 * Map manager, provides a set of methods to help manage map files
 */
public final class MapManager
{
    //region Constants

    private static final String LOGGER_TAG = "MapManager";

    private static final String MAP_DIR = "/maps"; // Map file directory name

    //endregion

    //region Static fields

    private static File mMapDir; // Map file directory

    //endregion

    //region Static methods

    /**
     * Delete specified map file
     *
     * @param mapFileName Map file name to delete
     * @return Whether the deletion is succeed or not
     */
    public static boolean deleteMapFile(final @NonNull String mapFileName)
    {
        File map = new File(mMapDir + "/" + mapFileName);
        return !map.exists() || map.delete();
    }

    /**
     * Gets available default map file name
     *
     * @return Available default map file name
     */
    public static String getAvailableDefaultMapFileName()
    {
        return Tools.getAvailableDefaultName(mMapDir, ".xml");
    }

    /**
     * Gets map directory
     *
     * @return Map directory
     */
    public static File getMapDir()
    {
        return mMapDir;
    }

    /**
     * Check specified map file is exist or not
     *
     * @param mapFileName Name of the map file to check
     * @return Whether specified map is exist
     */
    public static boolean hasMapFile(final @NonNull String mapFileName)
    {
        return new File(mMapDir + "/" + mapFileName).exists();
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
            mMapDir = new File(Navigator.getFilesDirPath() + MAP_DIR);
            if (!(mMapDir.exists() || mMapDir.mkdir()))
            {
                Logger.error(LOGGER_TAG, "Failed to init map manager. Can not init map directory.");
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
     * Load specified map file
     *
     * @param mapFileName Map file name to load
     * @return Map object, or null if failed to load
     */
    public static Map loadMap(final @NonNull String mapFileName)
    {
        return MapParser.parse(new File(mMapDir + "/" + mapFileName));
    }

    /**
     * Rename specified map file
     *
     * @param mapFileName    Map file to rename
     * @param newMapFileName New file name
     * @return Whether the renaming is succeed or not
     */
    public static boolean renameMapFile(final @NonNull String mapFileName, final @NonNull String newMapFileName)
    {
        if (!hasMapFile(mapFileName)) return false;
        if (hasMapFile(newMapFileName)) return false;
        File map = new File(mMapDir + "/" + mapFileName);
        return map.renameTo(new File(mMapDir + "/" + newMapFileName));
    }

    /**
     * Save specified file to map file directory
     *
     * @param src         Source file
     * @param overwritten Whether the existing file should be overwritten
     * @return Whether the saving is succeed or not
     */
    public static boolean saveMapFile(final @NonNull File src, boolean overwritten)
    {
        String fileName = src.getName();
        if (!overwritten && hasMapFile(fileName)) fileName = getAvailableDefaultMapFileName();
        return Tools.copyFile(src, new File(mMapDir + "/" + fileName), overwritten);
    }

    //endregion

    //region Constructor

    /**
     * Hidden for static class design pattern
     */
    private MapManager()
    {
        // no-op
    }

    //endregion
}
