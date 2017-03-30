package cn.vicey.navigator.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import cn.vicey.navigator.Components.MenuItem;
import cn.vicey.navigator.Contracts.Map;
import cn.vicey.navigator.Navigator;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.Logger;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
{
    //region Constants

    private static final String LOGGER_TAG = "MainActivity";
    private static final int VIEW_NAVIGATE = 0;
    private static final int VIEW_MAPS = 1;
    private static final int VIEW_TAGS = 2;
    private static final int VIEW_SETTINGS = 3;
    private static final long RIPPLE_DURATION = 250;

    //endregion

    //region Variables

    private Map mCurrentMap = null;
    private boolean mIsMenuOpened = false;

    //endregion

    //region UI variables

    private GuillotineAnimation mGuillotineAnimation;
    private List<MenuItem> mMenuItems;
    private cn.vicey.navigator.Components.Toolbar mToolbar;
    private ViewFlipper mViewFlipper;

    //endregion

    //region Callback variables

    private View.OnClickListener mDefaultOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {

        }
    };

    private View.OnClickListener mMenuItemOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view instanceof MenuItem)
            {
                clearSelection();
                ((MenuItem) view).setHighlighted(true);
            }
            switch (view.getId())
            {
                case R.id.menu_navigate:
                {
                    switchView(VIEW_NAVIGATE);
                    break;
                }
                case R.id.menu_maps:
                {
                    switchView(VIEW_MAPS);
                    break;
                }
                case R.id.menu_tags:
                {
                    switchView(VIEW_TAGS);
                    break;
                }
                case R.id.menu_settings:
                {
                    switchView(VIEW_SETTINGS);
                    break;
                }
            }
        }
    };

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

    //region Initialize functions

    private void initMainMenu(View view)
    {
        try
        {
            if (!(view instanceof LinearLayout))
                throw new InvalidParameterException("view is no a instance of LinearLayout.");
            LinearLayout menu = (LinearLayout) view;
            menu.setOnClickListener(mDefaultOnClickListener);
            mMenuItems = new ArrayList<>();
            int count = menu.getChildCount();
            for (int i = 0; i < count; i++)
            {
                View childView = menu.getChildAt(i);
                if (!(childView instanceof MenuItem)) continue;
                childView.setOnClickListener(mMenuItemOnClickListener);
                mMenuItems.add((MenuItem) childView);
            }
        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize main menu.", t);
            Navigator.exitWithError(Navigator.ERR_INIT);
        }
    }

    //endregion

    //region Functions

    private void clearSelection()
    {
        for (MenuItem item : mMenuItems)
        {
            item.setHighlighted(false);
        }
    }

    private void switchView(int viewId)
    {
        switch (viewId)
        {
            case VIEW_NAVIGATE:
            {
                mViewFlipper.setDisplayedChild(VIEW_NAVIGATE);
                mToolbar.setTitleText(R.string.navigate);
                break;
            }
            case VIEW_MAPS:
            {
                mViewFlipper.setDisplayedChild(VIEW_MAPS);
                mToolbar.setTitleText(R.string.maps);
                break;
            }
            case VIEW_TAGS:
            {
                mViewFlipper.setDisplayedChild(VIEW_TAGS);
                mToolbar.setTitleText(R.string.tags);
                break;
            }
            case VIEW_SETTINGS:
            {
                mViewFlipper.setDisplayedChild(VIEW_SETTINGS);
                mToolbar.setTitleText(R.string.settings);
                break;
            }
        }
        mGuillotineAnimation.close();
    }

    //endregion

    //region System event callbacks

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mToolbar = (cn.vicey.navigator.Components.Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);

            mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

            FrameLayout rootView = (FrameLayout) findViewById(R.id.root);
            View mainMenu = LayoutInflater.from(this).inflate(R.layout.menu_main, null);
            initMainMenu(mainMenu);
            rootView.addView(mainMenu);

            mGuillotineAnimation = new GuillotineAnimation.GuillotineBuilder(mainMenu, mainMenu.findViewById(R.id.t_menu_icon), mToolbar
                    .findViewById(R.id.t_menu_icon)).setStartDelay(RIPPLE_DURATION)
                                                    .setActionBarViewForAnimation(mToolbar)
                                                    .setClosedOnStart(true)
                                                    .setGuillotineListener(mGuillotineListener)
                                                    .build();

        }
        catch (Throwable t)
        {
            Logger.error(LOGGER_TAG, "Failed to initialize main activity.", t);
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
    public void onBackPressed()
    {
        if (mIsMenuOpened)
        {
            mGuillotineAnimation.close();
        }
        else
        {
            super.onBackPressed();
        }
    }

    //endregion
}
