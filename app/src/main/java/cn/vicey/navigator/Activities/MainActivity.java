package cn.vicey.navigator.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import cn.vicey.navigator.Contracts.Map;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.Logger;
import com.yalantis.guillotine.animation.GuillotineAnimation;

public class MainActivity
        extends AppCompatActivity
{
    //region Constants

    private static final int VIEW_NAVIGATE = 0;
    private static final int VIEW_MAPS = 1;
    private static final int VIEW_TAGS = 2;
    private static final int VIEW_SETTINGS = 3;
    private static final long RIPPLE_DURATION = 250;

    //endregion

    //region Variables

    private Map mCurrentMap = null;

    //endregion

    //region Functions

    //endregion

    //region System event callbacks

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FrameLayout rootView = (FrameLayout) findViewById(R.id.root);
        View mainMenu = LayoutInflater.from(this).inflate(R.layout.menu_main, null);
        rootView.addView(mainMenu);

        new GuillotineAnimation.GuillotineBuilder(mainMenu, mainMenu.findViewById(R.id.t_menu_icon), toolbar.findViewById(R.id.t_menu_icon)).setStartDelay(RIPPLE_DURATION).setActionBarViewForAnimation(toolbar).setClosedOnStart(true).build();
    }

    @Override
    protected void onDestroy()
    {
        Logger.flush();
        super.onDestroy();
    }

    //endregion
}
