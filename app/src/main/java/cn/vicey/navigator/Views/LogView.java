package cn.vicey.navigator.Views;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.vicey.navigator.Activities.MainActivity;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Utils.Logger;

public class LogView
        extends ScrollView
{
    private static final String LOGGER_TAG = "LogView";

    private MainActivity mParent;

    private void init()
    {
        try
        {
            LayoutInflater.from(mParent).inflate(R.layout.view_log, this, true);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init log view.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    public LogView(final @NonNull MainActivity parent)
    {
        super(parent);
        mParent = parent;
        init();
    }

    public void flush()
    {
        mParent.setTitleText(R.string.log);
        TextView textView = (TextView) findViewById(R.id.lv_text_view);
        textView.setText(Logger.getLogContent());
    }
}
