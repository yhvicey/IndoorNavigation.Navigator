package cn.vicey.navigator.Activities;

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
    private TextView mTextView = null;
    private CheckBox mCheckBox = null;

    public SettingsItem(Context context)
    {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.settings_item, this, true);
        mTextView = (TextView) findViewById(R.id.si_textView);
        mCheckBox = (CheckBox) findViewById(R.id.si_checkBox);
    }

    public SettingsItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.settings_item, this, true);
        mTextView = (TextView) findViewById(R.id.si_textView);
        mCheckBox = (CheckBox) findViewById(R.id.si_checkBox);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingsItem);
        CharSequence text = typedArray.getText(R.styleable.SettingsItem_text);
        if (text != null) setText(text);
        boolean checked = typedArray.getBoolean(R.styleable.SettingsItem_checked, false);
        setCheckd(checked);
        typedArray.recycle();
    }

    public SettingsItem(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.settings_item, this, true);
        mTextView = (TextView) findViewById(R.id.si_textView);
        mCheckBox = (CheckBox) findViewById(R.id.si_checkBox);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingsItem);
        CharSequence text = typedArray.getText(R.styleable.SettingsItem_text);
        if (text != null) setText(text);
        boolean checked = typedArray.getBoolean(R.styleable.SettingsItem_checked, false);
        setCheckd(checked);
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

    public void setCheckd(boolean value)
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
