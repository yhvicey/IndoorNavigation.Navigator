package cn.vicey.navigator.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import cn.vicey.navigator.Activities.MainActivity;
import cn.vicey.navigator.Components.SettingsCheckBox;
import cn.vicey.navigator.Debug.DebugManager;
import cn.vicey.navigator.Debug.DebugPath;
import cn.vicey.navigator.Debug.FakeLocateManager;
import cn.vicey.navigator.File.DebugPathParser;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.AlertManager;
import cn.vicey.navigator.Share.SettingsManager;
import cn.vicey.navigator.Utils.Logger;
import cn.vicey.navigator.Utils.Tools;

/**
 * Settings view, provides a view to manage setting items
 */
public class SettingsView
        extends ScrollView
{
    //region Constants

    private static final String LOGGER_TAG = "SettingsView";

    //endregion

    //region Listeners

    private final OnClickListener                        mOnEditDebugPathTextViewClick             = new OnClickListener()                        // Listener for edit debug path text view click event
    {
        @Override
        public void onClick(View view)
        {
            final EditText editor = new EditText(getContext());
            editor.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            editor.setSingleLine(false);

            DebugPath currentDebugPath = FakeLocateManager.getDebugPath();
            if (currentDebugPath != null) editor.setText(currentDebugPath.toString());

            new AlertDialog.Builder(getContext()).setView(editor)
                                                 .setTitle(R.string.edit_debug_path)
                                                 .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
                                                 {
                                                     @Override
                                                     public void onClick(DialogInterface dialogInterface, int i)
                                                     {
                                                         FakeLocateManager.setDebugPath(DebugPathParser.parse(editor.getText()
                                                                                                                    .toString()
                                                                                                                    .split(Tools.NEW_LINE)));
                                                     }
                                                 })
                                                 .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                                                 {
                                                     @Override
                                                     public void onClick(DialogInterface dialogInterface, int i)
                                                     {
                                                         dialogInterface.dismiss();
                                                     }
                                                 })
                                                 .show();
        }
    };
    private final OnClickListener                        mOnDisableDebugModeTextViewClick          = new OnClickListener()                        // Listener for disable debug mode text view click event
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
    private final OnClickListener                        mOnShowLogTextViewClick                   = new OnClickListener()                        // Listener for show log text view click event
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
    private final OnClickListener                        mOnStartEmulatingTextViewClick            = new OnClickListener()                        // Listener for start emulating text view click event
    {
        @Override
        public void onClick(View view)
        {
            try
            {
                if (view.getId() != R.id.sv_debug_start_emulating) return;
                if (!DebugManager.isUseFakeLocationEnabled()) return;
                if (DebugManager.isUseRandomLocationEnabled()) return;
                DebugPath debugPath = FakeLocateManager.getDebugPath();
                if (debugPath == null)
                {
                    AlertManager.alert(R.string.no_debug_path);
                    return;
                }
                debugPath.start();
                mParent.switchView(MainActivity.VIEW_NAVIGATE);
            }
            catch (Throwable t)
            {
                Logger.error(LOGGER_TAG, "Failed to start emulating.", t);
            }
        }
    };
    private final OnClickListener                        mOnStopEmulatingTextViewClick             = new OnClickListener()                        // Listener for stop emulating text view click event
    {
        @Override
        public void onClick(View view)
        {
            try
            {
                if (view.getId() != R.id.sv_debug_stop_emulating) return;
                if (!DebugManager.isUseFakeLocationEnabled()) return;
                if (DebugManager.isUseRandomLocationEnabled()) return;
                DebugPath debugPath = FakeLocateManager.getDebugPath();
                if (debugPath == null)
                {
                    AlertManager.alert(R.string.no_debug_path);
                    return;
                }
                debugPath.stop();
                mParent.switchView(MainActivity.VIEW_NAVIGATE);
            }
            catch (Throwable t)
            {
                Logger.error(LOGGER_TAG, "Failed to stop emulating.", t);
            }
        }
    };
    private final CompoundButton.OnCheckedChangeListener mOnTrackPathCheckedChangeListener         = new CompoundButton.OnCheckedChangeListener() // Listener for check box checked change event
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b)
        {
            DebugManager.setTrackPathEnabled(b);
        }
    };
    private final CompoundButton.OnCheckedChangeListener mOnUseDebugPathCheckedChangeListener      = new CompoundButton.OnCheckedChangeListener() // Listener for check box checked change event
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b)
        {
            mUseRandomLocationCheckBox.setChecked(!b);
        }
    };
    private final CompoundButton.OnCheckedChangeListener mOnUseFakeLocationCheckedChangeListener   = new CompoundButton.OnCheckedChangeListener() // Listener for check box checked change event
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b)
        {
            DebugManager.setUseFakeLocationEnabled(b);
            if (b) mFakeLocationPanel.setVisibility(View.VISIBLE);
            else mFakeLocationPanel.setVisibility(View.GONE);
        }
    };
    private final CompoundButton.OnCheckedChangeListener mOnUseRandomLocationCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() // Listener for check box checked change event
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b)
        {
            DebugManager.setUseRandomLocationEnabled(b);
            mUseDebugPathCheckBox.setChecked(!b);
            if (b) mDebugPathPanel.setVisibility(View.GONE);
            else mDebugPathPanel.setVisibility(View.VISIBLE);
        }
    };

    //endregion

    //region Fields

    private LinearLayout     mDebugPathPanel;            // Debug path panel
    private LinearLayout     mFakeLocationPanel;         // Fake location panel
    private MainActivity     mParent;                    // Parent activity
    private SettingsCheckBox mTrackPathCheckBox;         // Track path check box
    private SettingsCheckBox mUseDebugPathCheckBox;      // Use debug path check box
    private SettingsCheckBox mUseFakeLocationCheckBox;   // Use fake location check box
    private SettingsCheckBox mUseRandomLocationCheckBox; // Use Random location check box

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

            // mUseFakeLocationCheckBox
            mUseFakeLocationCheckBox = (SettingsCheckBox) findViewById(R.id.sv_debug_use_fake_location);
            mUseFakeLocationCheckBox.setOnCheckedChangeListener(mOnUseFakeLocationCheckedChangeListener);

            // mFakeLocationPanel
            mFakeLocationPanel = (LinearLayout) findViewById(R.id.sv_debug_fake_location_panel);

            // mUseRandomLocationCheckBox
            mUseRandomLocationCheckBox = (SettingsCheckBox) findViewById(R.id.sv_debug_use_random_location);
            mUseRandomLocationCheckBox.setOnCheckedChangeListener(mOnUseRandomLocationCheckedChangeListener);

            // mUseDebugPathCheckBox
            mUseDebugPathCheckBox = (SettingsCheckBox) findViewById(R.id.sv_debug_use_debug_path);
            mUseDebugPathCheckBox.setOnCheckedChangeListener(mOnUseDebugPathCheckedChangeListener);

            // mDebugPathPanel
            mDebugPathPanel = (LinearLayout) findViewById(R.id.sv_debug_debug_path_panel);

            // editDebugPathTextView
            TextView editDebugPathTextView = (TextView) findViewById(R.id.sv_debug_edit_debug_path);
            editDebugPathTextView.setOnClickListener(mOnEditDebugPathTextViewClick);

            // startEmulatingTextView
            TextView startEmulatingTextView = (TextView) findViewById(R.id.sv_debug_start_emulating);
            startEmulatingTextView.setOnClickListener(mOnStartEmulatingTextViewClick);

            // stopEmulatingTextView
            TextView stopEmulatingTextView = (TextView) findViewById(R.id.sv_debug_stop_emulating);
            stopEmulatingTextView.setOnClickListener(mOnStopEmulatingTextViewClick);

            // mTrackPathCheckBox
            mTrackPathCheckBox = (SettingsCheckBox) findViewById(R.id.sv_debug_track_path);
            mTrackPathCheckBox.setOnCheckedChangeListener(mOnTrackPathCheckedChangeListener);

            // showLogTextView
            View showLogTextView = findViewById(R.id.sv_debug_show_log);
            showLogTextView.setOnClickListener(mOnShowLogTextViewClick);

            // disableDebugModeTextView
            View disableDebugModeTextView = findViewById(R.id.sv_debug_disable_debug_mode);
            disableDebugModeTextView.setOnClickListener(mOnDisableDebugModeTextViewClick);
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
        mUseFakeLocationCheckBox.setChecked(DebugManager.isUseFakeLocationEnabled());
    }

    //endregion
}
