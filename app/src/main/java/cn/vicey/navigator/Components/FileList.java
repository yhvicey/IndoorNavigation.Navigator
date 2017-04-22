package cn.vicey.navigator.Components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.ListViewAdapter;
import cn.vicey.navigator.Utils.Logger;
import cn.vicey.navigator.Utils.Tools;

import java.io.File;
import java.util.List;

/**
 * File list component, provides a view to explorer file system
 */
public class FileList
        extends ListView
{
    //region Inner classes

    /**
     * Listener which will be invoked when a item of the list is chosen
     */
    public interface OnItemChooseListener
    {
        //region Methods

        /**
         * Invoked when a file is chosen
         *
         * @param chosenFile The chosen file
         */
        void onChooseFile(File chosenFile);

        /**
         * Invoked when a dir is failed to open
         */
        void onOpenDirFailed();

        //endregion
    }

    /**
     * File list adapter class
     */
    private class FileListAdapter
            extends ListViewAdapter<File>
    {
        //region Methods

        /**
         * Initialize new instance of class {@link FileListAdapter}
         *
         * @param context Related context
         */
        public FileListAdapter(Context context)
        {
            super(context);
        }

        /**
         * Gets item view
         *
         * @param i         Item index
         * @param view      Item view
         * @param viewGroup List view group
         * @return Item view
         */
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
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), file.isFile() ? R.drawable.ic_file : R.drawable.ic_folder));
            TextView textView = (TextView) view.findViewById(R.id.fli_text);
            if (textView != null) textView.setText(file.getName());
            return view;
        }

        //endregion
    }

    //endregion

    //region Constants

    private static final String LOGGER_TAG = "FileList";

    //endregion

    //region Listeners

    private final AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() // List item click listener
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            try
            {
                TextView textView = (TextView) view.findViewById(R.id.fli_text);
                String currentEntryName = textView.getText().toString();
                File nextEntry = currentEntryName.equals("..") ? mCurrentDir.getParentFile() : new File(mCurrentDir + "/" + currentEntryName);
                if (nextEntry.isDirectory()) setDirectory(nextEntry);
                else if (mOnItemChooseListener != null) mOnItemChooseListener.onChooseFile(nextEntry);
            }
            catch (Throwable t)
            {
                Logger.error(LOGGER_TAG, "Failed to handle list item click.", t);
            }
        }
    };

    //endregion

    //region Fields

    private FileListAdapter      mAdapter;              // Adapter for file list
    private File                 mCurrentDir;           // Current directory to show
    private boolean              mHideParent;           // Whether the component should hide the parent folder ("..")
    private OnItemChooseListener mOnItemChooseListener; // Listener for choose item action

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link FileList}
     *
     * @param context Related context
     */
    public FileList(Context context)
    {
        this(context, null);
    }

    /**
     * Initialize new instance of class {@link FileList}
     *
     * @param context Related context
     * @param attrs   Xml file attributes
     */
    public FileList(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
        init(attrs);
    }

    //endregion

    //region Accessors

    /**
     * Sets current directory of the file list
     *
     * @param value Current directory
     */
    public void setDirectory(final File value)
    {
        if (value == null) return;
        List<File> entries = Tools.getEntries(value, false);
        if (entries == null)
        {
            if (mOnItemChooseListener != null) mOnItemChooseListener.onOpenDirFailed();
            return;
        }
        if (!mHideParent) entries.add(0, new File(".."));
        mAdapter.replace(entries);
        mCurrentDir = value;
    }

    /**
     * Sets {@link OnItemChooseListener} for this file list
     *
     * @param value Listener to set
     */
    public void setOnItemChooseListener(final @NonNull OnItemChooseListener value)
    {
        mOnItemChooseListener = value;
    }

    //endregion

    //region Methods

    /**
     * Initialize component
     *
     * @param attrs Xml file attributes
     */
    private void init(AttributeSet attrs)
    {
        try
        {
            // mAdapter
            mAdapter = new FileListAdapter(getContext());

            setAdapter(mAdapter);
            setOnItemClickListener(mOnItemClickListener);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize file list.", t);
        }
    }

    /**
     * Flush the component
     */
    public void flush()
    {
        setDirectory(mCurrentDir);
    }

    /**
     * Hide the parent folder ("..")
     */
    public void hideParent()
    {
        mHideParent = true;
    }

    /**
     * Show the parent folder ("..")
     */
    public void showParent()
    {
        mHideParent = false;
    }

    //endregion
}
