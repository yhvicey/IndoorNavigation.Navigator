package cn.vicey.navigator.Map;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Contracts.Map;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.Share.Logger;
import cn.vicey.navigator.Share.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapManager
{
    private static final String LOGGER_TAG = "MapManager";
    private static final String MAP_DIR = "/maps/";

    private static String mMapDirFullPath = null;

    public static boolean initialize()
    {
        try
        {
            mMapDirFullPath = Navigator.getFilesDirPath() + MAP_DIR;
            File mapDir = new File(mMapDirFullPath);
            if (!(mapDir.exists() || mapDir.mkdir()))
            {
                Logger.error(LOGGER_TAG, "Failed to initialize map manager. Can not initialize map directory.");
                return false;
            }
            return true;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize map manager. Can not initialize map directory.", t);
            return false;
        }
    }

    private MapManager()
    {
        // no-op
    }

    public static boolean deleteMap(@NonNull String mapName)
    {
        File map = new File(mMapDirFullPath + mapName);
        return !map.exists() || map.delete();
    }

    public static void downloadMap(@NonNull String urlString, @NonNull String mapName, @NonNull Utils.DownloadCallback callback)
    {
        Utils.downloadFile(urlString, mMapDirFullPath + mapName, callback);
    }

    public static boolean hasMap(@NonNull String mapName)
    {
        return new File(mMapDirFullPath + mapName).exists();
    }

    public static Map loadMap(@NonNull String mapName)
    {
        return MapParser.parse(new File(mMapDirFullPath + mapName));
    }

    public static boolean renameMap(@NonNull String mapName, @NonNull String newMapName)
    {
        if (!hasMap(mapName)) return false;
        if (hasMap(newMapName)) return false;
        File map = new File(mMapDirFullPath + mapName);
        return map.renameTo(new File(mMapDirFullPath + newMapName));
    }

    public static List<String> getAllMaps()
    {
        File mapDir = new File(mMapDirFullPath);
        List<String> maps = new ArrayList<>();
        for (File file : mapDir.listFiles())
        {
            if (file.isFile()) maps.add(file.getName());
        }
        return maps;
    }
}
