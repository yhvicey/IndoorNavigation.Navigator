package cn.vicey.navigator.Views;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import cn.vicey.navigator.Activities.MainActivity;
import cn.vicey.navigator.Components.MapRenderer;
import cn.vicey.navigator.Managers.NavigateManager;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Utils.Logger;

/**
 * Navigate view, provides a view to show map and display navigation on it
 */
public class NavigateView
        extends RelativeLayout
{
    //region Constants

    private static final String LOGGER_TAG = "NavigateView";

    private static final int VIEW_MAP_RENDERER = 0; // Map renderer view's index
    private static final int VIEW_PLACEHOLDER  = 1; // Placeholder view's index

    //endregion

    //region Fields

    private MapRenderer  mMapRenderer; // Map renderer
    private MainActivity mParent;      // Parent activity
    private ViewFlipper  mViewFlipper; // View flipper

    //endregion

    //region Listeners

    private OnClickListener mDownstairsButtonOnClickListener = new OnClickListener() // Downstairs button click listener
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() != R.id.nv_downstairs_button) return;
            if (NavigateManager.getCurrentMap() == null) mParent.alert(R.string.no_loaded_map);
            else if (!NavigateManager.goDownstairs()) mParent.alert(R.string.already_ground_floor);
            flush();
        }
    };
    private OnClickListener mUpstairsButtonOnClickListener   = new OnClickListener() // Upstairs button click listener
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() != R.id.nv_upstairs_button) return;
            if (NavigateManager.getCurrentMap() == null) mParent.alert(R.string.no_loaded_map);
            else if (!NavigateManager.goUpstairs()) mParent.alert(R.string.already_top_floor);
            flush();
        }
    };

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link NavigateView}
     *
     * @param parent Parent activity
     */
    public NavigateView(final @NonNull MainActivity parent)
    {
        super(parent);
        mParent = parent;
        init();
    }

    /**
     * Initialize view
     */
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

    /**
     * Switch to placeholder view
     */
    private void showPlaceholder()
    {
        mViewFlipper.setDisplayedChild(VIEW_PLACEHOLDER);
    }

    /**
     * Switch to map renderer view
     */
    private void showRenderer()
    {
        mViewFlipper.setDisplayedChild(VIEW_MAP_RENDERER);
    }

    /**
     * Flush view
     */
    public void flush()
    {
        if (NavigateManager.getCurrentFloorIndex() == NavigateManager.NO_SELECTED_FLOOR)
        {
            mParent.setTitleText(R.string.navigate);
            if (mViewFlipper.getDisplayedChild() != VIEW_PLACEHOLDER) showPlaceholder();
        }
        else
        {
            String titleText = NavigateManager.getCurrentMap().getName();
            int floorIndex = NavigateManager.getCurrentFloorIndex();
            if (floorIndex != NavigateManager.NO_SELECTED_FLOOR) titleText = titleText + " - " + (floorIndex + 1) + "F";
            mParent.setTitleText(titleText);
            if (mViewFlipper.getDisplayedChild() != VIEW_MAP_RENDERER) showRenderer();
        }
        mMapRenderer.invalidate();
    }

    //endregion
}
