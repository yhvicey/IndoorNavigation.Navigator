package cn.vicey.navigator.Contracts;

import java.util.List;

/**
 * Map class.
 */
public class Map
{
    private List<Floor> mFloors = null;

    /**
     * Initialize new instance of class Map.
     *
     * @param floors Map's floors.
     */
    public Map(List<Floor> floors)
    {
        mFloors = floors;
    }

    /**
     * Gets map's floors.
     *
     * @return Map's floors.
     */
    public List<Floor> getFloors()
    {
        return mFloors;
    }

    /**
     * Gets map's version.
     *
     * @return Map's version.
     */
    public String getVersion()
    {
        return "1.0";
    }
}
