package cn.vicey.navigator.Share;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.vicey.navigator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic list view adapter, provides a generic class to use as list view adapter
 *
 * @param <T> List item type
 */
public class ListViewAdapter<T>
        extends BaseAdapter
{
    //region Fields

    protected final LayoutInflater mInflater; // List view's inflater

    protected final List<T> mItems = new ArrayList<>(); // List items

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link ListViewAdapter}
     *
     * @param context Related context
     */
    public ListViewAdapter(Context context)
    {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //endregion

    //region Methods

    /**
     * Add item to list
     *
     * @param item Item to add
     */
    public void addItem(final @NonNull T item)
    {
        mItems.add(item);
        notifyDataSetChanged();
    }

    /**
     * Clear all items
     */
    public void clear()
    {
        mItems.clear();
        notifyDataSetChanged();
    }

    /**
     * Replace items with new items
     *
     * @param items Items to replace with
     */
    public void replace(final @NonNull List<T> items)
    {
        mItems.clear();
        if (items == null) return;
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    //endregion

    //region Override methods

    @Override
    public int getCount()
    {
        return mItems.size();
    }

    @Override
    public T getItem(int i)
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

    //endregion
}
