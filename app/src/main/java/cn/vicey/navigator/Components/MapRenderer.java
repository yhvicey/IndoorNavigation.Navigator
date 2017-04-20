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
import cn.vicey.navigator.Managers.NavigateManager;
import cn.vicey.navigator.Managers.SettingsManager;
import cn.vicey.navigator.Models.Floor;
import cn.vicey.navigator.Models.Nodes.GuideNode;
import cn.vicey.navigator.Models.Nodes.NodeBase;
import cn.vicey.navigator.Models.Nodes.NodeType;
import cn.vicey.navigator.Models.Nodes.WallNode;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.Utils.Logger;

public class MapRenderer
        extends View
{
    private static final String LOGGER_TAG = "MapRenderer";
    private static final int GUIDE_COLOR = Color.GREEN;
    private static final int LINE_WIDTH = 8;
    private static final int NODE_RADIUS = 4;
    private static final int WALL_COLOR = Color.DKGRAY;
    private static final int ZOOM_LEVEL_MAX = 5;
    private static final int ZOOM_LEVEL_MIN = 1;
    private static final int ZOOM_SPEED = 200;

    private Paint mGuidePaint;
    private int mHalfWidth;
    private int mHalfHeight;
    private boolean mIsZooming;
    private Point mLookAt;
    private float mPrevTouchX = 0;
    private float mPrevTouchY = 0;
    private int mTouchedPointCount;
    private float mTouchPointDistance;
    private Paint mWallPaint;
    private float mZoomLevel = 3;

    private void drawLink(final @NonNull Canvas canvas, final @NonNull NodeBase start, final @NonNull NodeBase end)
    {
        mGuidePaint.setStrokeWidth(LINE_WIDTH * mZoomLevel);
        mWallPaint.setStrokeWidth(LINE_WIDTH * mZoomLevel);
        float startX = getRelativeX(start.getX());
        float startY = getRelativeY(start.getY());
        float endX = getRelativeX(end.getX());
        float endY = getRelativeY(end.getY());
        if (start.getType() == NodeType.WALL_NODE && end.getType() == NodeType.WALL_NODE)
            canvas.drawLine(startX, startY, endX, endY, mWallPaint);
        else canvas.drawLine(startX, startY, endX, endY, mGuidePaint);
    }

    private void drawLinks(final @NonNull Canvas canvas, final @NonNull Floor floor)
    {
        for (WallNode wallNode : floor.getWallNodes())
        {
            for (NodeBase.Link link : wallNode.getLinks())
            {
                drawLink(canvas, wallNode, link.getTarget());
            }
        }
        if (!SettingsManager.isDebugModeEnabled()) return;
        for (GuideNode guideNode : floor.getGuideNodes())
        {
            for (NodeBase.Link link : guideNode.getLinks())
            {
                drawLink(canvas, guideNode, link.getTarget());
            }
        }
    }

    private void drawNode(final @NonNull Canvas canvas, final @NonNull NodeBase node)
    {
        float x = getRelativeX(node.getX());
        float y = getRelativeY(node.getY());
        switch (node.getType())
        {
            case GUIDE_NODE:
            {
                canvas.drawCircle(x, y, NODE_RADIUS * mZoomLevel, mGuidePaint);
                break;
            }
            case WALL_NODE:
            {
                canvas.drawCircle(x, y, NODE_RADIUS * mZoomLevel, mWallPaint);
                break;
            }
        }
    }

    private void drawNodes(final @NonNull Canvas canvas, final @NonNull Floor floor)
    {
        for (WallNode wallNode : floor.getWallNodes())
        {
            drawNode(canvas, wallNode);
        }
        if (!SettingsManager.isDebugModeEnabled()) return;
        for (GuideNode guideNode : floor.getGuideNodes())
        {
            drawNode(canvas, guideNode);
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
            mGuidePaint = new Paint();
            mGuidePaint.setColor(GUIDE_COLOR);

            mWallPaint = new Paint();
            mWallPaint.setColor(WALL_COLOR);

            mLookAt = new Point();
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

        Floor floor = NavigateManager.getCurrentFloor();
        if (newX < 0) newX = 0;
        if (newX > floor.getWidth() * mZoomLevel) newX = (int) (floor.getWidth() * mZoomLevel);
        if (newY < 0) newY = 0;
        if (newY > floor.getHeight() * mZoomLevel) newY = (int) (floor.getHeight() * mZoomLevel);

        lookAt(newX, newY);
    }

    private boolean valid()
    {
        return NavigateManager.getCurrentFloor() != null;
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
        Floor floor = NavigateManager.getCurrentFloor();
        if (floor == null) return;

        drawNodes(canvas, floor);
        drawLinks(canvas, floor);
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
