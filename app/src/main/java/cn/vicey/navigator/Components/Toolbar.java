package cn.vicey.navigator.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import cn.vicey.navigator.Managers.TypefaceManager;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Utils.Logger;

/**
 * Toolbar class, provides a toolbar for guillotine menu and main activitys
 */
public class Toolbar
        extends android.support.v7.widget.Toolbar
{
    //region Constants

    private static final String LOGGER_TAG = "Toolbar";

    //endregion

    //region Fields

    private ImageView mImageView; // Image view object
    private TextView  mTextView;  // Text view object

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link Toolbar}
     *
     * @param context Related context
     */
    public Toolbar(Context context)
    {
        this(context, null);
    }

    /**
     * Initialize new instance of class {@link Toolbar}
     *
     * @param context Related context
     * @param attrs   Xml file attributes
     */
    public Toolbar(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
        init(attrs);
    }

    //endregion

    //region Accessors

    /**
     * Gets the toolbar icon
     *
     * @return Toolbar icon
     */
    public Drawable getIcon()
    {
        return mImageView.getDrawable();
    }

    /**
     * Gets the toolbar title text
     *
     * @return Toolbar title text
     */
    public CharSequence getTitleText()
    {
        return mTextView.getText();
    }

    /**
     * Sets the toolbar icon
     *
     * @param value Toolbar icon to set
     */
    public void setIcon(Drawable value)
    {
        mImageView.setImageDrawable(value);
    }

    /**
     * Sets the toolbar title text
     *
     * @param resId Title text string resource id
     */
    public void setTitleText(int resId)
    {
        mTextView.setText(resId);
    }

    /**
     * Sets the toolbar title text
     *
     * @param value Toolbar title text string
     */
    public void setTitleText(CharSequence value)
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
            LayoutInflater.from(getContext()).inflate(R.layout.cmpt_toolbar, this, true);

            // mImageView
            mImageView = (ImageView) findViewById(R.id.t_menu_icon);

            // mTextView
            mTextView = (TextView) findViewById(R.id.t_title);
            mTextView.setTypeface(TypefaceManager.getCanaroExtraBold());

            if (attrs == null) return;

            // Gets attributes from xml file
            final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Navigator_Toolbar);

            // icon
            Drawable icon = typedArray.getDrawable(R.styleable.Navigator_Toolbar_icon);
            if (icon != null) setIcon(icon);

            // title
            CharSequence title = typedArray.getText(R.styleable.Navigator_Toolbar_title);
            if (title != null) setTitleText(title);

            typedArray.recycle();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize toolbar.", t);
        }
    }

    //endregion
}
