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
import cn.vicey.navigator.Map.MapManager;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Nodes.GuideNode;
import cn.vicey.navigator.Models.Nodes.NodeBase;
import cn.vicey.navigator.Models.Nodes.NodeType;
import cn.vicey.navigator.Models.Nodes.WallNode;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.Share.Logger;
import cn.vicey.navigator.Share.Settings;

import java.util.List;

public class MapRenderer
        extends View
{
    private static final String LOGGER_TAG = "MapRenderer";
    private static final int ENTRY_COLOR = Color.GREEN;
    private static final int NODE_RADIUS = 10;
    private static final int GUIDE_COLOR = Color.BLUE;
    private static final int GUIDE_WIDTH = 4;
    private static final int WALL_COLOR = Color.DKGRAY;
    private static final int WALL_WIDTH = 10;
    private static final int ZOOM_LEVEL_MAX = 5;
    private static final int ZOOM_LEVEL_MIN = 1;
    private static final int ZOOM_SPEED = 200;

    private Paint mGuidePaint = new Paint();
    private int mHalfWidth;
    private int mHalfHeight;
    private boolean mIsZooming;
    private Point mLookAt = new Point();
    private float mPrevTouchX = 0;
    private float mPrevTouchY = 0;
    private int mTouchedPointCount;
    private float mTouchPointDistance;
    private Paint mWallPaint = new Paint();
    private float mZoomLevel = 3;

    private boolean valid()
    {
        return MapManager.getCurrentFloor() != null;
    }

    private void drawGuideNodes(final @NonNull Canvas canvas, final @NonNull Floor floor)
    {
        List<GuideNode> guideNodes = floor.getGuideNodes();
        for (GuideNode guideNode : guideNodes)
            drawNode(canvas, guideNode);
    }

    private void drawLine(final @NonNull Canvas canvas, final @NonNull NodeBase start, final @NonNull NodeBase end)
    {
        float startX = getRelativeX(start.getX());
        float startY = getRelativeY(start.getY());
        float endX = getRelativeX(end.getX());
        float endY = getRelativeY(end.getY());
        if (start.getType() == NodeType.WALL_NODE && end.getType() == NodeType.WALL_NODE)
            canvas.drawLine(startX, startY, endX, endY, mWallPaint);
        else canvas.drawLine(startX, startY, endX, endY, mGuidePaint);
    }

    private void drawNode(final @NonNull Canvas canvas, final @NonNull NodeBase node)
    {
        float x = getRelativeX(node.getX());
        float y = getRelativeY(node.getY());
        switch (node.getType())
        {
            case GUIDE_NODE:
            {
                canvas.drawCircle(x, y, NODE_RADIUS, mGuidePaint);
                break;
            }
            case WALL_NODE:
            {
                canvas.drawCircle(x, y, WALL_WIDTH / 2, mWallPaint);
                break;
            }
        }
    }

    private void drawWalls(final @NonNull Canvas canvas, final @NonNull Floor floor)
    {
        List<WallNode> wallNodes = floor.getWallNodes();
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

    private void init(AttributeSet attrs)
    {
        try
        {
            mGuidePaint.setColor(GUIDE_COLOR);
            mGuidePaint.setStrokeWidth(GUIDE_WIDTH);

            mWallPaint.setColor(WALL_COLOR);
            mWallPaint.setStrokeWidth(WALL_WIDTH);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init map renderer.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    private void lookAt(int x, int y)
    {
        if (!valid()) return;

        mLookAt.set(x, y);
        invalidate();
    }

    private void moveEye(float xOffset, float yOffset)
    {
        if (!valid()) return;

        int newX = mLookAt.x += xOffset / mZoomLevel;
        int newY = mLookAt.y += yOffset / mZoomLevel;

        Floor floor = MapManager.getCurrentFloor();
        if (newX < 0) newX = 0;
        if (newX > floor.getWidth() * mZoomLevel) newX = (int) (floor.getWidth() * mZoomLevel);
        if (newY < 0) newY = 0;
        if (newY > floor.getHeight() * mZoomLevel) newY = (int) (floor.getHeight() * mZoomLevel);

        lookAt(newX, newY);
    }

    private void zoom(float offset)
    {
        if (!valid()) return;

        mZoomLevel += offset / ZOOM_SPEED;

        if (mZoomLevel < ZOOM_LEVEL_MIN) mZoomLevel = ZOOM_LEVEL_MIN;
        if (mZoomLevel > ZOOM_LEVEL_MAX) mZoomLevel = ZOOM_LEVEL_MAX;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Floor floor = MapManager.getCurrentFloor();
        if (floor == null) return;

        drawWalls(canvas, floor);
        if (Settings.getIsDebugModeEnabled())
        {
            drawGuideNodes(canvas, floor);
        }
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
        this(context, null);
    }

    public MapRenderer(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
        init(attrs);
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
