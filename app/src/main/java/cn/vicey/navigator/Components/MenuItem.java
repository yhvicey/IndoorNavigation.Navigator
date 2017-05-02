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
import cn.vicey.navigator.Utils.Logger;

/**
 * Menu item class, provides menu item for main menu
 */
public class MenuItem
        extends LinearLayout
{
    //region Constants

    private static final String LOGGER_TAG = "MenuItem";

    //endregion

    //region Fields

    private boolean   mHighlighted; // Whether the item is highlighted
    private ImageView mImageView;   // Image view object
    private TextView  mTextView;    // Text view object

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link MenuItem}
     *
     * @param context Related context
     */
    public MenuItem(Context context)
    {
        this(context, null);
    }

    /**
     * Initialize new instance of class {@link MenuItem}
     *
     * @param context Related context
     * @param attrs   Xml file attributes
     */
    public MenuItem(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
        init(attrs);
    }

    //endregion

    //region Accessors

    /**
     * Gets the item icon
     *
     * @return Item icon
     */
    public Drawable getIcon()
    {
        return mImageView.getDrawable();
    }

    /**
     * Sets the item icon
     *
     * @param value Icon to set
     */
    public void setIcon(Drawable value)
    {
        mImageView.setImageDrawable(value);
    }

    /**
     * Gets the item text
     *
     * @return Item text
     */
    public CharSequence getText()
    {
        return mTextView.getText();
    }

    /**
     * Gets whether the item is highlighted
     *
     * @return Whether the item is highlighted
     */
    public boolean isHighlighted()
    {
        return mHighlighted;
    }

    /**
     * Sets whether the item is highlighted
     *
     * @param value Whether the item should highlight or not
     */
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

    /**
     * Sets the item text
     *
     * @param resId Text string resource id
     */
    public void setText(int resId)
    {
        mTextView.setText(resId);
    }

    /**
     * Sets the item text
     *
     * @param value Text string
     */
    public void setText(CharSequence value)
    {
        mTextView.setText(value);
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
            // Inflate layout
            LayoutInflater.from(getContext()).inflate(R.layout.cmpt_menu_item, this, true);

            // mImageView
            mImageView = (ImageView) findViewById(R.id.mi_icon);

            // mTextView
            mTextView = (TextView) findViewById(R.id.mi_text);
            mTextView.setTypeface(TypefaceManager.getCanaroExtraBold());

            if (attrs == null) return;

            // Gets attributes from xml file
            final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Navigator_MenuItem);

            // icon
            Drawable icon = typedArray.getDrawable(R.styleable.Navigator_MenuItem_icon);
            if (icon != null) setIcon(icon);

            // text
            CharSequence text = typedArray.getText(R.styleable.Navigator_MenuItem_text);
            if (text != null) setText(text);

            // highlighted
            boolean highlighted = typedArray.getBoolean(R.styleable.Navigator_MenuItem_highlighted, false);
            setHighlighted(highlighted);

            typedArray.recycle();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize menu item.", t);
        }
    }

    //endregion
}
