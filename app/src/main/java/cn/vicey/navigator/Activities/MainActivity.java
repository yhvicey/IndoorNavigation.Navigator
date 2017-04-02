package cn.vicey.navigator.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.vicey.navigator.Components.MapView;
import cn.vicey.navigator.Components.MenuItem;
import cn.vicey.navigator.Contracts.Map;
import cn.vicey.navigator.Map.MapManager;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.Logger;
import cn.vicey.navigator.Share.Utils;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
{
    //region Helper classes

    private class ListViewAdapter<T>
            extends BaseAdapter
    {
        private LayoutInflater mInflater;
        private List<T> mItems;

        public ListViewAdapter(Context context, List<T> items)
        {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mItems = items;
        }

        public void addItem(T item)
        {
            mItems.add(item);
            notifyDataSetChanged();
        }

        public void clear()
        {
            mItems.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount()
        {
            return mItems.size();
        }

        @Override
        public Object getItem(int i)
        {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.cmpt_list_item, null);
            }
            TextView textView = (TextView) view.findViewById(R.id.li_text_view);
            textView.setText(mItems.get(i).toString());
            return view;
        }
    }

    //endregion

    //region Constants

    private static final String LOGGER_TAG = "MainActivity";
    private static final int VIEW_NAVIGATE = 0;
    private static final int VIEW_MAPS = 1;
    private static final int VIEW_TAGS = 2;
    private static final int VIEW_SETTINGS = 3;
    private static final int VIEW_LOG = 4;
    private static final long SHORT_TOAST_DURATION = 2000;
    private static final long LONG_TOAST_DURATION = 3500;
    private static final long RIPPLE_DURATION = 250;

    //endregion

    //region Variables

    private int mClickCount;
    private Map mCurrentMap;
    private int mCurrentView = VIEW_NAVIGATE;
    private boolean mIsDebugModeEnabled;
    private boolean mIsMenuOpened;
    private Date mLastClickTime;

    //endregion

    //region UI variables

    private GuillotineAnimation mGuillotineAnimation;
    private ScrollView mLogView;
    private LinearLayout mMainMenu;
    private cn.vicey.navigator.Components.Toolbar mToolbar;
    private ViewFlipper mViewFlipper;
    private MapView mNavigateView;
    private RelativeLayout mMapsView;
    private ListViewAdapter<String> mMapsListViewAdapter;
    private LinearLayout mTagsView;
    private LinearLayout mSettingsView;

    //endregion

    //region Callback variables

    private GuillotineListener mGuillotineListener = new GuillotineListener()
    {
        @Override
        public void onGuillotineOpened()
        {
            mIsMenuOpened = true;
        }

        @Override
        public void onGuillotineClosed()
        {
            mIsMenuOpened = false;
        }
    };

    private ListView.OnItemClickListener mMapsViewOnItemClickListener = new ListView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            final TextView textView = view instanceof TextView ? (TextView) view : null;
            new AlertDialog.Builder(MainActivity.this).setTitle(R.string.manage).setItems(new String[]{
                    getString(R.string.load),
                    getString(R.string.rename),
                    getString(R.string.delete)
            }, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                    switch (i)
                    {
                        //region Load
                        case 0:
                        {
                            if (textView == null) return;
                            String mapName = textView.getText().toString();
                            if ((mCurrentMap = MapManager.loadMap(mapName)) != null)
                            {
                                alert(getString(R.string.load_succeed));
                                switchView(VIEW_NAVIGATE);
                            }
                            else alert(getString(R.string.load_failed));
                            break;
                        }
                        //endregion
                        //region Rename
                        case 1:
                        {
                            if (textView == null) return;
                            final String mapName = textView.getText().toString();
                            final EditText editor = new EditText(MainActivity.this);
                            editor.setText(mapName);
                            editor.selectAll();
                            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface childDialogInterface, int i)
                                {
                                    childDialogInterface.dismiss();
                                    switch (i)
                                    {
                                        case AlertDialog.BUTTON_POSITIVE:
                                        {
                                            String newMapName = editor.getText().toString();
                                            if (MapManager.renameMap(mapName, newMapName))
                                                alert(getString(R.string.rename_succeed));
                                            else alert(getString(R.string.rename_failed));
                                            flushMapsView();
                                        }
                                    }
                                }
                            };
                            new AlertDialog.Builder(MainActivity.this).setTitle(R.string.rename)
                                                                      .setView(editor)
                                                                      .setPositiveButton(R.string.confirm, listener)
                                                                      .setNegativeButton(R.string.cancel, listener)
                                                                      .show();
                            break;
                        }
                        //endregion
                        //region Delete
                        case 2:
                        {
                            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface childDialogInterface, int i)
                                {
                                    childDialogInterface.dismiss();
                                    switch (i)
                                    {
                                        case AlertDialog.BUTTON_POSITIVE:
                                        {
                                            if (textView == null) return;
                                            String mapName = textView.getText().toString();
                                            if (MapManager.deleteMap(mapName))
                                                alert(getString(R.string.delete_succeed));
                                            else alert(getString(R.string.delete_failed));
                                            flushMapsView();
                                        }
                                    }
                                }
                            };
                            new AlertDialog.Builder(MainActivity.this).setTitle(R.string.alert)
                                                                      .setMessage(R.string.confirm_to_delete)
                                                                      .setPositiveButton(R.string.confirm, listener)
                                                                      .setNegativeButton(R.string.cancel, listener)
                                                                      .show();
                            break;
                        }
                        //endregion
                    }
                }
            }).show();
        }
    };

    //endregion

    //region Callback functions

    public void onClick(View view)
    {

    }

    public void onEnableDebugModeClick(View view)
    {
        if (mIsDebugModeEnabled) return;
        if (view.getId() != R.id.sv_general_header) return;
        if (mLastClickTime == null || new Date().getTime() - mLastClickTime.getTime() > 2 * 1000)
        {
            mLastClickTime = new Date();
            mClickCount = 1;
            return;
        }
        mClickCount++;
        if (mClickCount > 5)
        {
            mIsDebugModeEnabled = true;
            Logger.info(LOGGER_TAG, "Debug mode enabled");
            alert(getString(R.string.debug_mode_enabled));
            flushSettingsView();
        }
        else if (mClickCount > 3)
        {
            alert(String.format(getString(R.string.debug_mode_notification), 5 - mClickCount + 1), 300);
        }
    }

    public void onLoadButtonClick(View view)
    {
        switch (view.getId())
        {
            case R.id.mv_load_from_disk:
            {

                break;
            }
            case R.id.mv_load_from_net:
            {
                final EditText editor = new EditText(MainActivity.this);
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                        switch (i)
                        {
                            case AlertDialog.BUTTON_POSITIVE:
                            {
                                String url = editor.getText().toString();
                                Utils.downloadFile(url, new Utils.DownloadCallback()
                                {
                                    @Override
                                    public void onDownloadSucceed(@NonNull String filePath)
                                    {
                                        if (MapManager.saveMap(new File(filePath), true))
                                        {
                                            alert(getString(R.string.download_succeed));
                                            invoke(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    flushMapsView();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onDownloadFailed()
                                    {
                                        alert(getString(R.string.download_failed));
                                    }
                                });
                            }
                        }
                    }
                };
                new AlertDialog.Builder(MainActivity.this).setTitle(R.string.load_from_net)
                                                          .setView(editor)
                                                          .setPositiveButton(R.string.confirm, listener)
                                                          .setNegativeButton(R.string.cancel, listener)
                                                          .show();
                break;
            }
        }
    }

    public void onMenuItemClick(View view)
    {
        switch (view.getId())
        {
            case R.id.menu_navigate:
            {
                switchView(VIEW_NAVIGATE);
                break;
            }
            case R.id.menu_maps:
            {
                switchView(VIEW_MAPS);
                break;
            }
            case R.id.menu_tags:
            {
                switchView(VIEW_TAGS);
                break;
            }
            case R.id.menu_settings:
            {
                switchView(VIEW_SETTINGS);
                break;
            }
        }
        flushViews();
        mGuillotineAnimation.close();
    }

    public void onShowLogClick(View view)
    {
        if (view.getId() != R.id.sv_debug_show_log) return;
        switchView(VIEW_LOG);
    }

    //endregion

    //region Initialize functions

    private void initViews()
    {
        try
        {
            mLogView = (ScrollView) findViewById(R.id.log_view);
            mMainMenu = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.menu_main, null);
            mMapsView = (RelativeLayout) findViewById(R.id.maps_view);
            mNavigateView = (MapView) findViewById(R.id.navigate_view);
            mTagsView = (LinearLayout) findViewById(R.id.tags_view);
            mToolbar = (cn.vicey.navigator.Components.Toolbar) findViewById(R.id.toolbar);
            mSettingsView = (LinearLayout) findViewById(R.id.settings_view);
            mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize views.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    private void initGuillotineAnimation()
    {
        try
        {
            mGuillotineAnimation = new GuillotineAnimation.GuillotineBuilder(mMainMenu, mMainMenu.findViewById(R.id.t_menu_icon), mToolbar
                    .findViewById(R.id.t_menu_icon)).setStartDelay(RIPPLE_DURATION)
                                                    .setActionBarViewForAnimation(mToolbar)
                                                    .setClosedOnStart(true)
                                                    .setGuillotineListener(mGuillotineListener)
                                                    .build();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize guillotine animation.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    private void initLogView()
    {
        try
        {

        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize log view.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    private void initMainMenu()
    {
        try
        {
            FrameLayout rootView = (FrameLayout) findViewById(R.id.root);
            rootView.addView(mMainMenu);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize main menu.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    private void initMapsView()
    {
        try
        {
            mMapsListViewAdapter = new ListViewAdapter<>(this, new ArrayList<String>());
            ListView mapsListView = (ListView) mMapsView.findViewById(R.id.mv_list_view);
            mapsListView.setOnItemClickListener(mMapsViewOnItemClickListener);
            mapsListView.setAdapter(mMapsListViewAdapter);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize maps view.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    private void initNavigateView()
    {
        try
        {
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize navigate view.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    private void initTagsView()
    {
        try
        {
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize tags view.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    private void initToolbar()
    {
        try
        {
            setSupportActionBar(mToolbar);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize toolbar.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    private void initSettingsView()
    {
        try
        {
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize settings view.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    private void initViewFlipper()
    {
        try
        {
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize view flipper.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    //endregion

    //region Flush functions

    private void flushLogView()
    {
        mToolbar.setTitleText(getString(R.string.log));
        TextView textView = (TextView) mLogView.findViewById(R.id.lv_text_view);
        textView.setText(Logger.getLogContent());
    }

    private void flushMainMenu()
    {
        int count = mMainMenu.getChildCount();
        int index = 0;
        for (int i = 0; i < count; i++)
        {
            View childView = mMainMenu.getChildAt(i);
            if (!(childView instanceof MenuItem)) continue;
            ((MenuItem) childView).setHighlighted(index++ == mCurrentView);
        }
    }

    private void flushNavigateView()
    {
        if (mCurrentMap == null)
        {
            mToolbar.setTitleText(R.string.navigate);
        }
        else
        {
            mToolbar.setTitleText(mCurrentMap.getName());
        }
    }

    private void flushMapsView()
    {
        mToolbar.setTitleText(R.string.maps);
        mMapsListViewAdapter.clear();
        List<String> maps = MapManager.getAllMaps();
        for (String map : maps)
        {
            mMapsListViewAdapter.addItem(map);
        }
    }

    private void flushTagsView()
    {
        mToolbar.setTitleText(R.string.tags);
    }

    private void flushSettingsView()
    {
        mToolbar.setTitleText(R.string.settings);
        View debugView = mSettingsView.findViewById(R.id.sv_debug_view);
        debugView.setVisibility(mIsDebugModeEnabled ? View.VISIBLE : View.INVISIBLE);
    }

    private void flushViews()
    {
        if (mCurrentView >= mViewFlipper.getChildCount()) mCurrentView = VIEW_NAVIGATE;
        if (mViewFlipper.getDisplayedChild() != mCurrentView) mViewFlipper.setDisplayedChild(mCurrentView);
        flushMainMenu();
        switch (mCurrentView)
        {
            case VIEW_NAVIGATE:
            {
                flushNavigateView();
                break;
            }
            case VIEW_MAPS:
            {
                flushMapsView();
                break;
            }
            case VIEW_TAGS:
            {
                flushTagsView();
                break;
            }
            case VIEW_SETTINGS:
            {
                flushSettingsView();
                break;
            }
            case VIEW_LOG:
            {
                flushLogView();
                break;
            }
        }
    }

    //endregion

    //region Functions

    private void alert(final @NonNull String message)
    {
        alert(message, LONG_TOAST_DURATION);
    }

    private void alert(final @NonNull String message, final long duration)
    {
        invoke(new Runnable()
        {
            @Override
            public void run()
            {
                final Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
                toast.show();
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        toast.cancel();
                    }
                }, duration);
            }
        });
    }

    private void invoke(final Runnable runnable)
    {
        runOnUiThread(runnable);
    }

    private void switchView(int viewIndex)
    {
        mCurrentView = viewIndex;
        flushViews();
    }
    //endregion

    //region System event callbacks

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            initViews();

            initGuillotineAnimation();
            initLogView();
            initMainMenu();
            initMapsView();
            initNavigateView();
            initTagsView();
            initToolbar();
            initSettingsView();
            initViewFlipper();

            flushViews();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize main activity.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    @Override
    protected void onDestroy()
    {
        Logger.flush();
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        if (mIsMenuOpened)
        {
            mGuillotineAnimation.close();
        }
        else
        {
            super.onBackPressed();
            Navigator.exit();
        }
    }

    //endregion
}
