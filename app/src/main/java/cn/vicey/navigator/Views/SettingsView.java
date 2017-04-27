package cn.vicey.navigator.Views;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import cn.vicey.navigator.Activities.MainActivity;
import cn.vicey.navigator.Components.SettingsCheckBox;
import cn.vicey.navigator.Managers.AlertManager;
import cn.vicey.navigator.Managers.DebugManager;
import cn.vicey.navigator.Managers.SettingsManager;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Utils.Logger;

/**
 * Settings view, provides a view to manage setting items
 */
public class SettingsView
        extends ScrollView
{
    //region Constants

    private static final String LOGGER_TAG = "SettingsView";

    //endregion

    //region Fields

    private MainActivity     mParent;                  // Parent activity
    private SettingsCheckBox mTrackPathCheckBox;       // Track path check box
    private SettingsCheckBox mUseFakeLocationCheckBox; // Use fake location check box

    //endregion

    //region Listeners

    private OnClickListener                        mOnDisableDebugModeTextViewClick        = new OnClickListener()                        // Listener for disable debug mode text view click event
    {
        @Override
        public void onClick(View view)
        {
            try
            {
                SettingsManager.setDebugModeEnabled(false);
                AlertManager.alert(R.string.debug_mode_disabled);
                flush();
            }
            catch (Throwable t)
            {
                Logger.error(LOGGER_TAG, "Failed to disable debug mode.", t);
            }
        }
    };
    private OnClickListener                        mOnShowLogTextViewClick                 = new OnClickListener()                        // Listener for show log text view click event
    {
        @Override
        public void onClick(View view)
        {
            try
            {
                if (view.getId() != R.id.sv_debug_show_log) return;
                mParent.switchView(MainActivity.VIEW_LOG);
            }
            catch (Throwable t)
            {
                Logger.error(LOGGER_TAG, "Failed to show log.", t);
            }
        }
    };
    private CompoundButton.OnCheckedChangeListener mOnTrackPathCheckedChangeListener       = new CompoundButton.OnCheckedChangeListener() // Listener for check box checked change event
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b)
        {
            DebugManager.setTrackPathEnabled(b);
        }
    };
    private CompoundButton.OnCheckedChangeListener mOnUseFakeLocationCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() // Listener for check box checked change event
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b)
        {
            DebugManager.setUseFakeLocation(b);
        }
    };

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link SettingsView}
     *
     * @param parent Parent activity
     */
    public SettingsView(final @NonNull MainActivity parent)
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
            LayoutInflater.from(mParent).inflate(R.layout.view_settings, this, true);

            // disableDebugModeTextView
            View disableDebugModeTextView = findViewById(R.id.sv_debug_disable_debug_mode);
            disableDebugModeTextView.setOnClickListener(mOnDisableDebugModeTextViewClick);

            // showLogTextView
            View showLogTextView = findViewById(R.id.sv_debug_show_log);
            showLogTextView.setOnClickListener(mOnShowLogTextViewClick);

            // mTrackPathCheckBox
            mTrackPathCheckBox = (SettingsCheckBox) findViewById(R.id.sv_debug_track_path);
            mTrackPathCheckBox.setOnCheckedChangeListener(mOnTrackPathCheckedChangeListener);

            // mUseFakeLocationCheckBox
            mUseFakeLocationCheckBox = (SettingsCheckBox) findViewById(R.id.sv_debug_use_fake_location);
            mUseFakeLocationCheckBox.setOnCheckedChangeListener(mOnUseFakeLocationCheckedChangeListener);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init settings view.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }

    }

    /**
     * Flush view
     */
    public void flush()
    {
        mParent.setTitleText(R.string.settings);

        // debugView
        View debugView = findViewById(R.id.sv_debug_view);
        debugView.setVisibility(SettingsManager.isDebugModeEnabled() ? View.VISIBLE : View.GONE);

        // mTrackPathCheckBox
        mTrackPathCheckBox.setChecked(DebugManager.isTrackPathEnabled());

        // mUseFakeLocationCheckBox
        mUseFakeLocationCheckBox.setChecked(DebugManager.isUseFakeLocation());
    }

    //endregion
}
