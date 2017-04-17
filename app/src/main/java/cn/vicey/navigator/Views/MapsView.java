package cn.vicey.navigator.Views;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.vicey.navigator.Activities.MainActivity;
import cn.vicey.navigator.Map.MapManager;
import cn.vicey.navigator.Models.Map;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.ListViewAdapter;
import cn.vicey.navigator.Share.Logger;
import cn.vicey.navigator.Share.Utils;

import java.io.File;
import java.util.List;

public class MapsView
        extends RelativeLayout
{
    private class FileListAdapter
            extends ListViewAdapter<File>
    {
        public FileListAdapter(Context context)
        {
            super(context);
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
                imageView.setImageDrawable(getContext().getDrawable(file.isFile() ? R.drawable.ic_file : R.drawable.ic_folder));
            TextView textView = (TextView) view.findViewById(R.id.fli_text);
            if (textView != null) textView.setText(file.getName());
            return view;
        }
    }

    private static final String LOGGER_TAG = "MapsView";

    private DialogInterface mFileChooserDialog;
    private FileListAdapter mFileListAdapter;
    private MainActivity mParent;
    private FileListAdapter mMapListAdapter;

    private OnClickListener mOnLoadFromNetButtonClick = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() != R.id.mv_load_from_net) return;
            //region Load from net
            final EditText editor = new EditText(mParent);
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
                            mParent.alert(R.string.downloading);
                            Utils.downloadFile(url, new Utils.DownloadCallback()
                            {
                                @Override
                                public void onDownloadSucceed(@NonNull String filePath)
                                {
                                    if (MapManager.saveMapFile(new File(filePath), true))
                                    {
                                        mParent.alert(R.string.download_succeed);
                                        mParent.invoke(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                flush();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onDownloadFailed()
                                {
                                    mParent.alert(R.string.download_failed);
                                }
                            });
                        }
                    }
                }
            };
            new AlertDialog.Builder(mParent).setTitle(R.string.load_from_net)
                                            .setView(editor)
                                            .setPositiveButton(R.string.confirm, listener)
                                            .setNegativeButton(R.string.cancel, listener)
                                            .show();
            //endregion
        }
    };
    private OnClickListener mOnLoadFromSdcardButtonClick = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() != R.id.mv_load_from_sdcard) return;
            //region Load from sdcard
            if (!mParent.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                mParent.alert(R.string.no_permission);
                mParent.requestPermission(MainActivity.REQ_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                return;
            }
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            {
                mParent.alert(R.string.sdcard_not_found);
                return;
            }
            final View fileChooser = LayoutInflater.from(mParent).inflate(R.layout.cmpt_file_chooser, null);
            final TextView fileChooserHeader = (TextView) fileChooser.findViewById(R.id.fc_header);
            final ListView fileChooserFileListView = (ListView) fileChooser.findViewById(R.id.fc_file_list);

            File startDir = Environment.getExternalStorageDirectory();
            List<File> entries = Utils.getEntries(startDir);
            if (entries == null)
            {
                mParent.alert(R.string.cant_open_folder);
                return;
            }
            fileChooserHeader.setText(startDir.getAbsolutePath());

            if (mFileListAdapter == null)
            {
                mFileListAdapter = new FileListAdapter(mParent);
                mFileListAdapter.replace(entries);
            }
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
                            mParent.alert(R.string.cant_open_folder);
                            return;
                        }
                        mFileListAdapter.replace(newEntries);
                        fileChooserHeader.setText(nextEntry.getAbsolutePath());
                    }
                    else if (nextEntry.isFile())
                    {
                        if (MapManager.saveMapFile(nextEntry, true))
                        {
                            mParent.alert(R.string.load_succeed);
                            flush();
                        }
                        else mParent.alert(R.string.load_failed);
                        if (mFileChooserDialog != null)
                        {
                            mFileChooserDialog.dismiss();
                            mFileChooserDialog = null;
                        }
                    }
                }
            });
            mFileChooserDialog = new AlertDialog.Builder(mParent).setTitle(R.string.load_from_sdcard)
                                                                 .setView(fileChooser)
                                                                 .show();
            //endregion
        }
    };
    private ListView.OnItemClickListener mOnMapListClickListener = new ListView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            final TextView textView = (TextView) view.findViewById(R.id.fli_text);
            new AlertDialog.Builder(mParent).setTitle(R.string.manage).setItems(new String[]{
                    mParent.getString(R.string.load),
                    mParent.getString(R.string.rename),
                    mParent.getString(R.string.delete)
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
                            Map map;
                            if ((map = MapManager.loadMapFile(mapName)) != null)
                            {
                                MapManager.setCurrentMap(map);
                                mParent.alert(R.string.load_succeed);
                                mParent.switchView(MainActivity.VIEW_NAVIGATE);
                            }
                            else mParent.alert(R.string.load_failed);
                            break;
                        }
                        //endregion
                        //region Rename
                        case 1:
                        {
                            if (textView == null) return;
                            final String mapName = textView.getText().toString();
                            final EditText editor = new EditText(mParent);
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
                                                mParent.alert(R.string.rename_succeed);
                                            else mParent.alert(R.string.rename_failed);
                                            flush();
                                        }
                                    }
                                }
                            };
                            new AlertDialog.Builder(mParent).setTitle(R.string.rename)
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
                                                mParent.alert(R.string.delete_succeed);
                                            else mParent.alert(R.string.delete_failed);
                                            flush();
                                        }
                                    }
                                }
                            };
                            new AlertDialog.Builder(mParent).setTitle(R.string.alert)
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

    private void init()
    {
        try
        {
            LayoutInflater.from(mParent).inflate(R.layout.view_maps, this, true);

            mMapListAdapter = new FileListAdapter(mParent);

            ListView mapList = (ListView) findViewById(R.id.mv_list_view);
            mapList.setOnItemClickListener(mOnMapListClickListener);
            mapList.setAdapter(mMapListAdapter);

            Button loadFromNet = (Button) findViewById(R.id.mv_load_from_net);
            loadFromNet.setOnClickListener(mOnLoadFromNetButtonClick);

            Button loadFromSdcard = (Button) findViewById(R.id.mv_load_from_sdcard);
            loadFromSdcard.setOnClickListener(mOnLoadFromSdcardButtonClick);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init maps view.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    public MapsView(final @NonNull MainActivity parent)
    {
        super(parent);
        mParent = parent;
        init();
    }

    public void flush()
    {
        mParent.setTitleText(R.string.maps);
        List<File> maps = MapManager.getAllMapFiles();
        mMapListAdapter.replace(maps);
    }
}
