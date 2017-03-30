package cn.vicey.navigator.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class MapView
        extends View
{
    public MapView(Context context)
    {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize(attrs, defStyle);
    }

    private void initialize(AttributeSet attrs, int defStyle)
    {

    }
}
