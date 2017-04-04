package cn.vicey.navigator.Contracts;

import android.support.annotation.NonNull;

import java.util.List;

public class TagList
{
    private String mName;
    private List<Tag> mTagList;

    public TagList(String name, @NonNull List<Tag> tagList)
    {
        mName = name;
        mTagList = tagList;
    }

    public String getName()
    {
        return mName;
    }

    public List<Tag> getTagList()
    {
        return mTagList;
    }
}
