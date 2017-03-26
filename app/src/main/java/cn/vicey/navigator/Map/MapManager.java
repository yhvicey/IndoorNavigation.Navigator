package cn.vicey.navigator.Map;

import cn.vicey.navigator.Contracts.Map;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.Share.Logger;

import java.io.File;

public class MapManager
{
    private static final String LOGGER_TAG = "MapManager";
    private static final String MAP_DIR = "/maps/";

    private static String mMapDirFullPath = null;

    static
    {
        initialize();
    }

    private static void initialize()
    {
        try
        {
            if (!Navigator.getInitialized())
            {
                Logger.error(LOGGER_TAG, "Failed to initialize map manager. Navigator hasn't been initialized yet.");
                return;
            }
            mMapDirFullPath = Navigator.getDataDirFullPath() + MAP_DIR;
            File mapDir = new File(mMapDirFullPath);
            if (!mapDir.exists()) mapDir.mkdir();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Can not initialize map directory.");
            t.printStackTrace();
        }
    }

    private MapManager()
    {
        // no-op
    }

    public static Map loadMap(String mapName)
    {
        return MapParser.parse(new File(mMapDirFullPath + mapName));
    }
}
