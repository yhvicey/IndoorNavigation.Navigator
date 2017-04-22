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
import cn.vicey.navigator.Managers.SettingsManager;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Utils.Logger;
import cn.vicey.navigator.Views.*;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import java.util.Date;

/**
 * Main activity, contains views and components
 */
public class MainActivity
        extends AppCompatActivity
{
    //region Constants

    private static final String LOGGER_TAG = "MainActivity";

    private static final long   RIPPLE_DURATION = 250;           // Guillotine animation duration
    private static final Object SYNC_LOCK       = new Object();  // Sync lock

    public static final long LONG_TOAST_DURATION   = 3500;       // Toast duration constant
    public static final long MIDDLE_TOAST_DURATION = 2000;       // Toast duration constant
    public static final int  REQ_STORAGE           = 1;          // Request code for storage
    public static final long SHORT_TOAST_DURATION  = 1000;       // Toast duration constant
    public static final int  VIEW_LOG              = 4;          // Log view index
    public static final int  VIEW_MAPS             = 1;          // Maps view index
    public static final int  VIEW_NAVIGATE         = 0;          // Navigate view index
    public static final int  VIEW_SETTINGS         = 3;          // Settings view index
    public static final int  VIEW_TAGS             = 2;          // Tags view index

    //endregion

    //region Listeners

    private final GuillotineListener   mGuillotineListener      = new GuillotineListener()   // Guillotine menu listener
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
    private final View.OnClickListener mOnClickListener         = new View.OnClickListener() // Default click listener
    {
        @Override
        public void onClick(View view)
        {
            // no-op
        }
    };
    private final View.OnClickListener mOnMenuItemClickListener = new View.OnClickListener() // Main menu item click listener
    {
        @Override
        public void onClick(View view)
        {
            try
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
            catch (Throwable t)
            {
                Logger.error(LOGGER_TAG, "Failed to handle menu item click.", t);
            }
        }
    };
    private final View.OnClickListener mOnTitleClickListener    = new View.OnClickListener() // Title click listener
    {
        @Override
        public void onClick(View view)
        {
            try
            {
                if (SettingsManager.isDebugModeEnabled()) return;
                if (view.getId() != R.id.t_title) return;
                if (new Date().getTime() - mLastClickTime > 2 * 1000)
                {
                    mLastClickTime = new Date().getTime();
                    mClickCount = 1;
                    return;
                }
                mClickCount++;
                if (mClickCount > 5)
                {
                    SettingsManager.setDebugModeEnabled(true);
                    alert(R.string.debug_mode_enabled);
                    flush();
                }
                else if (mClickCount > 3)
                {
                    alert(getString(R.string.debug_mode_notification, 5 - mClickCount + 1));
                }
            }
            catch (Throwable t)
            {
                Logger.error(LOGGER_TAG, "Failed to handle title click.", t);
            }
        }
    };

    //endregion

    //region Fields

    private int                                   mClickCount;          // Click count to enable debug mode
    private Toast                                 mCurrentToast;        // Current toast object
    private GuillotineAnimation                   mGuillotineAnimation; // Guillotine menu animation
    private boolean                               mIsMenuOpened;        // Is the guillotine menu opened
    private long                                  mLastBackPressedTime; // Last time the back is pressed
    private long                                  mLastClickTime;       // Last time the debug mode switch is clicked
    private LogView                               mLogView;             // Log view
    private LinearLayout                          mMainMenu;            // Main menu object
    private MapsView                              mMapsView;            // Maps view
    private NavigateView                          mNavigateView;        // Navigate view
    private SettingsView                          mSettingsView;        // Settings view
    private TagsView                              mTagsView;            // Tags view
    private cn.vicey.navigator.Components.Toolbar mToolbar;             // Tool bar object
    private ViewFlipper                           mViewFlipper;         // View flipper to switch views

    private int mCurrentView = VIEW_NAVIGATE; // Current view index

    //endregion

    //region Accessors

    /**
     * Sets view title text
     *
     * @param title Title text
     */
    public void setTitleText(final @NonNull String title)
    {
        mToolbar.setTitleText(title);
    }

    /**
     * Sets view title text
     *
     * @param resId Title string resource id
     */
    public void setTitleText(int resId)
    {
        mToolbar.setTitleText(resId);
    }

    //endregion

    //region Methods

    /**
     * Flush the activity
     */
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

    /**
     * Flush main menu
     */
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

    /**
     * Initialize components and views
     */
    private void init()
    {
        try
        {
            // Inflate layout
            setContentView(R.layout.activity_main);

            // Views
            initViews();

            // Components
            initComponents();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to init activity.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    /**
     * Initialize components
     */
    private void initComponents()
    {
        try
        {
            // Init main menu
            mMainMenu = (LinearLayout) getLayoutInflater().inflate(R.layout.cmpt_main_menu, null);
            ((FrameLayout) findViewById(R.id.root)).addView(mMainMenu);
            mMainMenu.setOnClickListener(mOnClickListener);
            for (int i = 0; i < mMainMenu.getChildCount(); i++)
                mMainMenu.getChildAt(i).setOnClickListener(mOnMenuItemClickListener);

            // Init toolbar
            mToolbar = (cn.vicey.navigator.Components.Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            View titleTextView = mToolbar.findViewById(R.id.t_title);
            titleTextView.setOnClickListener(mOnTitleClickListener);

            // Init main menu animation
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

    /**
     * Initialize views
     */
    private void initViews()
    {
        try
        {
            // Construct view objects
            mLogView = new LogView(this);
            mMapsView = new MapsView(this);
            mNavigateView = new NavigateView(this);
            mTagsView = new TagsView(this);
            mSettingsView = new SettingsView(this);

            // Add views to flipper
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

    /**
     * Alert a message
     *
     * @param redId Message string resource id
     */
    public void alert(int redId)
    {
        alert(getString(redId), MIDDLE_TOAST_DURATION);
    }

    /**
     * Alert a message with specified duration
     *
     * @param redId    Message string resource id
     * @param duration Duration
     */
    public void alert(int redId, final long duration)
    {
        getString(redId, duration);
    }

    /**
     * Alert a message
     *
     * @param message Message string
     */
    public void alert(final @NonNull String message)
    {
        alert(message, MIDDLE_TOAST_DURATION);
    }

    /**
     * Alert a message with specified duration
     *
     * @param message  Message string
     * @param duration Duration
     */
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
                        final Toast toastToCancel = mCurrentToast;
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                toastToCancel.cancel();
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

    /**
     * Check if this activity has specified permission
     *
     * @param permission Permission to check
     * @return Whether the activity has the permission
     */
    public boolean hasPermission(final @NonNull String permission)
    {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Invoke a method on UI thread
     *
     * @param runnable Method to run on UI thread
     */
    public void invoke(final Runnable runnable)
    {
        runOnUiThread(runnable);
    }

    /**
     * Request specified permission with request code
     *
     * @param requestCode Request code to filter the request
     * @param permission  Requested permission
     */
    public void requestPermission(int requestCode, String permission)
    {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    /**
     * Switch the current view by view index
     *
     * @param viewIndex View index
     */
    public void switchView(int viewIndex)
    {
        mCurrentView = viewIndex;
        flush();
    }

    //endregion

    //region Override methods

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
        try
        {
            Logger.flush();
            super.onDestroy();
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to destroy activity.", t);
        }
    }

    @Override
    public void onBackPressed()
    {
        try
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
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to handle back press.", t);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        try
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
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to handle request permission result.", t);
        }
    }

    //endregion
}