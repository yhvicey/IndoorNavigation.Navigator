package cn.vicey.navigator.Models;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Utils.Logger;

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

    /**
     * Gets all tags
     *
     * @return Tags
     */
    public List<Tag> getTags()
    {
        List<Tag> tags = new ArrayList<>();
        int index = 0;
        for (Floor floor : mFloors)
        {
            tags.addAll(floor.getTags(index));
            index++;
        }
        return tags;
    }

    /**
     * Sets tags
     *
     * @param tags Tags to set
     * @return Whether the setting is succeed or not
     */
    public boolean setTags(List<Tag> tags)
    {
        clearTags();
        try
        {
            for (Tag tag : tags)
            {
                int floor = tag.getFloorIndex();
                int index = tag.getNodeIndex();
                String value = tag.getValue();
                switch (tag.getNodeType())
                {
                    case GUIDE_NODE:
                    {
                        mFloors.get(floor).getGuideNodes().get(index).setTag(value);
                        continue;
                    }
                    case WALL_NODE:
                    {
                        mFloors.get(floor).getWallNodes().get(index).setTag(value);
                        continue;
                    }
                    default:
                    {
                        Logger.error(LOGGER_TAG, "Unexpected tag type.");
                        return false;
                    }
                }
            }
            return true;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to set tag.", t);
            return false;
        }
    }

    //endregion

    //region Methods

    /**
     * Clear all tags
     */
    public void clearTags()
    {
        for (Floor floor : mFloors)
        {
            floor.clearTags();
        }
    }

    //endregion

    //region Override methods

    @Override
    public String toString()
    {
        return mName;
    }

    //endregion
}
