package cn.vicey.navigator.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Map;
import cn.vicey.navigator.Models.Nodes.EntryNode;
import cn.vicey.navigator.Models.Nodes.NodeBase;
import cn.vicey.navigator.Models.Nodes.NodeType;
import cn.vicey.navigator.Models.Nodes.WallNode;

import java.util.List;

public class MapRenderer
        extends View
{
    private static final String LOGGER_TAG = "MapRenderer";
    private static final int ENTRY_COLOR = Color.GREEN;
    private static final int ENTRY_RADIUS = 10;
    private static final int GUIDE_COLOR = Color.GREEN;
    private static final int GUIDE_WIDTH = 4;
    private static final int NO_SELECTED_FLOOR = -1;
    private static final int WALL_COLOR = Color.DKGRAY;
    private static final int WALL_WIDTH = 4;
    private static final int ZOOM_LEVEL_MAX = 5;
    private static final int ZOOM_LEVEL_MIN = 1;
    private static final int ZOOM_SPEED = 200;

    private int mCurrentFloorIndex = NO_SELECTED_FLOOR;
    private Paint mEntryPaint = new Paint();
    private Paint mGuidePaint = new Paint();
    private int mHalfWidth;
    private int mHalfHeight;
    private boolean mIsZooming;
    private Point mLookAt = new Point();
    private Map mMap;
    private int mTouchedPointCount;
    private float mTouchPointDistance;
    private float mPrevTouchX = 0;
    private float mPrevTouchY = 0;
    private Paint mWallPaint = new Paint();
    private float mZoomLevel = 3;

    private void drawNode(@NonNull Canvas canvas, @NonNull NodeBase node)
    {
        float x = getRelativeX(node.getX());
        float y = getRelativeY(node.getY());
        if (node.getType() == NodeType.WALL_NODE) canvas.drawCircle(x, y, GUIDE_WIDTH / 2, mWallPaint);
        else if (node.getType() == NodeType.ENTRY_NODE) canvas.drawCircle(x, y, ENTRY_RADIUS, mEntryPaint);
        else canvas.drawCircle(x, y, GUIDE_WIDTH / 2, mGuidePaint);
    }

    private void drawEntries(@NonNull Canvas canvas)
    {
        List<EntryNode> entryNodes = mMap.getFloors().get(mCurrentFloorIndex).getEntryNodes();
        for (EntryNode entryNode : entryNodes)
            drawNode(canvas, entryNode);
    }

    private void drawLine(@NonNull Canvas canvas, @NonNull NodeBase start, @NonNull NodeBase end)
    {
        float startX = getRelativeX(start.getX());
        float startY = getRelativeY(start.getY());
        float endX = getRelativeX(end.getX());
        float endY = getRelativeY(end.getY());
        if (start.getType() == NodeType.WALL_NODE && end.getType() == NodeType.WALL_NODE)
            canvas.drawLine(startX, startY, endX, endY, mWallPaint);
        else canvas.drawLine(startX, startY, endX, endY, mGuidePaint);
    }

    private void drawWalls(@NonNull Canvas canvas)
    {
        List<WallNode> wallNodes = mMap.getFloors().get(mCurrentFloorIndex).getWallNodes();
        for (WallNode node : wallNodes)
        {
            drawNode(canvas, node);
            for (NodeBase.Link link : node.getLinks())
            {
                drawLine(canvas, node, link.getTarget());
            }
        }
    }

    private float getRelativeX(int x)
    {
        return (x - mLookAt.x) * mZoomLevel + mHalfWidth;
    }

    private float getRelativeY(int y)
    {
        return (y - mLookAt.y) * mZoomLevel + mHalfHeight;
    }

    private float getTouchPointDistance(MotionEvent event)
    {
        if (mTouchedPointCount != 2) return mTouchPointDistance;
        float firstX = event.getX(0);
        float firstY = event.getY(0);
        float secondX = event.getX(1);
        float secondY = event.getY(1);
        return (float) Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(firstY - secondY, 2));
    }

    private void initialize(AttributeSet attrs, int defStyle)
    {
        mEntryPaint.setColor(ENTRY_COLOR);
        mEntryPaint.setStrokeWidth(ENTRY_RADIUS);
        mGuidePaint.setColor(GUIDE_COLOR);
        mGuidePaint.setStrokeWidth(GUIDE_WIDTH);
        mWallPaint.setColor(WALL_COLOR);
        mWallPaint.setStrokeWidth(WALL_WIDTH);
    }

    private void lookAt(int x, int y)
    {
        mLookAt.set(x, y);
        invalidate();
    }

    private void moveEye(float xOffset, float yOffset)
    {
        if (mMap == null) return;
        if (mCurrentFloorIndex == NO_SELECTED_FLOOR) return;

        mLookAt.x += xOffset / mZoomLevel;
        mLookAt.y += yOffset / mZoomLevel;

        Floor floor = mMap.getFloors().get(mCurrentFloorIndex);
        if (mLookAt.x < 0) mLookAt.x = 0;
        if (mLookAt.x > floor.getWidth() * mZoomLevel) mLookAt.x = (int) (floor.getWidth() * mZoomLevel);
        if (mLookAt.y < 0) mLookAt.y = 0;
        if (mLookAt.y > floor.getHeight() * mZoomLevel) mLookAt.y = (int) (floor.getHeight() * mZoomLevel);

        invalidate();
    }

    private void zoom(float offset)
    {
        if (mMap == null) return;
        if (mCurrentFloorIndex == NO_SELECTED_FLOOR) return;

        mZoomLevel += offset / ZOOM_SPEED;

        if (mZoomLevel < ZOOM_LEVEL_MIN) mZoomLevel = ZOOM_LEVEL_MIN;
        if (mZoomLevel > ZOOM_LEVEL_MAX) mZoomLevel = ZOOM_LEVEL_MAX;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (mMap == null) return;
        if (mCurrentFloorIndex >= mMap.getFloors().size()) mCurrentFloorIndex = NO_SELECTED_FLOOR;
        if (mCurrentFloorIndex == NO_SELECTED_FLOOR) return;
        drawWalls(canvas);
        drawEntries(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH)
    {
        mHalfWidth = w / 2;
        mHalfHeight = h / 2;
        mLookAt.set(mHalfWidth, mHalfHeight);
    }

    public MapRenderer(Context context)
    {
        this(context, null, 0);
    }

    public MapRenderer(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public MapRenderer(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize(attrs, defStyle);
    }

    public int getCurrentFloorIndex()
    {
        return mCurrentFloorIndex;
    }

    public void setCurrentFloorIndex(int currentFloorIndex)
    {
        this.mCurrentFloorIndex = currentFloorIndex;
        lookAt(mMap.getFloors().get(currentFloorIndex).getWidth() / 2, mMap.getFloors()
                                                                           .get(currentFloorIndex)
                                                                           .getHeight() / 2);
    }

    public void setMap(Map mMap)
    {
        this.mMap = mMap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
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
                mTouchPointDistance = getTouchPointDistance(event);
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
                    float newDistance = getTouchPointDistance(event);
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
}
