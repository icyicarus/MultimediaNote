package icyicarus.gwu.com.multimedianote;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.squareup.leakcanary.LeakCanary;

import butterknife.BindView;
import butterknife.ButterKnife;
import icyicarus.gwu.com.multimedianote.Fragments.FragmentAbout;
import icyicarus.gwu.com.multimedianote.Fragments.FragmentAllNotes;
import icyicarus.gwu.com.multimedianote.Fragments.FragmentFeedback;
import icyicarus.gwu.com.multimedianote.Fragments.FragmentNote;
import icyicarus.gwu.com.multimedianote.Fragments.FragmentSettings;

public class UserInterface extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    MenuItem activeMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LeakCanary.isInAnalyzerProcess(this))
            return;
        LeakCanary.install(getApplication());

        setContentView(R.layout.activity_user_interface);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("All Notes");
        SharedPreferences userPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putBoolean(Variables.SOB, userPreferences.getBoolean(Variables.SOB, false));
        editor.putBoolean(Variables.CBG, userPreferences.getBoolean(Variables.CBG, false));
        editor.putString(Variables.BGC, userPreferences.getString(Variables.BGC, "000000"));
        editor.apply();
        editor.commit();

        getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentAllNotes()).commit();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_all_notes);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                activeMenu = null;
                navigationView.setCheckedItem(R.id.nav_all_notes);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        if (activeMenu != item) {
            if (activeMenu != null)
                activeMenu.setChecked(false);
            activeMenu = item;
            int id = item.getItemId();
            navigationView.setCheckedItem(id);

            switch (id) {
                case R.id.nav_all_notes:
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    break;
                case R.id.nav_calendar:
                    setTitle("Calendar");
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentNote()).addToBackStack(null).commit();
                    break;
                case R.id.nav_alarm:
                    setTitle("Alarm");
                    break;
                case R.id.nav_settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentSettings()).addToBackStack(null).commit();
                    break;
                case R.id.nav_help_and_feedback:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentFeedback()).addToBackStack(null).commit();
                    break;
                case R.id.nav_about:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentAbout()).addToBackStack(null).commit();
                    break;
                default:
                    break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
