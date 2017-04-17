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

import java.util.Date;

public class SettingsView
        extends ScrollView
{
    private static final String LOGGER_TAG = "SettingsView";

    private int mClickCount;
    private long mLastClickTime;
    private MainActivity mParent;

    private OnClickListener mOnGeneralHeaderClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (Settings.getIsDebugModeEnabled()) return;
            if (view.getId() != R.id.sv_general_header) return;
            if (new Date().getTime() - mLastClickTime > 2 * 1000)
            {
                mLastClickTime = new Date().getTime();
                mClickCount = 1;
                return;
            }
            mClickCount++;
            if (mClickCount > 5)
            {
                Settings.enableDebugMode();
                Logger.info(LOGGER_TAG, "Debug mode enabled");
                mParent.alert(R.string.debug_mode_enabled);
                flush();
            }
            else if (mClickCount > 3)
            {
                mParent.alert(R.string.debug_mode_notification, 5 - mClickCount + 1);
            }
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

            TextView generalHeader = (TextView) findViewById(R.id.sv_general_header);
            generalHeader.setOnClickListener(mOnGeneralHeaderClickListener);

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
