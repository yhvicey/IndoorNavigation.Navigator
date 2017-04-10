package cn.vicey.navigator.Models;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Nodes.*;
import cn.vicey.navigator.Share.Logger;
import cn.vicey.navigator.Share.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Floor class.
 */
public class Floor
{
    private static final String LOGGER_TAG = "Floor";

    private List<EntryNode> mEntryNodes = new ArrayList<>();
    private List<GuideNode> mGuideNodes = new ArrayList<>();
    private List<WallNode> mWallNodes = new ArrayList<>();
    private List<Link> mLinks = new ArrayList<>();

    public List<EntryNode> getEntryNodes()
    {
        return mEntryNodes;
    }

    public List<GuideNode> getGuideNodes()
    {
        return mGuideNodes;
    }

    public List<Link> getLinks()
    {
        return mLinks;
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

    public void addLink(final @NonNull Link link)
    {
        link.onAdd(this);
        mLinks.add(link);
    }

    public void addLinks(final @NonNull List<Link> links)
    {
        for (Link link : links)
        {
            addLink(link);
        }
    }

    public void addNode(final @NonNull NodeBase node)
    {
        switch (node.getType())
        {
            case ENTRY_NODE:
            {
                mEntryNodes.add((EntryNode) node);
                return;
            }
            case GUIDE_NODE:
            {
                mGuideNodes.add((GuideNode) node);
                return;
            }
            case WALL_NODE:
            {
                mWallNodes.add((WallNode) node);
                return;
            }
            default:
            {
                Logger.error(LOGGER_TAG, "Unexpected node type.");
            }
        }
    }

    public void addNodes(final @NonNull List<NodeBase> nodes)
    {
        for (NodeBase node : nodes)
        {
            addNode(node);
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

    public double getDistance(NodeType startType, int startIndex, NodeType endType, int endIndex)
    {
        return getNode(startType, startIndex).getDistance(getNode(endType, endIndex));
    }

    public EntryNode getEntryNode(int index)
    {
        return mEntryNodes.get(index);
    }

    public int getEntryNodeIndex(@NonNull EntryNode node)
    {
        return mEntryNodes.indexOf(node);
    }

    public GuideNode getGuideNode(int index)
    {
        return mGuideNodes.get(index);
    }

    public int getGuideNodeIndex(@NonNull GuideNode node)
    {
        return mGuideNodes.indexOf(node);
    }

    public WallNode getWallNode(int index)
    {
        return mWallNodes.get(index);
    }

    public int getWallNodeIndex(@NonNull WallNode node)
    {
        return mWallNodes.indexOf(node);
    }

    public NodeBase getNode(NodeType type, int index)
    {
        switch (type)
        {
            case ENTRY_NODE:
            {
                return getEntryNode(index);
            }
            case GUIDE_NODE:
            {
                return getGuideNode(index);
            }
            case WALL_NODE:
            {
                return getWallNode(index);
            }
            default:
            {
                Logger.error(LOGGER_TAG, "Unexpected node type.");
                return null;
            }
        }
    }

    public int getNodeIndex(NodeBase node)
    {
        switch (node.getType())
        {
            case ENTRY_NODE:
            {
                return getEntryNodeIndex((EntryNode) node);
            }
            case GUIDE_NODE:
            {
                return getGuideNodeIndex((GuideNode) node);
            }
            case WALL_NODE:
            {
                return getWallNodeIndex((WallNode) node);
            }
            default:
            {
                Logger.error(LOGGER_TAG, "Unexpected node type.");
                return -1;
            }
        }
    }

    public Link link(NodeType startType, int startIndex, NodeType endType, int endIndex)
    {
        Link target;
        for (Link link : mLinks)
        {
            if (link.getStartType() == startType && link.getStartIndex() == startIndex && link.getEndType() == endType && link
                    .getEndIndex() == endIndex) return link;
        }
        target = new Link(startType, startIndex, endType, endIndex);
        addLink(target);
        return target;
    }
}
