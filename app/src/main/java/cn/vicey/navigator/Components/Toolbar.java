package cn.vicey.navigator.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.TypefaceManager;

public class Toolbar
        extends android.support.v7.widget.Toolbar
{
    private ImageView mImageView;
    private TextView mTextView;

    public Toolbar(Context context)
    {
        this(context, null, 0);
    }

    public Toolbar(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public Toolbar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize(attrs, defStyle);
    }

    private void initialize(AttributeSet attrs, int defStyle)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.cmpt_toolbar, this, true);
        mImageView = (ImageView) findViewById(R.id.t_menu_icon);
        mTextView = (TextView) findViewById(R.id.t_title);
        mTextView.setTypeface(TypefaceManager.getCanaroExtraBold());

        if (attrs == null) return;

        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Navigator_Toolbar, defStyle, 0);
        Drawable icon = typedArray.getDrawable(R.styleable.Navigator_Toolbar_icon);
        if (icon != null) setIcon(icon);
        CharSequence title = typedArray.getText(R.styleable.Navigator_Toolbar_title);
        if (title != null) setTitleText(title);
        typedArray.recycle();
    }

    public Drawable getIcon()
    {
        return mImageView.getDrawable();
    }

    public CharSequence getTitleText()
    {
        return mTextView.getText();
    }

    public void setIcon(Drawable value)
    {
        mImageView.setImageDrawable(value);
    }

    public void setTitleText(CharSequence value)
    {
        mTextView.setText(value);
    }

    public void setTitleText(int resId)
    {
        mTextView.setText(resId);
    }
}
