package cn.vicey.navigator.Contracts;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import cn.vicey.navigator.Contracts.Nodes.EntryNode;
import cn.vicey.navigator.Contracts.Nodes.GuideNode;
import cn.vicey.navigator.Contracts.Nodes.NodeBase;
import cn.vicey.navigator.Contracts.Nodes.WallNode;
import cn.vicey.navigator.Share.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Floor class.
 */
public class Floor
{
    private HashMap<Integer, EntryNode> mEntryNodes;
    private List<GuideNode> mGuideNodes;
    private List<WallNode> mWallNodes;

    /**
     * Initialize new instance of class Floor.
     *
     * @param nodes Nodes of the floor.
     * @param links Links of the floor.
     */
    @SuppressLint("UseSparseArrays")
    public Floor(@NonNull List<NodeBase> nodes, @NonNull List<Link> links)
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
                if (mEntryNodes == null) mEntryNodes = new HashMap<>();
                mEntryNodes.put(((EntryNode) node).getId(), (EntryNode) node);
            }
            else if (node instanceof GuideNode)
            {
                if (mGuideNodes == null) mGuideNodes = new ArrayList<>();
                mGuideNodes.add((GuideNode) node);
            }
            else if (node instanceof WallNode)
            {
                if (mWallNodes == null) mWallNodes = new ArrayList<>();
                mWallNodes.add((WallNode) node);
            }
        }
    }

    public EntryNode findEntryNode(int id)
    {
        return mEntryNodes.get(id);
    }

    public List<GuideNode> findGuideNode(String pattern)
    {
        if (Utils.isStringEmpty(pattern, true)) return null;
        List<GuideNode> result = new ArrayList<>();
        for (GuideNode node : mGuideNodes)
        {
            if (node.getName() == null) continue;
            if (pattern.matches(node.getName())) result.add(node);
        }
        return result;
    }

    public NodeBase findNode(double x, double y)
    {
        for (NodeBase node : mEntryNodes.values())
        {
            if (Utils.isDoubleEqual(x, node.getX()) && Utils.isDoubleEqual(y, node.getY())) return node;
        }
        for (NodeBase node : mGuideNodes)
        {
            if (Utils.isDoubleEqual(x, node.getX()) && Utils.isDoubleEqual(y, node.getY())) return node;
        }
        for (NodeBase node : mWallNodes)
        {
            if (Utils.isDoubleEqual(x, node.getX()) && Utils.isDoubleEqual(y, node.getY())) return node;
        }
        return null;
    }

    public List<EntryNode> getEntryNodes()
    {
        return new ArrayList<>(mEntryNodes.values());
    }

    public List<GuideNode> getGuideNodes()
    {
        return mGuideNodes;
    }

    public List<WallNode> getWallNodes()
    {
        return mWallNodes;
    }
}
