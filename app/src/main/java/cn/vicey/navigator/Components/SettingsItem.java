package cn.vicey.navigator.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.vicey.navigator.R;

public class SettingsItem
        extends LinearLayout
{
    private CheckBox mCheckBox = null;
    private TextView mTextView = null;

    public SettingsItem(Context context)
    {
        this(context, null);
    }

    public SettingsItem(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
        init(attrs);
    }

    private void init(AttributeSet attrs)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.cmpt_settings_item, this, true);
        mCheckBox = (CheckBox) findViewById(R.id.si_checkBox);
        mTextView = (TextView) findViewById(R.id.si_textView);

        if (attrs == null) return;

        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Navigator_SettingsItem);
        boolean checked = typedArray.getBoolean(R.styleable.Navigator_SettingsItem_checked, false);
        setChecked(checked);
        CharSequence text = typedArray.getText(R.styleable.Navigator_SettingsItem_text);
        if (text != null) setText(text);
        typedArray.recycle();
    }

    public boolean getChecked()
    {
        return mCheckBox.isChecked();
    }

    public CharSequence getText()
    {
        return mTextView.getText();
    }

    public void setChecked(boolean value)
    {
        mCheckBox.setChecked(value);
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
