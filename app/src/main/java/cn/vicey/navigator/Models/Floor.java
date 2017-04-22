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
 * Floor class
 */
public class Floor
{
    //region Constants

    private static final String LOGGER_TAG = "Floor";

    private static final int MAP_PADDING = 50; // Map's right and bottom padding for displaying the whole area

    //endregion

    //region Fields

    private int mHeight; // Floor's height
    private int mWidth;  // Floor's width

    private List<GuideNode> mGuideNodes = new ArrayList<>(); // Floor's guide nodes
    private List<WallNode>  mWallNodes  = new ArrayList<>(); // Floor's wall nodes

    //endregion

    //region Accessors

    /**
     * Gets node's guide nodes
     *
     * @return Node's guide nodes
     */
    public List<GuideNode> getGuideNodes()
    {
        return mGuideNodes;
    }

    /**
     * Gets floor's height
     *
     * @return Floor's height
     */
    public int getHeight()
    {
        return mHeight;
    }

    /**
     * Gets floor's wall nodes
     *
     * @return Floor's wall nodes
     */
    public List<WallNode> getWallNodes()
    {
        return mWallNodes;
    }

    /**
     * Gets floor's width
     *
     * @return Floor's width
     */
    public int getWidth()
    {
        return mWidth;
    }

    //endregion

    // region Methods

    /**
     * Add and convert link to {@link NodeBase.Link}
     *
     * @param link Link to add
     */
    public void addLink(final @NonNull Link link)
    {
        NodeBase start = getNode(link.getStartType(), link.getStartIndex());
        NodeBase end = getNode(link.getEndType(), link.getEndIndex());
        start.link(end);
        end.link(start);
    }

    /**
     * Add and convert links to {@link NodeBase.Link}
     *
     * @param links Links to add
     */
    public void addLinks(final @NonNull List<Link> links)
    {
        for (Link link : links)
        {
            addLink(link);
        }
    }

    /**
     * Add node to floor
     *
     * @param node Node to add
     */
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

    /**
     * Clear all tags
     */
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

    /**
     * Find guide nodes by pattern
     *
     * @param pattern Search pattern
     * @return Found nodes
     */
    public List<GuideNode> findGuideNodes(final @NonNull String pattern)
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

    /**
     * Gets guide node by index
     *
     * @param index Guide node index
     * @return Specified guide node
     */
    public GuideNode getGuideNode(int index)
    {
        return mGuideNodes.get(index);
    }

    /**
     * Gets guide node's index
     *
     * @param node Specified guide node
     * @return Specified guide node's index
     */
    public int getGuideNodeIndex(@NonNull GuideNode node)
    {
        return mGuideNodes.indexOf(node);
    }

    /**
     * Gets node by type and index
     *
     * @param type  Node type
     * @param index Node index
     * @return Specified node
     */
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

    /**
     * Gets node's index
     *
     * @param node Specified node
     * @return Specified node's index
     */
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

    /**
     * Gets all tags
     *
     * @param floorIndex Current floor index
     * @return Tags
     */
    public List<Tag> getTags(int floorIndex)
    {
        List<Tag> tags = new ArrayList<>();
        int index = 0;
        for (NodeBase node : mGuideNodes)
        {
            String tagValue = node.getTag();
            if (tagValue != null) tags.add(new Tag(floorIndex, index, NodeType.GUIDE_NODE, tagValue));
            index++;
        }
        for (NodeBase node : mWallNodes)
        {
            String tagValue = node.getTag();
            if (tagValue != null) tags.add(new Tag(floorIndex, index, NodeType.WALL_NODE, tagValue));
            index++;
        }
        return tags;
    }

    /**
     * Gets wall node by index
     *
     * @param index Wall node's index
     * @return Specified wall node
     */
    public WallNode getWallNode(int index)
    {
        return mWallNodes.get(index);
    }

    /**
     * Gets wall node's index
     *
     * @param node Specified wall node
     * @return Specified wall node's index
     */
    public int getWallNodeIndex(@NonNull WallNode node)
    {
        return mWallNodes.indexOf(node);
    }

    //endregion
}
