package cn.vicey.navigator.Share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.vicey.navigator.R;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter<T>
        extends BaseAdapter
{
    protected final LayoutInflater mInflater;
    protected final List<T> mItems;

    public ListViewAdapter(Context context)
    {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems = new ArrayList<>();
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
}
