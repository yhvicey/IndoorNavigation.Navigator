package cn.vicey.navigator.Navigate;

import android.support.annotation.NonNull;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Nodes.GuideNode;
import cn.vicey.navigator.Models.Nodes.NodeBase;
import cn.vicey.navigator.Utils.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Navigator class, provides navigate ability for related floor
 */
public class FloorNavigator
{
    //region Inner classes

    /**
     * Listener which will be invoked when navigation is finished
     */
    public interface OnBuildFailedListener
    {
        //region Methods

        /**
         * Invoked when the navigation is failed
         */
        void onFailed();

        //endregion
    }

    /**
     * Navigate table builder
     */
    private class TableBuilder
            implements Runnable
    {
        //region Constants

        private static final String LOGGER_TAG = "TableBuilder";

        private static final int MAX_ERROR_COUNT = 3; // Max error count

        //endregion

        //region Fields

        private int       mErrorCount; // Error count
        private GuideNode mStartNode;  // Start node

        //endregion

        //region Constructors

        /**
         * Initialize new instance of class {@link TableBuilder}
         *
         * @param startNode Start node
         */
        public TableBuilder(final @NonNull GuideNode startNode)
        {
            mStartNode = startNode;
        }

        //endregion

        //region Override methods

        @Override
        public void run()
        {
            try
            {
                GuideNode current = mStartNode;

                long startTime = new Date().getTime();
                Logger.info(LOGGER_TAG, "Started building table.");

                // Dijkstra algorithm started
                Path path = new Path(null);

                // Create open table, put all nodes into it
                HashMap<GuideNode, Path> openTable = new HashMap<>();
                for (GuideNode guideNode : mPathTable.get(current).keySet()) openTable.put(guideNode, null);

                // Create close table
                HashMap<GuideNode, Path> closeTable = new HashMap<>();

                // Pick node from open table and update open table
                while (!openTable.isEmpty())
                {
                    // Append current node to path tail
                    path.appendTail(current);
                    // Remove current node from open table
                    openTable.remove(current);
                    // Put current node and path to close table
                    closeTable.put(current, path.fork());
                    // Walk through adjacent nodes, and find next node
                    GuideNode nearestNode = null;
                    double distance = Double.MAX_VALUE;
                    for (NodeBase.Link link : current.getLinks())
                    {
                        // Only support guide node
                        if (!(link.getTarget() instanceof GuideNode)) continue;
                        GuideNode target = (GuideNode) link.getTarget();
                        // Must not in close table
                        if (closeTable.containsKey(target)) continue;
                        // Must in open table
                        if (!openTable.containsKey(target)) continue;
                        // No path, this means the node used to be unreachable, so create a new path for it
                        if (openTable.get(target) == null) openTable.put(target, path.fork().appendTail(target));
                        else
                        {
                            // Calc new distance and compare to old distance
                            double newDistance = path.getLength() + link.getDistance();
                            // New shortest path to target found, update open table
                            if (openTable.get(target).getLength() > newDistance) openTable.put(target, path.fork()
                                                                                                           .appendTail(target));
                        }
                        if (link.getDistance() < distance)
                        {
                            distance = link.getDistance();
                            nearestNode = target;
                        }
                    }
                    // Meet corner, find nearest node in open table
                    if (nearestNode == null && !openTable.isEmpty())
                    {
                        distance = Double.MAX_VALUE;
                        Path newPath = null;
                        for (Map.Entry<GuideNode, Path> entry : openTable.entrySet())
                        {
                            if (entry.getValue() == null) continue;
                            if (entry.getValue().getLength() < distance)
                            {
                                distance = entry.getValue().getLength();
                                nearestNode = entry.getKey();
                                newPath = entry.getValue();
                            }
                        }
                        if (newPath == null)
                        {
                            Logger.error(LOGGER_TAG, "No nearest node found, error occurred.");
                            if (mOnBuildFailedListener != null) mOnBuildFailedListener.onFailed();
                            return;
                        }
                        path = newPath.fork().removeTail();
                    }
                    if (nearestNode == null) break;
                    // Move to nearest node
                    current = nearestNode;
                }
                // Dijkstra algorithm finished

                long totalTime = new Date().getTime() - startTime;
                Logger.info(LOGGER_TAG, "Finished building table. Total time: " + totalTime + " ms.");

                for (Map.Entry<GuideNode, Path> entry : closeTable.entrySet())
                    mPathTable.get(mStartNode).get(entry.getKey()).setPath(entry.getValue());

            }
            catch (Throwable t)
            {
                Logger.error(LOGGER_TAG, "Error occurred when building table. Error count: " + mErrorCount++ + ".", t);
                if (mErrorCount < MAX_ERROR_COUNT)
                {
                    Logger.info(LOGGER_TAG, "Trying to restart update task.");
                    new Thread(this).start();
                }
                else
                    Logger.error(LOGGER_TAG, "Table builder's crash count reaches its limit. Build process will be stopped.");
            }
        }

        //endregion
    }

