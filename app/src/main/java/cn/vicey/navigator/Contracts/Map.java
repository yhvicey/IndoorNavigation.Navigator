package cn.vicey.navigator.Contracts;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Map class.
 */
public class Map
{
    private List<Floor> mFloors;
    private String mName;

    /**
     * Initialize new instance of class Map.
     *
     * @param floors Map's floors.
     */
    public Map(@NonNull String name, @NonNull List<Floor> floors)
    {
        mFloors = floors;
        mName = name;
    }

    public Floor getFloor(int floor)
    {
        return mFloors.get(floor);
    }

    public int getFloorCount()
    {
        return mFloors.size();
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

    public String getName()
    {
        return mName;
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

    public void setName(@NonNull String value)
    {
        mName = value;
    }
}
