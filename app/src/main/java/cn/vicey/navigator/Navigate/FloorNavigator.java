package cn.vicey.navigator.Navigate;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Nodes.GuideNode;
import cn.vicey.navigator.Models.Nodes.NodeBase;

import java.util.HashMap;
import java.util.HashSet;

public class FloorNavigator
{
    public interface OnNavigateFinishedCallback
    {
        public void OnFinished();
    }

    private class Builder
            implements Runnable
    {
        private final Object SYNC_LOCK = new Object();

        private boolean mIsBuilding;
        private NodeBase mNode;
        private HashMap<NodeBase, Path> mTable;

        public Builder(final @NonNull NodeBase node)
        {
            mNode = node;
        }

        @Override
        public void run()
        {
            mTable = buildTable(mNode);
            if (mCallback != null) mCallback.OnFinished();
        }

        public HashMap<NodeBase, Path> build()
        {
            // Build already done, return result
            if (mTable != null || mIsBuilding) return mTable;
            synchronized (SYNC_LOCK)
            {
                // The builder isn't building now, double-check for thread-safety
                if (mIsBuilding) return mTable;
                // Launch builder to build table
                new Thread(this).start();
                mIsBuilding = true;
            }
            return mTable;
        }
    }

    private HashMap<NodeBase, Builder> mBuilderTable = new HashMap<>();
    private OnNavigateFinishedCallback mCallback;

    private HashMap<NodeBase, Path> buildTable(final @NonNull NodeBase node)
    {
        Path path = new Path(node);
        HashSet<NodeBase> closeTable = new HashSet<>();
        HashMap<NodeBase, Path> table = new HashMap<>();
        // TODO: Finish Dijkstra algorithm here
        return table;
    }

    public FloorNavigator(final @NonNull Floor floor)
    {
        for (GuideNode guideNode : floor.getGuideNodes()) mBuilderTable.put(guideNode, new Builder(guideNode));
    }

    public void setCallback(OnNavigateFinishedCallback value)
    {
        mCallback = value;
    }

    public Path navigate(final @NonNull NodeBase start, final @NonNull NodeBase end)
    {
        Builder builder = mBuilderTable.get(start);
        if (builder == null) throw new IllegalArgumentException("Start node doesn't belong to this navigator.");
        HashMap<NodeBase, Path> table = builder.build();
        if (table == null) return null;
        Path path = table.get(end);
        if (path == null) throw new IllegalArgumentException("End node doesn't belong to this navigator.");
        return path;
    }
}
