package cn.vicey.navigator.Navigate;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Nodes.GuideNode;
import cn.vicey.navigator.Models.Nodes.NodeBase;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Navigator class, provides navigate ability for related floor
 */
public class FloorNavigator
{
    //region Inner classes

    /**
     * Listener which will be invoked when navigation is finished
     */
    public interface OnNavigationFinishedListener
    {
        //region Methods

        /**
         * Invoked when the navigation is finished
         */
        void onFinished();

        //endregion
    }

    /**
     * Navigate table builder
     */
    private class TableBuilder
            implements Runnable
    {
        //region Constants

        private final Object SYNC_LOCK = new Object();

        //endregion

        //region Fields

        private boolean                 mIsBuilding; // Whether the builder is building
        private NodeBase                mStartNode;  // Start node
        private HashMap<NodeBase, Path> mTable;      // Built table

        //endregion

        //region Constructors

        /**
         * Initialize new instance of class {@link TableBuilder}
         *
         * @param startNode Start node
         */
        public TableBuilder(final @NonNull NodeBase startNode)
        {
            mStartNode = startNode;
        }

        //endregion

        //region Methods

        /**
         * Start building the table
         *
         * @return Built table, or null if the building isn't finished yet
         */
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

        //endregion

        //region Override methods

        @Override
        public void run()
        {
            Path path = new Path(mStartNode);
            HashSet<NodeBase> closeTable = new HashSet<>();
            HashMap<NodeBase, Path> table = new HashMap<>();
            // TODO: Finish Dijkstra algorithm here
            mTable = table;
            if (mOnNavigationFinishedListener != null) mOnNavigationFinishedListener.onFinished();
        }

        //endregion
    }

    /**
     * Path builder
     */
    public class PathBuilder
    {
        //region Fields

        GuideNode    mEndNode;      // Related end node
        TableBuilder mTableBuilder; // Related start node's table builder

        //endregion

        //region Constructors

        /**
         * Initialize new instance of class {@link PathBuilder}
         *
         * @param endNode      Related end node
         * @param tableBuilder Related start node's table builder
         */
        public PathBuilder(final @NonNull GuideNode endNode, final @NonNull TableBuilder tableBuilder)
        {
            mEndNode = endNode;
            mTableBuilder = tableBuilder;
        }

        //endregion

        //region Methods

        /**
         * Gets the shortest path from start node to end node in related floor
         *
         * @return The shortest path, or null if the navigate table isn't built yet
         */
        public Path build()
        {
            if (mTableBuilder.mTable == null) return null;
            Path path = mTableBuilder.mTable.get(mEndNode);
            if (path == null) throw new IllegalArgumentException("End node doesn't belong to this navigator.");
            return path.fork();
        }

        //endregion
    }

    //endregion

    //region Fields

    private OnNavigationFinishedListener mOnNavigationFinishedListener; // Listener for navigation finished event

    private HashMap<NodeBase, TableBuilder> mBuilderTable = new HashMap<>(); // TableBuilder table to get built table or start building table

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link FloorNavigator}
     *
     * @param floor Related floor
     */
    public FloorNavigator(final @NonNull Floor floor)
    {
        for (GuideNode guideNode : floor.getGuideNodes()) mBuilderTable.put(guideNode, new TableBuilder(guideNode));
    }

    //endregion

    //region Accessors

    /**
     * Sets {@link OnNavigationFinishedListener} for this navigator
     *
     * @param value Listener to set
     */
    public void setOnNavigationFinishedListener(OnNavigationFinishedListener value)
    {
        mOnNavigationFinishedListener = value;
    }

    //endregion

    //region Methods

    /**
     * Gets path builder for building the shortest path from start node to end node in related floor
     *
     * @param start Start node
     * @param end   End node
     * @return Path builder
     */
    public PathBuilder navigate(final @NonNull GuideNode start, final @NonNull GuideNode end)
    {
        TableBuilder tableBuilder = mBuilderTable.get(start);
        if (tableBuilder == null) throw new IllegalArgumentException("Start node doesn't belong to this navigator.");
        return new PathBuilder(end, tableBuilder);
    }

    //endregion
}
