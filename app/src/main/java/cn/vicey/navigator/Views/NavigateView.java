package cn.vicey.navigator.Views;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import cn.vicey.navigator.Activities.MainActivity;
import cn.vicey.navigator.Components.MapRenderer;
import cn.vicey.navigator.Navigate.NavigateManager;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.AlertManager;
import cn.vicey.navigator.Utils.Logger;

/**
 * Navigate view, provides a view to show map and display navigation on it
 */
public class NavigateView
        extends RelativeLayout
{
    //region Constants

    private static final String LOGGER_TAG = "NavigateView";

    private static final int VIEW_MAP_RENDERER = 1; // Map renderer view's index
    private static final int VIEW_PLACEHOLDER  = 0; // Placeholder view's index

    //endregion

    //region Listeners

    private final OnClickListener mOnDownstairsButtonClickListener = new OnClickListener() // Downstairs button click listener
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() != R.id.nv_downstairs_button) return;
            if (NavigateManager.getCurrentMap() == null) AlertManager.alert(R.string.no_loaded_map);
            else if (!mMapRenderer.displayDownstairs()) AlertManager.alert(R.string.already_ground_floor);
            flush();
        }
    };
    private final OnClickListener mOnLocationButtonClickListener   = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (NavigateManager.getCurrentMap() == null)
            {
                AlertManager.alert(R.string.no_loaded_map);
                return;
            }
            int floorIndex = NavigateManager.getCurrentFloorIndex();
            if (mMapRenderer.getCurrentDisplayingFloorIndex() != floorIndex)
                mMapRenderer.setCurrentDisplayingFloorIndex(floorIndex);
            mMapRenderer.lookAt(NavigateManager.getCurrentLocation().x, NavigateManager.getCurrentLocation().y);
            mMapRenderer.flush();
        }
    };
    private final OnClickListener mOnUpstairsButtonClickListener   = new OnClickListener() // Upstairs button click listener
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() != R.id.nv_upstairs_button) return;
            if (NavigateManager.getCurrentMap() == null) AlertManager.alert(R.string.no_loaded_map);
            else if (!mMapRenderer.displayUpstairs()) AlertManager.alert(R.string.already_top_floor);
            flush();
        }
    };

    //endregion

    //region Fields

    private MapRenderer  mMapRenderer; // Map renderer
    private MainActivity mParent;      // Parent activity
    private ViewFlipper  mViewFlipper; // View flipper

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

    //endregion

    //region Methods

    /**
     * Initialize view
     */
    private void init()
    {
        try
        {
            // Inflate layout
            LayoutInflater.from(mParent).inflate(R.layout.view_navigate, this, true);

            // mMapRenderer
            mMapRenderer = new MapRenderer(mParent);

            // upstairsButton
            FloatingActionButton upstairsButton = (FloatingActionButton) findViewById(R.id.nv_upstairs_button);
            upstairsButton.setOnClickListener(mOnUpstairsButtonClickListener);

            // downstairsButton
            FloatingActionButton downStairsButton = (FloatingActionButton) findViewById(R.id.nv_downstairs_button);
            downStairsButton.setOnClickListener(mOnDownstairsButtonClickListener);

            // locationButton
            FloatingActionButton locationButton = (FloatingActionButton) findViewById(R.id.nv_location_button);
            locationButton.setOnClickListener(mOnLocationButtonClickListener);

            // placeholder
            View placeholder = mParent.getLayoutInflater().inflate(R.layout.cmpt_placeholder, null);

            // mViewFlipper
            mViewFlipper = (ViewFlipper) findViewById(R.id.nv_view_flipper);
            mViewFlipper.addView(placeholder);
            mViewFlipper.addView(mMapRenderer);
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
        if (mViewFlipper.getDisplayedChild() != VIEW_PLACEHOLDER) mViewFlipper.setDisplayedChild(VIEW_PLACEHOLDER);
    }

    /**
     * Switch to map renderer view
     */
    private void showRenderer()
    {
        if (mViewFlipper.getDisplayedChild() != VIEW_MAP_RENDERER) mViewFlipper.setDisplayedChild(VIEW_MAP_RENDERER);
    }

    /**
     * Flush view
     */
    public void flush()
    {
        if (NavigateManager.getCurrentMap() == null)
        {
            mParent.setTitleText(R.string.navigate);
            showPlaceholder();
            return;
        }
        mParent.setTitleText(NavigateManager.getCurrentMap().getName());
        if (mMapRenderer.getCurrentDisplayingFloorIndex() == NavigateManager.NO_SELECTED_FLOOR && !mMapRenderer.displayUpstairs())
        {
            showPlaceholder();
            return;
        }
        int floorIndex = mMapRenderer.getCurrentDisplayingFloorIndex();
        mParent.setTitleText(NavigateManager.getCurrentMap().getName() + " - " + (floorIndex + 1) + "F");
        showRenderer();
        mMapRenderer.invalidate();
    }

    //endregion
}
