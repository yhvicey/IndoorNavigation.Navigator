package cn.vicey.navigator.Activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ViewFlipper;
import cn.vicey.navigator.Contracts.Map;
import cn.vicey.navigator.R;
import cn.vicey.navigator.Share.Logger;

public class MainActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    //region Constants

    private static final int VIEW_NAVIGATE = 0;
    private static final int VIEW_MAPS = 1;
    private static final int VIEW_TAGS = 2;
    private static final int VIEW_SETTINGS = 3;

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.view_flipper);
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menu_navigate:
            {
                flipper.setDisplayedChild(VIEW_NAVIGATE);
                break;
            }
            case R.id.menu_maps:
            {
                flipper.setDisplayedChild(VIEW_MAPS);
                break;
            }
            case R.id.menu_tags:
            {
                flipper.setDisplayedChild(VIEW_TAGS);
                break;
            }
            case R.id.menu_settings:
            {
                flipper.setDisplayedChild(VIEW_SETTINGS);
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //endregion
}
