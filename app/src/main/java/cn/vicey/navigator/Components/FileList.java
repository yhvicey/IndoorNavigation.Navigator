package cn.vicey.navigator.Components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.ListViewAdapter;
import cn.vicey.navigator.Share.Utils;

import java.io.File;
import java.util.List;

public class FileList
        extends ListView
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

    public interface OnFileListItemChooseCallback
    {
        void onChooseFile(File chosenFile);

        void onOpenDirFailed();
    }

    private FileListAdapter mAdapter;
    private OnFileListItemChooseCallback mCallback;
    private File mCurrentDir;
    private boolean mHideParent;

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            TextView textView = (TextView) view.findViewById(R.id.fli_text);
            String currentEntryName = textView.getText().toString();
            File nextEntry = currentEntryName.equals("..") ? mCurrentDir.getParentFile() : new File(mCurrentDir + "/" + currentEntryName);
            if (nextEntry.isDirectory()) setDirectory(nextEntry);
            else if (mCallback != null) mCallback.onChooseFile(nextEntry);
        }
    };

    private void init(AttributeSet attrs)
    {
        mAdapter = new FileListAdapter(getContext());
        setAdapter(mAdapter);
        setOnItemClickListener(mOnItemClickListener);
    }

    public FileList(Context context)
    {
        this(context, null);
    }

    public FileList(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public void setCallback(final @NonNull OnFileListItemChooseCallback value)
    {
        mCallback = value;
    }

    public void setDirectory(final File value)
    {
        if (value == null) return;
        List<File> entries = Utils.getEntries(value, false);
        if (entries == null)
        {
            if (mCallback != null) mCallback.onOpenDirFailed();
            return;
        }
        if (!mHideParent) entries.add(0, new File(".."));
        mAdapter.replace(entries);
        mCurrentDir = value;
    }

    public void flush()
    {
        setDirectory(mCurrentDir);
    }

    public void hideParent()
    {
        mHideParent = true;
    }

    public void showParent()
    {
        mHideParent = false;
    }
}
