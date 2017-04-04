package cn.vicey.navigator.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.vicey.navigator.Components.MenuItem;
import cn.vicey.navigator.Contracts.Map;
import cn.vicey.navigator.Contracts.TagList;
import cn.vicey.navigator.Map.MapManager;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.Logger;
import cn.vicey.navigator.Share.Utils;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import java.io.File;
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
        protected LayoutInflater mInflater;
        protected List<T> mItems;

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

        public void replace(List<T> items)
        {
            mItems.clear();
            if (items == null) return;
            mItems.addAll(items);
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
            TextView textView = (TextView) view.findViewById(R.id.list_item);
            textView.setText(mItems.get(i).toString());
            return view;
        }
    }

    private class FileListAdapter
            extends ListViewAdapter<File>
    {
        public FileListAdapter(Context context, List<File> items)
        {
            super(context, items);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.cmpt_file_list_item, null);
            }
            File file = mItems.get(i);
            ImageView imageView = (ImageView) view.findViewById(R.id.fli_icon);
            if (imageView != null)
                imageView.setImageDrawable(getDrawable(file.isFile() ? R.drawable.ic_file : R.drawable.ic_folder));
            TextView textView = (TextView) view.findViewById(R.id.fli_text);
            if (textView != null) textView.setText(file.getName());
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
    private static final long SHORT_TOAST_DURATION = 1000;
    private static final long MIDDLE_TOAST_DURATION = 2000;
    private static final long LONG_TOAST_DURATION = 3500;
    private static final long RIPPLE_DURATION = 250;
    private static final int REQ_STORAGE = 1;

    //endregion

    //region Variables

    private int mClickCount;
    private Map mCurrentMap;
    private int mCurrentView = VIEW_NAVIGATE;
    private boolean mIsDebugModeEnabled;
    private boolean mIsMenuOpened;
    private long mLastBackPressedTime;
    private long mLastClickTime;

    //endregion

    //region UI variables

    private DialogInterface mFileChooserDialog;
    private FileListAdapter mFileListAdapter;
    private GuillotineAnimation mGuillotineAnimation;
    private ScrollView mLogView;
    private LinearLayout mMainMenu;
    private FileListAdapter mMapListAdapter;
    private RelativeLayout mMapsView;
    private RelativeLayout mNavigateView;
    private LinearLayout mSettingsView;
    private FileListAdapter mTagListAdapter;
    private RelativeLayout mTagsView;
    private cn.vicey.navigator.Components.Toolbar mToolbar;
    private ViewFlipper mViewFlipper;

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

    private ListView.OnItemClickListener mMapListOnItemClickListener = new ListView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            final TextView textView = (TextView) view.findViewById(R.id.fli_text);
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
                            if ((mCurrentMap = MapManager.loadMapFile(mapName)) != null)
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
                                            if (MapManager.renameMapFile(mapName, newMapName))
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
                                            if (MapManager.deleteMapFile(mapName))
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

    private ListView.OnItemClickListener mTagListOnItemClickListener = new ListView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            final TextView textView = (TextView) view.findViewById(R.id.fli_text);
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
                            if (mCurrentMap == null)
                            {
                                alert("No loaded map");
                                return;
                            }
                            if (textView == null) return;
                            String tagName = textView.getText().toString();
                            final TagList tagList = MapManager.loadTagFile(tagName);
                            if (tagList == null)
                            {
                                alert(getString(R.string.load_failed));
                                return;
                            }
                            if (!mCurrentMap.getName().equals(tagList.getName()))
                            {
                                new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.alert))
                                                                          .setMessage(R.string.unmatch_name)
                                                                          .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener()
                                                                          {
                                                                              @Override
                                                                              public void onClick(DialogInterface dialogInterface, int i)
                                                                              {
                                                                                  if (mCurrentMap.setTags(tagList))
                                                                                  {
                                                                                      alert(getString(R.string.load_succeed));
                                                                                      switchView(VIEW_NAVIGATE);
                                                                                  }
                                                                                  else
                                                                                      alert(getString(R.string.load_failed));
                                                                              }
                                                                          })
                                                                          .setNegativeButton(getString(R.string.cancel), null)
                                                                          .show();
                            }
                            else
                            {
                                if (mCurrentMap.setTags(tagList))
                                {
                                    alert(getString(R.string.load_succeed));
                                    switchView(VIEW_NAVIGATE);
                                }
                                else alert(getString(R.string.load_failed));
                                break;
                            }
                        }
                        //endregion
                        //region Rename
                        case 1:
                        {
                            if (textView == null) return;
                            final String tagFileName = textView.getText().toString();
                            final EditText editor = new EditText(MainActivity.this);
                            editor.setText(tagFileName);
                            editor.selectAll();
                            new AlertDialog.Builder(MainActivity.this).setTitle(R.string.rename)
                                                                      .setView(editor)
                                                                      .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
                                                                      {
                                                                          @Override
                                                                          public void onClick(DialogInterface childDialogInterface, int i)
                                                                          {
                                                                              childDialogInterface.dismiss();
                                                                              switch (i)
                                                                              {
                                                                                  case AlertDialog.BUTTON_POSITIVE:
                                                                                  {
                                                                                      String newTagFileName = editor.getText()
                                                                                                                    .toString();
                                                                                      if (MapManager.renameTagFile(tagFileName, newTagFileName))
                                                                                          alert(getString(R.string.rename_succeed));
                                                                                      else
                                                                                          alert(getString(R.string.rename_failed));
                                                                                      flushTagsView();
                                                                                  }
                                                                              }
                                                                          }
                                                                      })
                                                                      .setNegativeButton(R.string.cancel, null)
                                                                      .show();
                            break;
                        }
                        //endregion
                        //region Delete
                        case 2:
                        {
                            new AlertDialog.Builder(MainActivity.this).setTitle(R.string.alert)
                                                                      .setMessage(R.string.confirm_to_delete)
                                                                      .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
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
                                                                                      String tagFileName = textView.getText()
                                                                                                                   .toString();
                                                                                      if (MapManager.deleteTagFile(tagFileName))
                                                                                          alert(getString(R.string.delete_succeed));
                                                                                      else
                                                                                          alert(getString(R.string.delete_failed));
                                                                                      flushMapsView();
                                                                                  }
                                                                              }
                                                                          }
                                                                      })
                                                                      .setNegativeButton(R.string.cancel, null)
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
        if (new Date().getTime() - mLastClickTime > 2 * 1000)
        {
            mLastClickTime = new Date().getTime();
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

    public void onLoadMapButtonClick(View view)
    {
        switch (view.getId())
        {
            case R.id.mv_load_from_sdcard:
            {
                if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    alert(getString(R.string.no_permission));
                    requestPermission(REQ_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    return;
                }
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    alert(getString(R.string.sdcard_not_found));
                    return;
                }
                final View fileChooser = LayoutInflater.from(this).inflate(R.layout.cmpt_file_chooser, null);
                final TextView fileChooserHeader = (TextView) fileChooser.findViewById(R.id.fc_header);
                final ListView fileChooserFileListView = (ListView) fileChooser.findViewById(R.id.fc_file_list);

                File startDir = Environment.getExternalStorageDirectory();
                List<File> entries = Utils.getEntries(startDir);
                if (entries == null)
                {
                    alert(getString(R.string.cant_open_folder));
                    return;
                }
                fileChooserHeader.setText(startDir.getAbsolutePath());

                if (mFileListAdapter == null) mFileListAdapter = new FileListAdapter(this, entries);
                else mFileListAdapter.replace(entries);
                fileChooserFileListView.setAdapter(mFileListAdapter);
                fileChooserFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        File currentDir = new File(fileChooserHeader.getText().toString());
                        TextView textView = (TextView) view.findViewById(R.id.fli_text);
                        String currentEntryName = textView.getText().toString();
                        File nextEntry = currentEntryName.equals("..") ? currentDir.getParentFile() : new File(currentDir + "/" + currentEntryName);
                        if (nextEntry.isDirectory())
                        {
                            List<File> newEntries = Utils.getEntries(nextEntry);
                            if (newEntries == null)
                            {
                                alert(getString(R.string.cant_open_folder));
                                return;
                            }
                            mFileListAdapter.replace(newEntries);
                            fileChooserHeader.setText(nextEntry.getAbsolutePath());
                        }
                        else if (nextEntry.isFile())
                        {
                            if (MapManager.saveMapFile(nextEntry, true))
                            {
                                alert(getString(R.string.load_succeed));
                                flushMapsView();
                            }
                            else alert(getString(R.string.load_failed));
                            if (mFileChooserDialog != null)
                            {
                                mFileChooserDialog.dismiss();
                                mFileChooserDialog = null;
                            }
                        }
                    }
                });
                mFileChooserDialog = new AlertDialog.Builder(MainActivity.this).setTitle(R.string.load_from_sdcard)
                                                                               .setView(fileChooser)
                                                                               .show();
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
                                alert(getString(R.string.downloading));
                                Utils.downloadFile(url, new Utils.DownloadCallback()
                                {
                                    @Override
                                    public void onDownloadSucceed(@NonNull String filePath)
                                    {
                                        if (MapManager.saveMapFile(new File(filePath), true))
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

    public void onLoadTagButtonClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_load_from_sdcard:
            {
                if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    alert(getString(R.string.no_permission));
                    requestPermission(REQ_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    return;
                }
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    alert(getString(R.string.sdcard_not_found));
                    return;
                }
                final View fileChooser = LayoutInflater.from(this).inflate(R.layout.cmpt_file_chooser, null);
                final TextView fileChooserHeader = (TextView) fileChooser.findViewById(R.id.fc_header);
                final ListView fileChooserFileListView = (ListView) fileChooser.findViewById(R.id.fc_file_list);

                File startDir = Environment.getExternalStorageDirectory();
                List<File> entries = Utils.getEntries(startDir);
                if (entries == null)
                {
                    alert(getString(R.string.cant_open_folder));
                    return;
                }
                fileChooserHeader.setText(startDir.getAbsolutePath());

                if (mFileListAdapter == null) mFileListAdapter = new FileListAdapter(this, entries);
                else mFileListAdapter.replace(entries);
                fileChooserFileListView.setAdapter(mFileListAdapter);
                fileChooserFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        File currentDir = new File(fileChooserHeader.getText().toString());
                        TextView textView = (TextView) view.findViewById(R.id.fli_text);
                        String currentEntryName = textView.getText().toString();
                        File nextEntry = currentEntryName.equals("..") ? currentDir.getParentFile() : new File(currentDir + "/" + currentEntryName);
                        if (nextEntry.isDirectory())
                        {
                            List<File> newEntries = Utils.getEntries(nextEntry);
                            if (newEntries == null)
                            {
                                alert(getString(R.string.cant_open_folder));
                                return;
                            }
                            mFileListAdapter.replace(newEntries);
                            fileChooserHeader.setText(nextEntry.getAbsolutePath());
                        }
                        else if (nextEntry.isFile())
                        {
                            if (MapManager.saveTagFile(nextEntry, true))
                            {
                                alert(getString(R.string.load_succeed));
                                flushTagsView();
                            }
                            else alert(getString(R.string.load_failed));
                            if (mFileChooserDialog != null)
                            {
                                mFileChooserDialog.dismiss();
                                mFileChooserDialog = null;
                            }
                        }
                    }
                });
                mFileChooserDialog = new AlertDialog.Builder(MainActivity.this).setTitle(R.string.load_from_sdcard)
                                                                               .setView(fileChooser)
                                                                               .show();
                break;
            }
            case R.id.tv_load_from_net:
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
                                alert(getString(R.string.downloading));
                                Utils.downloadFile(url, new Utils.DownloadCallback()
                                {
                                    @Override
                                    public void onDownloadSucceed(@NonNull String filePath)
                                    {
                                        if (MapManager.saveTagFile(new File(filePath), true))
                                        {
                                            alert(getString(R.string.download_succeed));
                                            invoke(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    flushTagsView();
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
            case R.id.mm_navigate:
            {
                switchView(VIEW_NAVIGATE);
                break;
            }
            case R.id.mm_maps:
            {
                switchView(VIEW_MAPS);
                break;
            }
            case R.id.mm_tags:
            {
                switchView(VIEW_TAGS);
                break;
            }
            case R.id.mm_settings:
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
            mMainMenu = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.cmpt_main_menu, null);
            mMapsView = (RelativeLayout) findViewById(R.id.maps_view);
            mNavigateView = (RelativeLayout) findViewById(R.id.navigate_view);
            mSettingsView = (LinearLayout) findViewById(R.id.settings_view);
            mTagsView = (RelativeLayout) findViewById(R.id.tags_view);
            mToolbar = (cn.vicey.navigator.Components.Toolbar) findViewById(R.id.toolbar);
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
            mMapListAdapter = new FileListAdapter(this, new ArrayList<File>());
            ListView mapList = (ListView) mMapsView.findViewById(R.id.mv_list_view);
            mapList.setOnItemClickListener(mMapListOnItemClickListener);
            mapList.setAdapter(mMapListAdapter);
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
            mTagListAdapter = new FileListAdapter(this, new ArrayList<File>());
            ListView tagList = (ListView) mTagsView.findViewById(R.id.tv_list_view);
            tagList.setOnItemClickListener(mTagListOnItemClickListener);
            tagList.setAdapter(mTagListAdapter);
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
        List<File> maps = MapManager.getAllMapFiles();
        mMapListAdapter.replace(maps);
    }

    private void flushTagsView()
    {
        mToolbar.setTitleText(R.string.tags);
        List<File> tags = MapManager.getAllTagFiles();
        mTagListAdapter.replace(tags);
    }

    private void flushSettingsView()
    {
        mToolbar.setTitleText(R.string.settings);
        View debugView = mSettingsView.findViewById(R.id.sv_debug_view);
        debugView.setVisibility(mIsDebugModeEnabled ? View.VISIBLE : View.INVISIBLE);
    }

    //endregion

    //region Functions

    private void alert(final @NonNull String message)
    {
        alert(message, MIDDLE_TOAST_DURATION);
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

    private boolean hasPermission(final @NonNull String permission)
    {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
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

    private void requestPermission(int requestCode, String permission)
    {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    //endregion

    //region System event callbacks

    @Override
    public void onBackPressed()
    {
        if (mIsMenuOpened)
        {
            mGuillotineAnimation.close();
        }
        else
        {
            long current = new Date().getTime();
            if (current - mLastBackPressedTime > SHORT_TOAST_DURATION)
            {
                alert(getString(R.string.exit_notification), SHORT_TOAST_DURATION);
                mLastBackPressedTime = current;
            }
            else
            {
                super.onBackPressed();
                Navigator.exit();
            }
        }
    }

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQ_STORAGE:
            {
                if (grantResults.length != 0)
                {
                    int index = 0;
                    for (String permission : permissions)
                    {
                        if (permission.equals(Manifest.permission_group.STORAGE))
                        {
                            if (grantResults[index] == PackageManager.PERMISSION_GRANTED) return;
                            else break;
                        }
                        index++;
                    }
                }
            }
        }
    }

    //endregion
}
