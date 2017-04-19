package cn.vicey.navigator.Views;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.vicey.navigator.Activities.MainActivity;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.Logger;
import cn.vicey.navigator.Share.Settings;

public class SettingsView
        extends ScrollView
{
    private static final String LOGGER_TAG = "SettingsView";

    private MainActivity mParent;

    private OnClickListener mOnShowLogTextViewClick = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() != R.id.sv_debug_show_log) return;
            mParent.switchView(MainActivity.VIEW_LOG);
        }
    };

    private void init()
    {
        try
        {
            LayoutInflater.from(mParent).inflate(R.layout.view_settings, this, true);

            TextView showLogTextView = (TextView) findViewById(R.id.sv_debug_show_log);
            showLogTextView.setOnClickListener(mOnShowLogTextViewClick);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init settings view.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }

    }

    public SettingsView(final @NonNull MainActivity parent)
    {
        super(parent);
        mParent = parent;
        init();
    }

    public void flush()
    {
        mParent.setTitleText(R.string.settings);
        View debugView = findViewById(R.id.sv_debug_view);
        debugView.setVisibility(Settings.getIsDebugModeEnabled() ? View.VISIBLE : View.INVISIBLE);
    }
}
