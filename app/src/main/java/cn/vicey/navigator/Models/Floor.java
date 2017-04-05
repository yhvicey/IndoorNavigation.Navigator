package cn.vicey.navigator.Models;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Nodes.*;
import cn.vicey.navigator.Share.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Floor class.
 */
public class Floor
{
    private List<EntryNode> mEntryNodes = new ArrayList<>();
    private List<GuideNode> mGuideNodes = new ArrayList<>();
    private List<WallNode> mWallNodes = new ArrayList<>();

    /**
     * Initialize new instance of class Floor.
     *
     * @param nodes Nodes of the floor.
     * @param links Links of the floor.
     */
    public Floor(final @NonNull List<NodeBase> nodes, final @NonNull List<Link> links)
    {
        for (Link link : links)
        {
            NodeBase start = nodes.get(link.getStart());
            NodeBase target = nodes.get(link.getEnd());
            start.addAdjacentNode(target);
        }
        for (NodeBase node : nodes)
        {
            if (node instanceof EntryNode)
            {
                mEntryNodes.add((EntryNode) node);
            }
            else if (node instanceof GuideNode)
            {
                mGuideNodes.add((GuideNode) node);
            }
            else if (node instanceof WallNode)
            {
                mWallNodes.add((WallNode) node);
            }
        }
    }

    public void clearTags()
    {
        for (NodeBase node : mEntryNodes)
        {
            node.clearTag();
        }
        for (NodeBase node : mGuideNodes)
        {
            node.clearTag();
        }
        for (NodeBase node : mWallNodes)
        {
            node.clearTag();
        }
    }

    public List<EntryNode> findEntryNode(final @NonNull String pattern)
    {
        if (Utils.isStringEmpty(pattern, true)) return new ArrayList<>();
        List<EntryNode> result = new ArrayList<>();
        for (EntryNode node : mEntryNodes)
        {
            if (node.getName() == null) continue;
            if (pattern.matches(node.getName())) result.add(node);
        }
        return result;
    }

    public List<GuideNode> findGuideNode(final @NonNull String pattern)
    {
        if (Utils.isStringEmpty(pattern, true)) return new ArrayList<>();
        List<GuideNode> result = new ArrayList<>();
        for (GuideNode node : mGuideNodes)
        {
            if (node.getName() == null) continue;
            if (pattern.matches(node.getName())) result.add(node);
        }
        return result;
    }

    public List<NodeBase> findNode(final @NonNull String pattern)
    {
        List<NodeBase> result = new ArrayList<>();
        result.addAll(findEntryNode(pattern));
        result.addAll(findGuideNode(pattern));
        return result;
    }

    public List<EntryNode> getEntryNodes()
    {
        return mEntryNodes;
    }

    public List<GuideNode> getGuideNodes()
    {
        return mGuideNodes;
    }

    public List<Tag> getTags(int floor)
    {
        List<Tag> tags = new ArrayList<>();
        int index = 0;
        for (NodeBase node : mEntryNodes)
        {
            String tagValue = node.getTag();
            if (tagValue != null) tags.add(new Tag(floor, index, NodeType.ENTRY_NODE, tagValue));
            index++;
        }
        for (NodeBase node : mGuideNodes)
        {
            String tagValue = node.getTag();
            if (tagValue != null) tags.add(new Tag(floor, index, NodeType.GUIDE_NODE, tagValue));
            index++;
        }
        for (NodeBase node : mWallNodes)
        {
            String tagValue = node.getTag();
            if (tagValue != null) tags.add(new Tag(floor, index, NodeType.WALL_NODE, tagValue));
            index++;
        }
        return tags;
    }

    public List<WallNode> getWallNodes()
    {
        return mWallNodes;
    }
}
