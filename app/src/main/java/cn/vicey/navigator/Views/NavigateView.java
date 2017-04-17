package cn.vicey.navigator.Views;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import cn.vicey.navigator.Activities.MainActivity;
import cn.vicey.navigator.Components.MapRenderer;
import cn.vicey.navigator.Map.MapManager;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.Logger;

public class NavigateView
        extends RelativeLayout
{
    private static final String LOGGER_TAG = "NavigateView";

    private MapRenderer mMapRenderer;
    private MainActivity mParent;

    private void init()
    {
        try
        {
            LayoutInflater.from(mParent).inflate(R.layout.view_navigate, this, true);

            mMapRenderer = (MapRenderer) findViewById(R.id.nv_map_renderer);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init navigate view.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }

    }

    public NavigateView(final @NonNull MainActivity parent)
    {
        super(parent);
        mParent = parent;
        init();
    }

    public void flush()
    {
        if (MapManager.getCurrentMap() == null)
        {
            mParent.setTitleText(R.string.navigate);
        }
        else
        {
            mParent.setTitleText(MapManager.getCurrentMap().getName());
        }
        mMapRenderer.setCurrentFloorIndex(0);// TODO: Remove it
        mMapRenderer.invalidate();
    }
}
