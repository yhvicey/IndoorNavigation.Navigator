package cn.vicey.navigator.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;
import cn.vicey.navigator.Components.MenuItem;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.Logger;
import cn.vicey.navigator.Views.*;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import java.util.Date;

public class MainActivity
        extends AppCompatActivity
{
    //region Constants

    private static final String LOGGER_TAG = "MainActivity";
    private static final long RIPPLE_DURATION = 250;
    private static final Object SYNC_LOCK = new Object();

    public static final int VIEW_NAVIGATE = 0;
    public static final int VIEW_MAPS = 1;
    public static final int VIEW_TAGS = 2;
    public static final int VIEW_SETTINGS = 3;
    public static final int VIEW_LOG = 4;
    public static final int REQ_STORAGE = 1;
    public static final long LONG_TOAST_DURATION = 3500;
    public static final long MIDDLE_TOAST_DURATION = 2000;
    public static final long SHORT_TOAST_DURATION = 1000;

    //endregion

    //region Variables

    private Toast mCurrentToast;
    private int mCurrentView = VIEW_NAVIGATE;
    private boolean mIsMenuOpened;
    private long mLastBackPressedTime;

    //endregion

    //region UI variables

    private GuillotineAnimation mGuillotineAnimation;
    private LinearLayout mMainMenu;
    private cn.vicey.navigator.Components.Toolbar mToolbar;
    private ViewFlipper mViewFlipper;

    //endregion

    //region View variables

    private LogView mLogView;
    private MapsView mMapsView;
    private NavigateView mNavigateView;
    private SettingsView mSettingsView;
    private TagsView mTagsView;

    //endregion

    //region Callback variables

    private GuillotineListener mGuillotineListener = new GuillotineListener()
    {
        @Override
        public void onGuillotineOpened()
        {
            mIsMenuOpened = true;
        }

        @Override
        public void onGuillotineClosed()
        {
            mIsMenuOpened = false;
        }
    };

    //endregion

    //region Callback functions

    public void onClick(View view)
    {

    }

    public void onMenuItemClick(View view)
    {
        switch (view.getId())
        {
            case R.id.mm_navigate:
            {
                switchView(VIEW_NAVIGATE);
                break;
            }
            case R.id.mm_maps:
            {
                switchView(VIEW_MAPS);
                break;
            }
            case R.id.mm_tags:
            {
                switchView(VIEW_TAGS);
                break;
            }
            case R.id.mm_settings:
            {
                switchView(VIEW_SETTINGS);
                break;
            }
        }
        flush();
        mGuillotineAnimation.close();
    }

    //endregion

    //region Initialize functions

    private void init()
    {
        try
        {
            setContentView(R.layout.activity_main);
            initViews();
            initComponents();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init activity.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    private void initComponents()
    {
        try
        {
            mMainMenu = (LinearLayout) getLayoutInflater().inflate(R.layout.cmpt_main_menu, null);
            ((FrameLayout) findViewById(R.id.root)).addView(mMainMenu);

            mToolbar = (cn.vicey.navigator.Components.Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);

            mGuillotineAnimation = new GuillotineAnimation.GuillotineBuilder(mMainMenu, mMainMenu.findViewById(R.id.t_menu_icon), mToolbar
                    .findViewById(R.id.t_menu_icon)).setStartDelay(RIPPLE_DURATION)
                                                    .setActionBarViewForAnimation(mToolbar)
                                                    .setClosedOnStart(true)
                                                    .setGuillotineListener(mGuillotineListener)
                                                    .build();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init components.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    private void initViews()
    {
        try
        {
            mLogView = new LogView(this);
            mMapsView = new MapsView(this);
            mNavigateView = new NavigateView(this);
            mTagsView = new TagsView(this);
            mSettingsView = new SettingsView(this);
            mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
            mViewFlipper.addView(mNavigateView);
            mViewFlipper.addView(mMapsView);
            mViewFlipper.addView(mTagsView);
            mViewFlipper.addView(mSettingsView);
            mViewFlipper.addView(mLogView);
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init views.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    //endregion

    //region Flush functions

    private void flush()
    {
        if (mCurrentView >= mViewFlipper.getChildCount()) mCurrentView = VIEW_NAVIGATE;
        if (mViewFlipper.getDisplayedChild() != mCurrentView) mViewFlipper.setDisplayedChild(mCurrentView);
        flushMainMenu();
        switch (mCurrentView)
        {
            case VIEW_NAVIGATE:
            {
                mNavigateView.flush();
                break;
            }
            case VIEW_MAPS:
            {
                mMapsView.flush();
                break;
            }
            case VIEW_TAGS:
            {
                mTagsView.flush();
                break;
            }
            case VIEW_SETTINGS:
            {
                mSettingsView.flush();
                break;
            }
            case VIEW_LOG:
            {
                mLogView.flush();
                break;
            }
        }
    }

    private void flushMainMenu()
    {
        int count = mMainMenu.getChildCount();
        int index = 0;
        for (int i = 0; i < count; i++)
        {
            View childView = mMainMenu.getChildAt(i);
            if (!(childView instanceof MenuItem)) continue;
            ((MenuItem) childView).setHighlighted(index++ == mCurrentView);
        }
    }

    //endregion

    //region Functions

    public void alert(int redId)
    {
        alert(getString(redId), MIDDLE_TOAST_DURATION);
    }

    public void alert(int redId, final long duration)
    {
        getString(redId, duration);
    }

    public void alert(final @NonNull String message)
    {
        alert(message, MIDDLE_TOAST_DURATION);
    }

    public void alert(final @NonNull String message, final long duration)
    {
        try
        {
            synchronized (SYNC_LOCK)
            {
                invoke(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mCurrentToast != null) mCurrentToast.cancel();
                        mCurrentToast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
                        mCurrentToast.show();
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mCurrentToast.cancel();
                            }
                        }, duration);
                    }
                });
            }
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to alert message.", t);
        }
    }

    public boolean hasPermission(final @NonNull String permission)
    {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void invoke(final Runnable runnable)
    {
        runOnUiThread(runnable);
    }

    public void switchView(int viewIndex)
    {
        mCurrentView = viewIndex;
        flush();
    }

    public void requestPermission(int requestCode, String permission)
    {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    public void setTitleText(final @NonNull String title)
    {
        mToolbar.setTitleText(title);
    }

    public void setTitleText(int resId)
    {
        mToolbar.setTitleText(resId);
    }

    //endregion

    //region System event callbacks

    @Override
    public void onBackPressed()
    {
        if (mIsMenuOpened)
        {
            mGuillotineAnimation.close();
        }
        else
        {
            long current = new Date().getTime();
            if (current - mLastBackPressedTime > SHORT_TOAST_DURATION)
            {
                alert(getString(R.string.exit_notification), SHORT_TOAST_DURATION);
                mLastBackPressedTime = current;
            }
            else
            {
                super.onBackPressed();
                Navigator.exit();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);

            init();

            flush();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init main activity.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    @Override
    protected void onDestroy()
    {
        Logger.flush();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQ_STORAGE:
            {
                if (grantResults.length != 0)
                {
                    int index = 0;
                    for (String permission : permissions)
                    {
                        if (permission.equals(Manifest.permission_group.STORAGE))
                        {
                            if (grantResults[index] == PackageManager.PERMISSION_GRANTED) return;
                            else break;
                        }
                        index++;
                    }
                }
            }
        }
    }

    //endregion
}