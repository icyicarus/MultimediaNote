package com.icyicarus.multimedianote.views;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.icyicarus.multimedianote.FontManager;
import com.icyicarus.multimedianote.NoteDB;
import com.icyicarus.multimedianote.OperationDetail;
import com.icyicarus.multimedianote.R;
import com.icyicarus.multimedianote.fragments.FragmentAbout;
import com.icyicarus.multimedianote.fragments.FragmentAlarm;
import com.icyicarus.multimedianote.fragments.FragmentAllNotes;
import com.icyicarus.multimedianote.fragments.FragmentCalendar;
import com.icyicarus.multimedianote.fragments.FragmentFeedback;
import com.icyicarus.multimedianote.fragments.FragmentNote;
import com.icyicarus.multimedianote.medialist.AdapterMediaList;
import com.icyicarus.multimedianote.medialist.MediaContent;
import com.icyicarus.multimedianote.notelist.AdapterNoteList;
import com.icyicarus.multimedianote.notelist.NoteContent;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;

import static com.icyicarus.multimedianote.Variables.EXTRA_NOTE_DATA;
import static com.icyicarus.multimedianote.Variables.FILE_PROVIDER_AUTHORITIES;
import static com.icyicarus.multimedianote.Variables.MEDIA_TYPE_AUDIO;
import static com.icyicarus.multimedianote.Variables.MEDIA_TYPE_PHOTO;
import static com.icyicarus.multimedianote.Variables.MEDIA_TYPE_VIDEO;
import static com.icyicarus.multimedianote.Variables.TAG_FRAGMENT_ABOUT;
import static com.icyicarus.multimedianote.Variables.TAG_FRAGMENT_ALARM;
import static com.icyicarus.multimedianote.Variables.TAG_FRAGMENT_ALL_NOTE;
import static com.icyicarus.multimedianote.Variables.TAG_FRAGMENT_CALENDAR;
import static com.icyicarus.multimedianote.Variables.TAG_FRAGMENT_FEEDBACK;
import static com.icyicarus.multimedianote.Variables.TAG_FRAGMENT_NOTE;

public class FragmentContainerView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.fab_toolbar_fab) FloatingActionButton floatingActionButton;
    @BindView(R.id.fab_toolbar) FABToolbarLayout fabToolbarLayout;
    @BindView(R.id.button_add_audio) AppCompatButton buttonAddAudio;

    private MenuItem activeMenu;
    private boolean showToolbar = false;
    private File f;
    private FragmentAllNotes defaultFragment = null;
    private FragmentNote noteFragment = null;

    @OnClick({R.id.button_add_note, R.id.button_add_photo, R.id.button_add_video, R.id.button_add_audio})
    void fabClickListener(View v) {
        Uri uri;
        Intent i;
        switch (v.getId()) {
            case R.id.button_add_note:
                noteFragment = new FragmentNote();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, noteFragment).addToBackStack(null).commit();
                break;
            case R.id.button_add_photo:
                f = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
                uri = FileProvider.getUriForFile(getApplicationContext(), FILE_PROVIDER_AUTHORITIES, f);
                i = new Intent();
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, MEDIA_TYPE_PHOTO);
                break;
            case R.id.button_add_video:
                f = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".mp4");
                uri = FileProvider.getUriForFile(getApplicationContext(), FILE_PROVIDER_AUTHORITIES, f);
                i = new Intent();
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, MEDIA_TYPE_VIDEO);
                break;
            case R.id.button_add_audio:
                f = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".wav");
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                AndroidAudioRecorder.with(this)
                        .setFilePath(f.getAbsolutePath())
                        .setColor(color)
                        .setRequestCode(MEDIA_TYPE_AUDIO)
                        .setSource(AudioSource.MIC)
                        .setChannel(AudioChannel.STEREO)
                        .setSampleRate(AudioSampleRate.HZ_48000)
                        .setAutoStart(false)
                        .setKeepDisplayOn(true)
                        .recordFromFragment();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LeakCanary.isInAnalyzerProcess(this))
            return;
        LeakCanary.install(getApplication());

        setContentView(R.layout.view_user_interface);
        ButterKnife.bind(this);

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(getApplicationContext()).setDownsampleEnabled(true).build();
        Fresco.initialize(getApplicationContext(), config);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONT_AWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.drawer_layout), iconFont);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("All Notes");
