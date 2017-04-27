package cn.vicey.navigator.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import cn.vicey.navigator.Managers.DebugManager;
import cn.vicey.navigator.Managers.NavigateManager;
import cn.vicey.navigator.Managers.SettingsManager;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Map;
import cn.vicey.navigator.Models.Nodes.*;
import cn.vicey.navigator.Navigate.Path;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.ListViewAdapter;
import cn.vicey.navigator.Utils.Logger;

import java.util.List;

/**
 * Map renderer component, provides support for drawing, scrolling and zooming map
 */
public class MapRenderer
        extends RelativeLayout
{
    //region Constants

    private static final String LOGGER_TAG = "MapRenderer";

    private static final int LINE_WIDTH      = 8;    // Line width
    private static final int MAX_ERROR_COUNT = 3;    // Max error count
    private static final int NODE_RADIUS     = 4;    // Node radius
    private static final int UPDATE_INTERNAL = 1000; // Update internal in milliseconds
    private static final int ZOOM_LEVEL_MAX  = 10;   // Max zoom level
    private static final int ZOOM_LEVEL_MIN  = 1;    // Min zoom level
    private static final int ZOOM_SPEED      = 200;  // Zoom speed

    //endregion

    //region Listeners

    private SearchView.OnQueryTextListener mOnQueryTextListener      = new SearchView.OnQueryTextListener()  // Search view query text listener
    {
        @Override
        public boolean onQueryTextSubmit(String s)
        {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s)
        {
            if (s.isEmpty())
            {
                // No query string, hide list panel
                mSearchResultsAdapter.clear();
            }
            else
            {
                Floor floor = NavigateManager.getFloor(mCurrentDisplayingFloorIndex);
                if (floor == null) return false;
                List<GuideNode> targets = floor.findGuideNodes(s);
                mSearchResultsAdapter.replace(targets);
            }
            return true;
        }
    };
    private ListView.OnItemClickListener   mOnResultClickListener    = new AdapterView.OnItemClickListener() // Search result item click listener
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            GuideNode target = mSearchResultsAdapter.getItem(i);
            if (target == null) return;
            mSearchView.setQuery("", false);
            mSearchView.setIconified(true);
            mSearchResultsAdapter.clear();
            NavigateManager.startNavigate(mCurrentDisplayingFloorIndex, target);
        }
    };
    private View.OnClickListener           mOnSearchBoxClickListener = new View.OnClickListener()            // Search box click event listener
    {
        @Override
        public void onClick(View view)
        {
            ((SearchView) findViewById(R.id.mr_search_view)).setIconified(false);
        }
    };

    //endregion

    //region Fields

    private Paint                      mBackgroundPaint;      // Paint for background
    private int                        mErrorCount;           // Error count
    private Paint                      mGuidePaint;           // Paint for guide nodes and lines
    private int                        mHalfHeight;           // Half of the component height
    private int                        mHalfWidth;            // Half of the component width
    private boolean                    mIsZooming;            // Whether the component is zooming
    private Point                      mLookAt;               // The center point of the view window in map
    private float                      mPrevTouchX;           // Previous touch point x axis
    private float                      mPrevTouchY;           // Previous touch point y axis
    private ListViewAdapter<GuideNode> mSearchResultsAdapter; // Search result list adapter
    private SearchView                 mSearchView;           // Search view
    private float                      mTouchPointDistance;   // Distance between two touch points
    private int                        mTouchedPointCount;    // Current touch point count
    private Paint                      mUserPaint;            // Paint for user node and lines
    private Paint                      mUserPathPaint;        // Paint for user path
    private Paint                      mWallPaint;            // Paint for wall nodes and lines

    private int   mCurrentDisplayingFloorIndex = NavigateManager.NO_SELECTED_FLOOR;     // Current displaying floor's index
    private float mCurrentZoomLevel            = (ZOOM_LEVEL_MAX + ZOOM_LEVEL_MIN) / 2; // Current zoom level

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link MapRenderer}
     *
     * @param context Related context
     */
    public MapRenderer(Context context)
    {
        this(context, null);
    }

    /**
     * Initialize new instance of class {@link MapRenderer}
     *
     * @param context Related context
     * @param attrs   Xml file attributes
     */
    public MapRenderer(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
        init(attrs);
    }

    //endregion

    //region Accessors

    /**
     * Gets current displaying floor index
     *
     * @return Current displaying floor index
     */
    public int getCurrentDisplayingFloorIndex()
    {
        return mCurrentDisplayingFloorIndex;
    }

    public void setCurrentDisplayingFloorIndex(int value)
    {
        Map currentMap = NavigateManager.getCurrentMap();
        if (currentMap == null) return;
        if (value < 0) return;
        if (value > currentMap.getFloors().size() - 1) return;
        mCurrentDisplayingFloorIndex = value;
    }

    //endregion

    //region Methods

    /**
     * Calculate distance between two touch points
     *
     * @param event Touch event
     * @return Distance between two touch points
     */
    private float calcTouchPointDistance(MotionEvent event)
    {
        if (mTouchedPointCount != 2) return mTouchPointDistance;
        float firstX = event.getX(0);
        float firstY = event.getY(0);
        float secondX = event.getX(1);
        float secondY = event.getY(1);
        return (float) Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(firstY - secondY, 2));
    }

    /**
     * Draw a line between two nodes
     *
     * @param canvas Canvas to draw
     * @param paint  Paint to use
     * @param start  Start node
     * @param end    End node
     */
    private void drawLine(final @NonNull Canvas canvas, final @NonNull Paint paint, int width, final @NonNull NodeBase start, final @NonNull NodeBase end)
    {
        paint.setStrokeWidth(width * mCurrentZoomLevel);
        float startX = getRelativeX(start.getX());
        float startY = getRelativeY(start.getY());
        float endX = getRelativeX(end.getX());
        float endY = getRelativeY(end.getY());
        canvas.drawLine(startX, startY, endX, endY, paint);
    }

    /**
     * Draw target floor's links
     *
     * @param canvas Canvas to draw
     * @param floor  Target floor
     */
    private void drawLinks(final @NonNull Canvas canvas, final @NonNull Floor floor)
    {
        for (WallNode wallNode : floor.getWallNodes())
            for (NodeBase.Link link : wallNode.getLinks())
                drawLine(canvas, mWallPaint, LINE_WIDTH, wallNode, link.getTarget());
        if (!SettingsManager.isDebugModeEnabled()) return;
        for (GuideNode guideNode : floor.getGuideNodes())
            for (NodeBase.Link link : guideNode.getLinks())
                drawLine(canvas, mGuidePaint, LINE_WIDTH, guideNode, link.getTarget());
    }

    /**
     * Draw a node
     *
     * @param canvas Canvas to draw
     * @param paint  Paint to use
     * @param node   Target node
     */
    private void drawNode(final @NonNull Canvas canvas, final @NonNull Paint paint, int radius, final @NonNull NodeBase node)
    {
        float x = getRelativeX(node.getX());
        float y = getRelativeY(node.getY());
        canvas.drawCircle(x, y, radius * mCurrentZoomLevel, paint);
    }

    /**
     * Draw target floor's nodes
     *
     * @param canvas Canvas to draw
     * @param floor  Target floor
     */
    private void drawNodes(final @NonNull Canvas canvas, final @NonNull Floor floor)
    {
        for (WallNode wallNode : floor.getWallNodes())
            drawNode(canvas, mWallPaint, NODE_RADIUS, wallNode);
        if (UserNode.getInstance().getCurrentFloorIndex() == mCurrentDisplayingFloorIndex)
            drawNode(canvas, mUserPaint, NODE_RADIUS * 2, UserNode.getInstance());
        if (!SettingsManager.isDebugModeEnabled()) return;
        for (GuideNode guideNode : floor.getGuideNodes())
            drawNode(canvas, mGuidePaint, NODE_RADIUS, guideNode);
    }

    /**
     * Draw a path
     *
     * @param canvas Canvas to draw
     * @param paint  Paint to use
     * @param path   Path to draw
     */
    private void drawPath(final @NonNull Canvas canvas, final @NonNull Paint paint, final @NonNull Path path)
    {
        drawPath(canvas, paint, path, 0, path.getSize() - 1);
    }

    /**
     * Draw a path
     *
     * @param canvas     Canvas to draw
     * @param paint      Paint to use
     * @param path       Path to draw
     * @param startIndex Path's start index for drawing
     * @param endIndex   Path's end index for drawing
     */
    private void drawPath(final @NonNull Canvas canvas, final @NonNull Paint paint, final @NonNull Path path, int startIndex, int endIndex)
    {
        if (startIndex < 0 || endIndex >= path.getSize() || startIndex > endIndex) return;
        PathNode prevNode = null;
        for (; startIndex < endIndex; startIndex++)
        {
            PathNode curNode = path.getNodes().get(startIndex);
            if (prevNode != null) drawLine(canvas, paint, LINE_WIDTH, prevNode, curNode);
            drawNode(canvas, paint, NODE_RADIUS, curNode);
            prevNode = curNode;
        }
    }

    /**
     * Draw current paths
     *
     * @param canvas Canvas to draw
     */
    private void drawPaths(final @NonNull Canvas canvas)
    {
        Path navigatePath = NavigateManager.getCurrentGuidePath();
        if (navigatePath != null) drawPath(canvas, mGuidePaint, navigatePath);
        if (DebugManager.isTrackPathEnabled())
        {
            List<Path> userPaths = NavigateManager.getUserPaths(mCurrentDisplayingFloorIndex);
            if (userPaths != null) for (Path path : userPaths) drawPath(canvas, mUserPathPaint, path);
        }
    }

    /**
     * Gets displaying floor
     *
     * @return Displaying floor, or null if no floor is displaying
     */
    private Floor getDisplayingFloor()
    {
        Floor floor = NavigateManager.getFloor(mCurrentDisplayingFloorIndex);
        if (floor == null) mCurrentDisplayingFloorIndex = NavigateManager.NO_SELECTED_FLOOR;
        return floor;
    }

    /**
     * Convert x axis from floor coordinate to view coordinate
     *
     * @param x X axis in floor coordinate
     * @return X axis in view coordinate
     */
    private float getRelativeX(int x)
    {
        return (x - mLookAt.x) * mCurrentZoomLevel + mHalfWidth;
    }

    /**
     * Convert y axis from floor coordinate to view coordinate
     *
     * @param y Y axis in floor coordinate
     * @return Y axis in view coordinate
     */
    private float getRelativeY(int y)
    {
        return (y - mLookAt.y) * mCurrentZoomLevel + mHalfHeight;
    }

    /**
     * Initialize component
     *
     * @param attrs Xml file attribute
     */
    private void init(AttributeSet attrs)
    {
        try
        {
            // Inflate layout
            LayoutInflater.from(getContext()).inflate(R.layout.cmpt_map_renderer, this, true);

            // searchBox
            RelativeLayout searchBox = (RelativeLayout) findViewById(R.id.mr_search_box);
            searchBox.setOnClickListener(mOnSearchBoxClickListener);

            // mSearchView
            mSearchView = (SearchView) findViewById(R.id.mr_search_view);
            mSearchView.setOnQueryTextListener(mOnQueryTextListener);

            // mSearchResultsListAdapter
            mSearchResultsAdapter = new ListViewAdapter<>(getContext());

            // searchResults
            ListView searchResults = (ListView) findViewById(R.id.mr_search_results);
            searchResults.setAdapter(mSearchResultsAdapter);
            searchResults.setOnItemClickListener(mOnResultClickListener);

            // mBackgroundPaint
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.renderer_background));
            mBackgroundPaint.setStyle(Paint.Style.FILL);

            // mGuidePaint
            mGuidePaint = new Paint();
            mGuidePaint.setColor(ContextCompat.getColor(getContext(), R.color.renderer_guide_color));

            // mWallPaint
            mWallPaint = new Paint();
            mWallPaint.setColor(ContextCompat.getColor(getContext(), R.color.renderer_wall_color));

            // mUserPaint
            mUserPaint = new Paint();
            mUserPaint.setColor(ContextCompat.getColor(getContext(), R.color.renderer_user_color));

            // mUserPathPaint
            mUserPathPaint = new Paint();
            mUserPathPaint.setColor(ContextCompat.getColor(getContext(), R.color.renderer_user_path_color));

            // mLookAt
            mLookAt = new Point();

            NavigateManager.addOnUpdateListener(new NavigateManager.OnUpdateListener()
            {
                @Override
                public void onUpdate()
                {
                    invoke(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            flush();
                        }
                    });
                }
            });

            setWillNotDraw(false);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init map renderer.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    /**
     * Clip the offsets and move "eyes" to specified location
     *
     * @param xOffset X-axis offset
     * @param yOffset Y-axis offset
     */
    private void moveEye(float xOffset, float yOffset)
    {
        Floor floor;
        if ((floor = getDisplayingFloor()) == null) return;

        int newX = mLookAt.x += xOffset / mCurrentZoomLevel;
        int newY = mLookAt.y += yOffset / mCurrentZoomLevel;

        if (newX < 0) newX = 0;
        if (newX > floor.getWidth() * mCurrentZoomLevel) newX = (int) (floor.getWidth() * mCurrentZoomLevel);
        if (newY < 0) newY = 0;
        if (newY > floor.getHeight() * mCurrentZoomLevel) newY = (int) (floor.getHeight() * mCurrentZoomLevel);

        mLookAt.set(newX, newY);
        flush();
    }

    /**
     * Zoom the floor view
     *
     * @param offset Zoom offset
     */
    private void zoom(float offset)
    {
        mCurrentZoomLevel += offset / ZOOM_SPEED;

        if (mCurrentZoomLevel < ZOOM_LEVEL_MIN) mCurrentZoomLevel = ZOOM_LEVEL_MIN;
        if (mCurrentZoomLevel > ZOOM_LEVEL_MAX) mCurrentZoomLevel = ZOOM_LEVEL_MAX;

        flush();
    }

    /**
     * Try display downstairs
     *
     * @return True if didn't reach the ground floor, otherwise false
     */
    public boolean displayDownstairs()
    {
        if (NavigateManager.getCurrentMap() == null) return false;
        if (mCurrentDisplayingFloorIndex <= 0) return false;
        mCurrentDisplayingFloorIndex--;

        flush();
        return true;
    }

    /**
     * Try display upstairs
     *
     * @return True if didn't reach the top floor, otherwise false
     */
    public boolean displayUpstairs()
    {
        if (NavigateManager.getCurrentMap() == null) return false;
        if (mCurrentDisplayingFloorIndex >= NavigateManager.getCurrentMap().getFloors().size() - 1) return false;
        mCurrentDisplayingFloorIndex++;

        flush();
        return true;
    }

    /**
     * Flush view
     */
    public void flush()
    {
        invalidate();
    }

    /**
     * Invoke a method on UI thread
     *
     * @param runnable Method to run on UI thread
     */
    public void invoke(final Runnable runnable)
    {
        post(runnable);
    }

    public void lookAt(int x, int y)
    {
        mLookAt.x = x;
        mLookAt.y = y;
    }

    //endregion

    //region Override methods

    @Override
    protected void onDraw(Canvas canvas)
    {
        try
        {
            canvas.drawPaint(mBackgroundPaint);
            Floor floor = getDisplayingFloor();
            if (floor == null) return;

            drawNodes(canvas, floor);
            drawLinks(canvas, floor);
            drawPaths(canvas);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to handle draw event.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH)
    {
        try
        {
            mHalfWidth = w / 2;
            mHalfHeight = h / 2;
            mLookAt.set(mHalfWidth, mHalfHeight);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to handle size change event.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        try
        {
            switch (event.getActionMasked())
            {
                case MotionEvent.ACTION_DOWN:
                {
                    mTouchedPointCount = 1;
                    mPrevTouchX = event.getX();
                    mPrevTouchY = event.getY();
                    break;
                }
                case MotionEvent.ACTION_UP:
                {
                    mIsZooming = false;
                    mTouchedPointCount = 0;
                    break;
                }
                case MotionEvent.ACTION_POINTER_DOWN:
                {
                    mTouchedPointCount++;
                    mIsZooming = true;
                    mTouchPointDistance = calcTouchPointDistance(event);
                    break;
                }
                case MotionEvent.ACTION_POINTER_UP:
                {
                    mTouchedPointCount--;
                    mPrevTouchX = event.getX();
                    mPrevTouchY = event.getY();
                    break;
                }
                case MotionEvent.ACTION_MOVE:
                {
                    if (mIsZooming)
                    {
                        float newDistance = calcTouchPointDistance(event);
                        float offset = newDistance - mTouchPointDistance;
                        zoom(offset);
                        mTouchPointDistance = newDistance;
                    }
                    else
                    {
                        float xOffset = mPrevTouchX - event.getX();
                        float yOffset = mPrevTouchY - event.getY();
                        moveEye(xOffset, yOffset);
                        mPrevTouchX = event.getX();
                        mPrevTouchY = event.getY();
                    }
                }
            }
            return true;
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to handle touch event.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
            return false;
        }
    }

    //endregion
}
