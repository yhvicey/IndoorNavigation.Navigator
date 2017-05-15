package cn.vicey.navigator.Models;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Map class
 */
public class Map
{
    //region Constants

    private static final String LOGGER_TAG = "Map";

    private static final String MAP_VERSION = "1.1";

    //endregion

    //region Fields

    private String mName; // Map's name

    private List<Floor> mFloors = new ArrayList<>(); // Map's floors

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link Map}
     *
     * @param floors Map's floors
     */
    public Map(@NonNull String name, @NonNull List<Floor> floors)
    {
        mName = name;
        mFloors.addAll(floors);
    }

    //endregion

    //region Accessors

    /**
     * Gets map's floors
     *
     * @return Map's floors
     */
    public List<Floor> getFloors()
    {
        return mFloors;
    }

    /**
     * Gets map's name
     *
     * @return Map's name
     */
    public String getName()
    {
        return mName;
    }

    //endregion

    //region Methods

    //endregion

    //region Override methods

    @Override
    public String toString()
    {
        return mName;
    }

    //endregion
}
