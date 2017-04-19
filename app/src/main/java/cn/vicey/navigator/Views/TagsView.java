package cn.vicey.navigator.Views;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.vicey.navigator.Activities.MainActivity;
import cn.vicey.navigator.Managers.MapManager;
import cn.vicey.navigator.Models.Tag;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.ListViewAdapter;
import cn.vicey.navigator.Utils.Logger;

import java.util.List;

public class TagsView
        extends RelativeLayout
{
    private class TagListAdapter
            extends ListViewAdapter<Tag>
    {
        public TagListAdapter(Context context)
        {
            super(context);
        }

        public List<Tag> getAll()
        {
            return mItems;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.cmpt_tag_list_item, null);
            }
            Tag tag = mItems.get(i);
            TextView textView = (TextView) view.findViewById(R.id.tli_text);
            TextView subTextView = (TextView) view.findViewById(R.id.tli_sub_text);
            if (textView != null) textView.setText(tag.getValue());
            if (subTextView != null)
                subTextView.setText(mParent.getString(R.string.tag_metadata, tag.getFloor(), Tag.getNodeText(tag.getType()), tag
                        .getIndex()));
            return view;
        }
    }

    private static final String LOGGER_TAG = "TagsView";

    private MainActivity mParent;
    private TagListAdapter mTagListAdapter;

    private OnClickListener mOnLoadTagButtonClick = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() != R.id.tv_load_tags) return;
            //region Load tags
            if (MapManager.getCurrentMap() == null)
            {
                mParent.alert(R.string.no_loaded_map);
                return;
            }
            String mapName = MapManager.getCurrentMap().getName();
            List<Tag> tags = MapManager.loadTags(mapName);
            if (tags == null) mParent.alert(R.string.no_tag);
            else
            {
                if (MapManager.getCurrentMap().setTags(tags)) mParent.alert(R.string.load_succeed);
                else mParent.alert(R.string.load_failed);
            }
            //endregion
        }
    };
    private OnClickListener mOnSaveTagButtonClick = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            //region Save tags
            if (MapManager.getCurrentMap() == null)
            {
                mParent.alert(R.string.no_loaded_map);
                return;
            }
            List<Tag> tags = mTagListAdapter.getAll();
            if (tags.size() == 0)
            {
                mParent.alert(R.string.no_tag);
                return;
            }
            if (MapManager.saveTags(MapManager.getCurrentMap().getName(), tags)) mParent.alert(R.string.save_succeed);
            else mParent.alert(R.string.save_failed);
            //endregion
        }
    };
    private ListView.OnItemClickListener mOnTagListItemClickListener = new ListView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            final TextView textView = (TextView) view.findViewById(R.id.fli_text);
            new AlertDialog.Builder(mParent).setTitle(R.string.manage).setItems(new String[]{
                    mParent.getString(R.string.modify),
                    mParent.getString(R.string.delete)
            }, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                    switch (i)
                    {
                        //region Modify
                        case 0:
                        {
                            final String tagValue = textView.getText().toString();
                            final EditText editor = new EditText(mParent);
                            editor.setText(tagValue);
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
                                            String newTagValue = editor.getText().toString();

                                            if (MapManager.renameMapFile(tagValue, newTagValue))
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
                        case 1:
                        {
                            new AlertDialog.Builder(mParent).setTitle(R.string.alert)
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
                                                                                mParent.alert(R.string.delete_succeed);
                                                                            else mParent.alert(R.string.delete_failed);
                                                                            flush();
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

    private void init()
    {
        try
        {
            LayoutInflater.from(mParent).inflate(R.layout.view_tags, this, true);

            mTagListAdapter = new TagListAdapter(mParent);

            ListView tagList = (ListView) findViewById(R.id.tv_list_view);
            tagList.setOnItemClickListener(mOnTagListItemClickListener);
            tagList.setAdapter(mTagListAdapter);

            Button loadTagButton = (Button) findViewById(R.id.tv_load_tags);
            loadTagButton.setOnClickListener(mOnLoadTagButtonClick);

            Button saveTagButton = (Button) findViewById(R.id.tv_save_tags);
            saveTagButton.setOnClickListener(mOnSaveTagButtonClick);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init tags view.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }

    }

    public TagsView(final @NonNull MainActivity parent)
    {
        super(parent);
        mParent = parent;
        init();
    }

    public void flush()
    {
        mParent.setTitleText(R.string.tags);
        if (MapManager.getCurrentMap() != null)
        {
            List<Tag> tags = MapManager.getCurrentMap().getTags();
            mTagListAdapter.replace(tags);
        }
        else
        {
            mParent.alert(R.string.no_loaded_map);
        }
    }
}
