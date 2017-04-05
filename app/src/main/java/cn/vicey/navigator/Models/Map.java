package cn.vicey.navigator.Models;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Share.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Map class.
 */
public class Map
{
    private static final String LOGGER_TAG = "Map";

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

    public void clearTags()
    {
        for (Floor floor : mFloors)
        {
            floor.clearTags();
        }
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
     * Gets map's version.
     *
     * @return Map's version.
     */
    public String getVersion()
    {
        return "1.0";
    }

    public boolean setTags(List<Tag> tags)
    {
        clearTags();
        try
        {
            for (Tag tag : tags)
            {
                int floor = tag.getFloor();
                int index = tag.getIndex();
                String value = tag.getValue();
                switch (tag.getType())
                {
                    case ENTRY_NODE:
                    {
                        getFloor(floor).getEntryNodes().get(index).setTag(value);
                        continue;
                    }
                    case GUIDE_NODE:
                    {
                        getFloor(floor).getGuideNodes().get(index).setTag(value);
                        continue;
                    }
                    case WALL_NODE:
                    {
                        getFloor(floor).getWallNodes().get(index).setTag(value);
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
}
