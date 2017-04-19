package cn.vicey.navigator.Views;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import cn.vicey.navigator.Activities.MainActivity;
import cn.vicey.navigator.Components.MapRenderer;
import cn.vicey.navigator.Managers.MapManager;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Utils.Logger;

public class NavigateView
        extends RelativeLayout
{
    private static final String LOGGER_TAG = "NavigateView";
    private static final int MAP_RENDERER_INDEX = 0;
    private static final int PLACEHOLDER_INDEX = 1;

    private MapRenderer mMapRenderer;
    private MainActivity mParent;
    private ViewFlipper mViewFlipper;

    private OnClickListener mUpstairsButtonOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() != R.id.nv_upstairs_button) return;
            if (MapManager.getCurrentMap() == null) mParent.alert(R.string.no_loaded_map);
            else if (!MapManager.goUpstairs()) mParent.alert(R.string.already_top_floor);
            flush();
        }
    };
    private OnClickListener mDownstairsButtonOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() != R.id.nv_downstairs_button) return;
            if (MapManager.getCurrentMap() == null) mParent.alert(R.string.no_loaded_map);
            else if (!MapManager.goDownstairs()) mParent.alert(R.string.already_ground_floor);
            flush();
        }
    };

    private void init()
    {
        try
        {
            LayoutInflater.from(mParent).inflate(R.layout.view_navigate, this, true);

            mMapRenderer = new MapRenderer(mParent);

            FloatingActionButton upstairsButton = (FloatingActionButton) findViewById(R.id.nv_upstairs_button);
            upstairsButton.setOnClickListener(mUpstairsButtonOnClickListener);

            FloatingActionButton downStairsButton = (FloatingActionButton) findViewById(R.id.nv_downstairs_button);
            downStairsButton.setOnClickListener(mDownstairsButtonOnClickListener);

            View placeholder = mParent.getLayoutInflater().inflate(R.layout.cmpt_placeholder, null);

            mViewFlipper = (ViewFlipper) findViewById(R.id.nv_view_flipper);
            mViewFlipper.addView(mMapRenderer);
            mViewFlipper.addView(placeholder);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init navigate view.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    private void showPlaceholder()
    {
        mViewFlipper.setDisplayedChild(PLACEHOLDER_INDEX);
    }

    private void showRenderer()
    {
        mViewFlipper.setDisplayedChild(MAP_RENDERER_INDEX);
    }

    public NavigateView(final @NonNull MainActivity parent)
    {
        super(parent);
        mParent = parent;
        init();
    }

    public void flush()
    {
        if (MapManager.getCurrentFloorIndex() == MapManager.NO_SELECTED_FLOOR)
        {
            mParent.setTitleText(R.string.navigate);
            if (mViewFlipper.getDisplayedChild() != PLACEHOLDER_INDEX) showPlaceholder();
        }
        else
        {
            String titleText = MapManager.getCurrentMap().getName();
            int floorIndex = MapManager.getCurrentFloorIndex();
            if (floorIndex != MapManager.NO_SELECTED_FLOOR) titleText = titleText + " - " + (floorIndex + 1) + "F";
            mParent.setTitleText(titleText);
            if (mViewFlipper.getDisplayedChild() != MAP_RENDERER_INDEX) showRenderer();
        }
        mMapRenderer.invalidate();
    }
}
