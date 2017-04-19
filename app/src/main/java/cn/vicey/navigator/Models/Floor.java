package cn.vicey.navigator.Models;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Nodes.GuideNode;
import cn.vicey.navigator.Models.Nodes.NodeBase;
import cn.vicey.navigator.Models.Nodes.NodeType;
import cn.vicey.navigator.Models.Nodes.WallNode;
import cn.vicey.navigator.Utils.Logger;
import cn.vicey.navigator.Utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Floor class.
 */
public class Floor
{
    private static final String LOGGER_TAG = "Floor";
    private static final int MAP_PADDING = 50;

    private List<GuideNode> mGuideNodes = new ArrayList<>();
    private int mHeight;
    private List<WallNode> mWallNodes = new ArrayList<>();
    private int mWidth;

    public List<GuideNode> getGuideNodes()
    {
        return mGuideNodes;
    }

    public int getHeight()
    {
        return mHeight;
    }

    public List<WallNode> getWallNodes()
    {
        return mWallNodes;
    }

    public int getWidth()
    {
        return mWidth;
    }

    public void addLink(final @NonNull Link link)
    {
        NodeBase start = getNode(link.getStartType(), link.getStartIndex());
        NodeBase end = getNode(link.getEndType(), link.getEndIndex());
        start.link(end);
        end.link(start);
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
        if (node.getX() > mWidth) mWidth = node.getX() + MAP_PADDING;
        if (node.getY() > mHeight) mHeight = node.getY() + MAP_PADDING;
        switch (node.getType())
        {
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

    public double calcDistance(NodeType startType, int startIndex, NodeType endType, int endIndex)
    {
        return getNode(startType, startIndex).calcDistance(getNode(endType, endIndex));
    }

    public void clearTags()
    {
        for (NodeBase node : mGuideNodes)
        {
            node.clearTag();
        }
        for (NodeBase node : mWallNodes)
        {
            node.clearTag();
        }
    }

    public List<GuideNode> findGuideNode(final @NonNull String pattern)
    {
        if (Tools.isStringEmpty(pattern, true)) return new ArrayList<>();
        List<GuideNode> result = new ArrayList<>();
        for (GuideNode node : mGuideNodes)
        {
            if (node.getName() == null) continue;
            if (pattern.matches(node.getName())) result.add(node);
        }
        return result;
    }

    public GuideNode getGuideNode(int index)
    {
        return mGuideNodes.get(index);
    }

    public int getGuideNodeIndex(@NonNull GuideNode node)
    {
        return mGuideNodes.indexOf(node);
    }

    public NodeBase getNode(NodeType type, int index)
    {
        switch (type)
        {
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

    public List<Tag> getTags(int floor)
    {
        List<Tag> tags = new ArrayList<>();
        int index = 0;
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

    public WallNode getWallNode(int index)
    {
        return mWallNodes.get(index);
    }

    public int getWallNodeIndex(@NonNull WallNode node)
    {
        return mWallNodes.indexOf(node);
    }
}
