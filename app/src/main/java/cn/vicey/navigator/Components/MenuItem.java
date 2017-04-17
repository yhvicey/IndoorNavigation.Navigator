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
import cn.vicey.navigator.Share.TypefaceManager;

public class MenuItem
        extends LinearLayout
{
    private boolean mHighlighted;
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
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.cmpt_menu_item, this, true);
        mImageView = (ImageView) findViewById(R.id.mi_icon);
        mTextView = (TextView) findViewById(R.id.mi_text);
        mTextView.setTypeface(TypefaceManager.getCanaroExtraBold());

        if (attrs == null) return;

        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Navigator_MenuItem, defStyle, 0);
        Drawable icon = typedArray.getDrawable(R.styleable.Navigator_MenuItem_icon);
        if (icon != null) setIcon(icon);
        CharSequence text = typedArray.getText(R.styleable.Navigator_MenuItem_text);
        if (text != null) setText(text);
        boolean highlighted = typedArray.getBoolean(R.styleable.Navigator_MenuItem_highlighted, false);
        setHighlighted(highlighted);
        typedArray.recycle();
    }

    public boolean getHighlighted()
    {
        return mHighlighted;
    }

    public Drawable getIcon()
    {
        return mImageView.getDrawable();
    }

    public CharSequence getText()
    {
        return mTextView.getText();
    }

    public void setHighlighted(boolean value)
    {
        mHighlighted = value;
        if (mHighlighted)
        {
            mTextView.setTextColor(getResources().getColor(R.color.highlighted));
        }
        else
        {
            mTextView.setTextColor(getResources().getColor(R.color.text));
        }
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
