package cn.vicey.navigator.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Utils.Logger;

/**
 * Settings item class, provides a checkable item for settings view
 */
public class SettingsItem
        extends LinearLayout
{
    //region Constants

    private static final String LOGGER_TAG = "SettingsItem";

    //endregion

    //region Fields

    private CheckBox mCheckBox; // CheckBox object
    private TextView mTextView; // Text view object

    //endregion

    //region Constructors

    /**
     * Initialize new instance of class {@link SettingsItem}
     *
     * @param context Related context
     */
    public SettingsItem(Context context)
    {
        this(context, null);
    }

    /**
     * Initialize new instance of class {@link SettingsItem}
     *
     * @param context Related context
     * @param attrs   Xml file attributes
     */
    public SettingsItem(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
        init(attrs);
    }

    //endregion

    //region Accessors

    /**
     * Gets whether the item is checked
     *
     * @return Whether the item is checked
     */
    public boolean getChecked()
    {
        return mCheckBox.isChecked();
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
     * Sets whether the item is checked
     *
     * @param value Whether the item is checked
     */
    public void setChecked(boolean value)
    {
        mCheckBox.setChecked(value);
    }

    /**
     * Sets the item text
     *
     * @param resId Item text string resource id
     */
    public void setText(int resId)
    {
        mTextView.setText(resId);
    }

    /**
     * Sets the item text
     *
     * @param value Item text string
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
            LayoutInflater.from(getContext()).inflate(R.layout.cmpt_settings_item, this, true);

            // mCheckBox
            mCheckBox = (CheckBox) findViewById(R.id.si_checkBox);

            // mTextView
            mTextView = (TextView) findViewById(R.id.si_textView);

            if (attrs == null) return;

            // Gets attributes from xml file
            final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Navigator_SettingsItem);

            // checked
            boolean checked = typedArray.getBoolean(R.styleable.Navigator_SettingsItem_checked, false);
            setChecked(checked);

            // text
            CharSequence text = typedArray.getText(R.styleable.Navigator_SettingsItem_text);
            if (text != null) setText(text);

            typedArray.recycle();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize settings item.", t);
        }
    }

    //endregion
}
