package cn.vicey.navigator.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.vicey.navigator.R;

public class MenuItem
        extends LinearLayout
{
    private ImageView mImageView;
    private TextView mTextView;

    public MenuItem(Context context)
    {
        this(context, null, 0);
    }

    public MenuItem(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public MenuItem(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize(attrs, defStyle);
    }

    private void initialize(AttributeSet attrs, int defStyle)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.menu_item, this, true);
        mImageView = (ImageView) findViewById(R.id.mi_icon);
        mTextView = (TextView) findViewById(R.id.mi_text);

        if (attrs == null) return;

        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Navigator_MenuItem, defStyle, 0);
        Drawable icon = typedArray.getDrawable(R.styleable.Navigator_MenuItem_icon);
        if (icon != null) setIcon(icon);
        CharSequence text = typedArray.getText(R.styleable.Navigator_MenuItem_text);
        if (text != null) setText(text);
        typedArray.recycle();
    }

    public Drawable getIcon()
    {
        return mImageView.getDrawable();
    }

    public CharSequence getText()
    {
        return mTextView.getText();
    }

    public void setIcon(Drawable value)
    {
        mImageView.setImageDrawable(value);
    }

    public void setText(CharSequence value)
    {
        mTextView.setText(value);
    }

    public void setText(int resId)
    {
        mTextView.setText(resId);
    }
}
