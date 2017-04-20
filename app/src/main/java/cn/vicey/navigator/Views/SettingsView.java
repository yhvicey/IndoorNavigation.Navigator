package cn.vicey.navigator.Views;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import cn.vicey.navigator.Activities.MainActivity;
import cn.vicey.navigator.Managers.SettingsManager;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Utils.Logger;

public class SettingsView
        extends ScrollView
{
    private static final String LOGGER_TAG = "SettingsView";

    private MainActivity mParent;

    private OnClickListener mOnDisableDebugModeTextViewClick = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            SettingsManager.disableDebugMode();
            mParent.alert(R.string.debug_mode_disabled);
            flush();
        }
    };
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

            View disableDebugModeTextView = findViewById(R.id.sv_debug_disable_debug_mode);
            disableDebugModeTextView.setOnClickListener(mOnDisableDebugModeTextViewClick);

            View showLogTextView = findViewById(R.id.sv_debug_show_log);
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
        debugView.setVisibility(SettingsManager.isDebugModeEnabled() ? View.VISIBLE : View.INVISIBLE);
    }
}
