package cn.vicey.navigator.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
        }

        public void clear()
        {
            mItems.clear();
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
    private static final long RIPPLE_DURATION = 250;

    //endregion

    //region Variables

    private Map mCurrentMap = null;
    private int mCurrentView = VIEW_NAVIGATE;
    private boolean mIsMenuOpened = false;

    //endregion

    //region UI variables

    private GuillotineAnimation mGuillotineAnimation;
    private LinearLayout mMainMenu;
    private List<MenuItem> mMenuItems;
    private cn.vicey.navigator.Components.Toolbar mToolbar;
    private ViewFlipper mViewFlipper;
    private MapView mNavigateView;
    private ListView mMapsView;
    private ListViewAdapter<String> mMapsViewAdapter;
    private LinearLayout mTagsView;
    private LinearLayout mSettingsView;

    //endregion

    //region Callback variables

    private View.OnClickListener mDefaultOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {

        }
    };

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
                            final EditText editor = new EditText(MainActivity.this);
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
                                            String newMapName = editor.getText().toString();
                                            if (MapManager.renameMap(mapName, newMapName))
                                                alert(getString(R.string.rename_succeed));
                                            else alert(getString(R.string.rename_failed));
                                            flushMapsView();
                                        }
                                    }
                                }
                            };
                            new AlertDialog.Builder(MainActivity.this).setTitle(R.string.new_file)
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

    private View.OnClickListener mMenuItemOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view instanceof MenuItem)
            {
                flushMainMenu();
                ((MenuItem) view).setHighlighted(true);
            }
            switch (view.getId())
            {
                case R.id.menu_navigate:
                {
                    mCurrentView = VIEW_NAVIGATE;
                    break;
                }
                case R.id.menu_maps:
                {
                    mCurrentView = VIEW_MAPS;
                    break;
                }
                case R.id.menu_tags:
                {
                    mCurrentView = VIEW_TAGS;
                    break;
                }
                case R.id.menu_settings:
                {
                    mCurrentView = VIEW_SETTINGS;
                    break;
                }
            }
            flushViews();
            mGuillotineAnimation.close();
        }
    };

    //endregion

    //region Initialize functions

    private void initViews()
    {
        try
        {
            mMainMenu = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.menu_main, null);
            mMapsView = (ListView) findViewById(R.id.maps_view);
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

    private void initMainMenu()
    {
        try
        {
            mMainMenu.setOnClickListener(mDefaultOnClickListener);
            mMenuItems = new ArrayList<>();
            int count = mMainMenu.getChildCount();
            for (int i = 0; i < count; i++)
            {
                View childView = mMainMenu.getChildAt(i);
                if (!(childView instanceof MenuItem)) continue;
                childView.setOnClickListener(mMenuItemOnClickListener);
                mMenuItems.add((MenuItem) childView);
            }
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
            mMapsViewAdapter = new ListViewAdapter<>(this, new ArrayList<String>());
            mMapsView.setOnItemClickListener(mMapsViewOnItemClickListener);
            mMapsView.setAdapter(mMapsViewAdapter);
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

    private void flushMainMenu()
    {
        for (MenuItem item : mMenuItems)
        {
            item.setHighlighted(false);
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
        mMapsViewAdapter.clear();
        List<String> maps = MapManager.getAllMaps();
        for (String map : maps)
        {
            mMapsViewAdapter.addItem(map);
        }
        mMapsViewAdapter.notifyDataSetChanged();
    }

    private void flushTagsView()
    {
        mToolbar.setTitleText(R.string.tags);
    }

    private void flushSettingsView()
    {
        mToolbar.setTitleText(R.string.settings);
    }

    private void flushViews()
    {
        if (mCurrentView >= mViewFlipper.getChildCount()) mCurrentView = VIEW_NAVIGATE;
        if (mViewFlipper.getDisplayedChild() != mCurrentView) mViewFlipper.setDisplayedChild(mCurrentView);
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
        }
    }

    //endregion

    //region Functions

    private void switchView(int viewIndex)
    {
        mCurrentView = viewIndex;
        flushViews();
    }

    private void alert(@NonNull String message)
    {
        alert(message, Toast.LENGTH_SHORT);
    }

    private void alert(@NonNull String message, int duration)
    {
        Toast.makeText(this, message, duration).show();
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
            initMainMenu();
            initMapsView();
            initNavigateView();
            initTagsView();
            initToolbar();
            initSettingsView();
            initViewFlipper();


            if (!MapManager.hasMap("iot_b.inmap"))
            {
                MapManager.downloadMap("http://www.vicey.cn/iot_b.inmap", "iot_b.inmap", new Utils.DownloadCallback()
                {
                    @Override
                    public void onDownloadFinished(boolean succeed)
                    {
                        if (!succeed) return;
                        mCurrentMap = MapManager.loadMap("iot_b.inmap");
                    }
                });
            }
            else
            {
                mCurrentMap = MapManager.loadMap("iot_b.inmap");
            }
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
            Logger.info(LOGGER_TAG, "Exit application.");
            Logger.flush();
            super.onBackPressed();
        }
    }

    //endregion
}
