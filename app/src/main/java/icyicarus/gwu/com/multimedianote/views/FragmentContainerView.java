package icyicarus.gwu.com.multimedianote.views;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import icyicarus.gwu.com.multimedianote.R;
import icyicarus.gwu.com.multimedianote.Variables;
import icyicarus.gwu.com.multimedianote.fragments.FragmentAbout;
import icyicarus.gwu.com.multimedianote.fragments.FragmentAllNotes;
import icyicarus.gwu.com.multimedianote.fragments.FragmentCalendar;
import icyicarus.gwu.com.multimedianote.fragments.FragmentFeedback;
import icyicarus.gwu.com.multimedianote.fragments.FragmentSettings;

public class FragmentContainerView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;

    MenuItem activeMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LeakCanary.isInAnalyzerProcess(this))
            return;
        LeakCanary.install(getApplication());

        setContentView(R.layout.activity_user_interface);
        ButterKnife.bind(this);
        Fresco.initialize(this);

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            AndPermission.with(this)
                    .requestCode(3)
                    .permission(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    .send();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager().popBackStack();
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
        }
        int id = item.getItemId();
        navigationView.setCheckedItem(id);
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        switch (id) {
            case R.id.nav_calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentCalendar()).addToBackStack(null).commit();
                break;
            case R.id.nav_alarm:
//                    setTitle("Alarm");
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
            case R.id.nav_all_notes:
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, List<String> grantPermissions) {

            }

            @Override
            public void onFailed(int requestCode, List<String> deniedPermissions) {
                String message = "Permission denied, some function will be disabled until manually granted permission";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}