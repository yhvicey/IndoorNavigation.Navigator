package cn.vicey.navigator.Views;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import cn.vicey.navigator.Activities.MainActivity;
import cn.vicey.navigator.Components.FileList;
import cn.vicey.navigator.Managers.MapManager;
import cn.vicey.navigator.Managers.NavigateManager;
import cn.vicey.navigator.Models.Map;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Utils.Logger;
import cn.vicey.navigator.Utils.Tools;

import java.io.File;

/**
 * Maps view, provides a view to manage all map files
 */
public class MapsView
        extends RelativeLayout
{
    //region Constants

    private static final String LOGGER_TAG = "MapsView";

    //endregion

    //region Listeners

    private final FileList.OnItemChooseListener mOnFileListItemChooseListener        = new FileList.OnItemChooseListener() // File list item choose listener
    {
        @Override
        public void onChooseFile(File chosenFile)
        {
            if (MapManager.saveMapFile(chosenFile, true))
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

        @Override
        public void onOpenDirFailed()
        {
            mParent.alert(R.string.cant_open_folder);
        }
    };
    private final FileList.OnItemChooseListener mOnMapListItemChooseListener         = new FileList.OnItemChooseListener() // Map list item choose listener
    {
        @Override
        public void onChooseFile(final File chosenFile)
        {
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
                            Map map;
                            if ((map = MapManager.loadMap(chosenFile.getName())) != null)
                            {
                                NavigateManager.setCurrentMap(map);
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
                            final EditText editor = new EditText(mParent);
                            editor.setText(chosenFile.getName());
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
                                            if (MapManager.renameMapFile(chosenFile.getName(), newMapName))
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
                                            if (MapManager.deleteMapFile(chosenFile.getName()))
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

        @Override
        public void onOpenDirFailed()
        {
            mParent.alert(R.string.cant_open_folder);
        }
    };
    private final OnClickListener               mOnLoadFromNetButtonClickListener    = new OnClickListener()               // Load from net button click listener
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
                            Tools.downloadFile(url, new Tools.OnDownloadListener()
                            {
                                @Override
                                public void onDownloadSucceed(@NonNull File file)
                                {
                                    if (MapManager.saveMapFile(file, true))
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
    private final OnClickListener               mOnLoadFromSdcardButtonClickListener = new OnClickListener()               // Load from sdcard button click listener
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

            FileList fileList = new FileList(getContext());
            fileList.setDirectory(Environment.getExternalStorageDirectory());
            fileList.setOnItemChooseListener(mOnFileListItemChooseListener);

            mFileChooserDialog = new AlertDialog.Builder(mParent).setTitle(R.string.load_from_sdcard)
                                                                 .setView(fileList)
                                                                 .show();
            //endregion
        }
    };

    //endregion

    //region Fields

    private DialogInterface mFileChooserDialog; // File chooser dialog
    private FileList        mMapList;           // Map list
    private MainActivity    mParent;            // Parent activity

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link MapsView}
     *
     * @param parent Parent activity
     */
    public MapsView(final @NonNull MainActivity parent)
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
            LayoutInflater.from(mParent).inflate(R.layout.view_maps, this, true);

            mMapList = (FileList) findViewById(R.id.mv_file_list);
            mMapList.setOnItemChooseListener(mOnMapListItemChooseListener);
            mMapList.setDirectory(MapManager.getMapDir());
            mMapList.hideParent();

            Button loadFromNet = (Button) findViewById(R.id.mv_load_from_net);
            loadFromNet.setOnClickListener(mOnLoadFromNetButtonClickListener);

            Button loadFromSdcard = (Button) findViewById(R.id.mv_load_from_sdcard);
            loadFromSdcard.setOnClickListener(mOnLoadFromSdcardButtonClickListener);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init maps view.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    /**
     * Flush view
     */
    public void flush()
    {
        mParent.setTitleText(R.string.maps);
        mMapList.flush();
    }

    //endregion
}