    /**
     * Path builder
     */
    private class PathBuilder
    {
        //region Fields

        private boolean mBuilding; // Indicates whether the path is building or not
        private Path    mPath;       // Built path

        //endregion

        //region Constructors

        public PathBuilder()
        {

        }

        //endregion

        //region Accessors

        /**
         * Gets built path
         *
         * @return Built path, or null if the build process isn't finished yet
         */
        public Path getPath()
        {
            return mPath;
        }

        /**
         * Gets whether the path is building or not
         *
         * @return Whether the path is building or not
         */
        public boolean isBuilding()
        {
            return mBuilding;
        }

        /**
         * Sets whether the path is building or not
         *
         * @param value Whether the path is building or not
         */
        public void setBuilding(boolean value)
        {
            mBuilding = value;
        }

        /**
         * Sets built path
         *
         * @param value Built path
         */
        public void setPath(final @NonNull Path value)
        {
            mPath = value;
        }

        //endregion
    }

    //endregion

    //region Fields

    private OnBuildFailedListener mOnBuildFailedListener; // Listener for navigation finished event

    private HashMap<GuideNode, HashMap<GuideNode, PathBuilder>> mPathTable = new HashMap<>(); // TableBuilder table to get built table or start building table

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link FloorNavigator}
     *
     * @param floor Related floor
     */
    public FloorNavigator(final @NonNull Floor floor)
    {
        for (GuideNode startNode : floor.getGuideNodes())
        {
            mPathTable.put(startNode, new HashMap<GuideNode, PathBuilder>());
            for (GuideNode endNode : floor.getGuideNodes()) mPathTable.get(startNode).put(endNode, new PathBuilder());
        }
    }

    //endregion

    //region Accessors

    /**
     * Gets path builder from start node to end node
     *
     * @param startNode Start node
     * @param endNode   End node
     * @return Path builder from start node to end node
     */
    private PathBuilder getBuilder(final @NonNull GuideNode startNode, final @NonNull GuideNode endNode)
    {
        return mPathTable.get(startNode).get(endNode);
    }

    /**
     * Sets {@link OnBuildFailedListener} for this navigator
     *
     * @param value Listener to set
     */
    public void setOnBuildFailedListener(OnBuildFailedListener value)
    {
        mOnBuildFailedListener = value;
    }

    //endregion

    //region Methods

    public void buildPathAsync(final @NonNull GuideNode startNode, final @NonNull GuideNode endNode)
    {
        if (getBuilder(startNode, endNode).isBuilding()) return;
        new Thread(new TableBuilder(startNode)).start();
    }

    /**
     * Gets path from start node to end node in related floor
     *
     * @param startNode Start node
     * @param endNode   End node
     * @return Path's fork, or null if the build process isn't finished yet
     */
    public Path getPath(final @NonNull GuideNode startNode, final @NonNull GuideNode endNode)
    {
        PathBuilder builder = mPathTable.get(startNode).get(endNode);
        if (builder == null) return null;
        return builder.getPath();
    }

    //endregion
}