//        SharedPreferences userPreferences = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = userPreferences.edit();
//        editor.putBoolean(SSD, userPreferences.getBoolean(SSD, false));
//        editor.putBoolean(CBG, userPreferences.getBoolean(CBG, false));
//        editor.putString(BGC, userPreferences.getString(BGC, "000000"));
//        editor.apply();
//        editor.commit();

        defaultFragment = new FragmentAllNotes();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, defaultFragment, TAG_FRAGMENT_ALL_NOTE).commit();

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
        processIntent(getIntent());

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabToolbarLayout.show();
                showToolbar = true;
            }
        });

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            buttonAddAudio.setVisibility(View.GONE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        Bundle bundle = intent.getBundleExtra(EXTRA_NOTE_DATA);
        if (bundle != null) {
            Fragment fragment = new FragmentNote();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, fragment, TAG_FRAGMENT_NOTE).addToBackStack(null).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (showToolbar) {
            showToolbar = false;
            fabToolbarLayout.hide();
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START))
                drawer.closeDrawer(GravityCompat.START);
            else {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    super.onBackPressed();
                } else {
                    getSupportFragmentManager().popBackStack();
                    noteFragment = null;
                    activeMenu = null;
                    navigationView.setCheckedItem(R.id.nav_all_notes);
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentCalendar(), TAG_FRAGMENT_CALENDAR).addToBackStack(null).commit();
                break;
            case R.id.nav_alarm:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentAlarm(), TAG_FRAGMENT_ALARM).addToBackStack(null).commit();
                break;
//            case R.id.nav_settings:
//                getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentSettings(), TAG_FRAGMENT_SETTINGS).addToBackStack(null).commit();
//                break;
            case R.id.nav_help_and_feedback:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentFeedback(), TAG_FRAGMENT_FEEDBACK).addToBackStack(null).commit();
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentAbout(), TAG_FRAGMENT_ABOUT).addToBackStack(null).commit();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showToolbar = false;
        fabToolbarLayout.hide();
        if (resultCode == RESULT_OK) {
            if (noteFragment == null) {
                AdapterNoteList adapter = (AdapterNoteList) defaultFragment.getNoteList().getAdapter();
                SQLiteDatabase readDatabase = (new NoteDB(this)).getReadableDatabase();
                SQLiteDatabase writeDatabase = (new NoteDB(this)).getWritableDatabase();
                String today = new SimpleDateFormat("yyyy-MM-dd%", Locale.getDefault()).format(new Date(System.currentTimeMillis()));
                Cursor c = readDatabase.query(NoteDB.TABLE_NAME_NOTES, null, "date like ?", new String[]{today}, null, null, null, null);
                int ownerId = -1;
                while (c.moveToNext())
                    ownerId = (int) c.getLong(c.getColumnIndex(NoteDB.COLUMN_ID));
                if (ownerId == -1) { // no today note
                    ContentValues cv = new ContentValues();
                    cv.put(NoteDB.COLUMN_NAME_NOTE_TITLE, "Quick Note");
                    cv.put(NoteDB.COLUMN_NAME_NOTE_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                    ownerId = (int) writeDatabase.insert(NoteDB.TABLE_NAME_NOTES, null, cv);
                    cv = new ContentValues();
                    cv.put(NoteDB.COLUMN_NAME_MEDIA_PATH, f.getAbsolutePath());
                    cv.put(NoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, ownerId);
                    writeDatabase.insert(NoteDB.TABLE_NAME_MEDIA, null, cv);
                    LinkedList<MediaContent> list = new LinkedList<>();
                    list.add(new MediaContent(f.getAbsolutePath()));
                    c = readDatabase.query(NoteDB.TABLE_NAME_NOTES, null, "_id=?", new String[]{ownerId + ""}, null, null, null, null);
                    c.moveToNext();
                    NoteContent newNote = new NoteContent(
                            ownerId,
                            c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_TITLE)),
                            c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_DATE)),
                            c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_CONTENT)),
                            list,
                            requestCode == MEDIA_TYPE_PHOTO ? f.getAbsolutePath() : null,
                            c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_LATITUDE)),
                            c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_LONGITUDE))
                    );
                    adapter.getNotes().add(newNote);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);
                } else { // have today note
                    ContentValues cv = new ContentValues();
                    cv.put(NoteDB.COLUMN_NAME_MEDIA_PATH, f.getAbsolutePath());
                    cv.put(NoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, ownerId);
                    writeDatabase.insert(NoteDB.TABLE_NAME_MEDIA, null, cv);
                    adapter.getNotes().get(adapter.getItemCount() - 1).getMediaList().add(new MediaContent(f.getAbsolutePath()));
                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                }
                c.close();
            } else {
                AdapterMediaList adapter = (AdapterMediaList) noteFragment.getMediaList().getAdapter();
                MediaContent media = new MediaContent(f.getAbsolutePath());
                adapter.getMediaList().add(media);
                adapter.notifyItemInserted(adapter.getMediaList().size());
                noteFragment.getOperationQueue().add(new OperationDetail(OperationDetail.OPERATION_ADD, media));
            }
        } else if (requestCode != MEDIA_TYPE_PHOTO && !f.delete())
            Logger.e("Failed to delete some file");
    }
}