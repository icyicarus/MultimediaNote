package icyicarus.gwu.com.multimedianote.Fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import icyicarus.gwu.com.multimedianote.FontManager;
import icyicarus.gwu.com.multimedianote.MapView;
import icyicarus.gwu.com.multimedianote.MediaList.AdapterMediaList;
import icyicarus.gwu.com.multimedianote.MediaList.MediaListCellData;
import icyicarus.gwu.com.multimedianote.NoteDB;
import icyicarus.gwu.com.multimedianote.NoteList.NoteContent;
import icyicarus.gwu.com.multimedianote.R;
import icyicarus.gwu.com.multimedianote.Variables;

import static android.app.Activity.RESULT_OK;
import static icyicarus.gwu.com.multimedianote.Variables.ADD_LOCATION;
import static icyicarus.gwu.com.multimedianote.Variables.EXTRA_NOTE_LATITUDE;
import static icyicarus.gwu.com.multimedianote.Variables.EXTRA_NOTE_LONGITUDE;
import static icyicarus.gwu.com.multimedianote.Variables.FILE_PROVIDER_AUTHORITIES;
import static icyicarus.gwu.com.multimedianote.Variables.MEDIA_TYPE_AUDIO;
import static icyicarus.gwu.com.multimedianote.Variables.MEDIA_TYPE_PHOTO;
import static icyicarus.gwu.com.multimedianote.Variables.MEDIA_TYPE_VIDEO;

public class FragmentNote extends Fragment {
    @BindView(R.id.fragment_note_edit_text_title) EditText fragmentNoteEditTextTitle;
    @BindView(R.id.fragment_note_edit_text_content) EditText fragmentNoteEditTextContent;
    @BindView(R.id.button_note_save) AppCompatButton buttonNoteSave;
    @BindView(R.id.button_note_add_photo) AppCompatButton buttonNoteAddPhoto;
    @BindView(R.id.button_note_add_video) AppCompatButton buttonNoteAddVideo;
    @BindView(R.id.button_note_add_audio) AppCompatButton buttonNoteAddAudio;
    @BindView(R.id.button_location) AppCompatButton buttonLocation;
    @BindView(R.id.fragment_note_media_list) RecyclerView mediaList;
    @BindView(R.id.snackbar_container_note) CoordinatorLayout snackbarContainer;

    private LinkedList<MediaListCellData> mediaListData = null;
    private File f;

    private String latitude = " ";
    private String longitude = " ";
    private NoteContent noteData = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            noteData = (NoteContent) getArguments().getSerializable("NOTE_DATA");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note, container, false);
        ButterKnife.bind(this, v);
        Typeface iconFont = FontManager.getTypeface(getContext(), FontManager.FONT_AWESOME);
        FontManager.markAsIconContainer(v.findViewById(R.id.container_note_view), iconFont);
        if (noteData != null) {
            ((EditText) v.findViewById(R.id.fragment_note_edit_text_title)).setText(noteData.getTitle());
            ((EditText) v.findViewById(R.id.fragment_note_edit_text_content)).setText(noteData.getContent());
            mediaListData = noteData.getMediaList();
        } else
            mediaListData = new LinkedList<>();

        mediaList.setLayoutManager(new LinearLayoutManager(getActivity()));
        AdapterMediaList adapterMediaList = new AdapterMediaList(getContext(), mediaListData);
        adapterMediaList.setOnMediaDeleteListener(new AdapterMediaList.deleteMediaListener() {
            @Override
            public void onMediaDeleteListener(MediaListCellData media, int position) {
                mediaListData.remove(media);
                mediaList.getAdapter().notifyItemRemoved(position);
                deleteMedia(media);
            }
        });
        mediaList.setAdapter(adapterMediaList);
        latitude = noteData.getLatitude();
        longitude = noteData.getLongitude();
        if (!longitude.equals(" ") && !longitude.equals(" "))
            buttonLocation.setTextColor(getResources().getColor(R.color.royalBlue));
        return v;
    }

    private void deleteMedia(MediaListCellData media) {
        SQLiteDatabase writeDatabase = (new NoteDB(getContext())).getWritableDatabase();
        writeDatabase.delete(NoteDB.TABLE_NAME_MEDIA, "_id=?", new String[]{media.id + ""});
        writeDatabase.close();
        File file = new File(media.path);
        final Snackbar snackbar = Snackbar.make(snackbarContainer, "", Snackbar.LENGTH_SHORT);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).setActionTextColor(Color.WHITE);
        if (file.delete())
            snackbar.setText("File deleted");
        else
            snackbar.setText("File not deleted, please delete it manually");
        snackbar.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SQLiteDatabase writeDatabase = (new NoteDB(getContext())).getWritableDatabase();
        ContentValues noteContent = new ContentValues();

        if (Objects.equals(fragmentNoteEditTextTitle.getText().toString(), ""))
            noteContent.put(NoteDB.COLUMN_NAME_NOTE_TITLE, "New Note");
        else
            noteContent.put(NoteDB.COLUMN_NAME_NOTE_TITLE, fragmentNoteEditTextTitle.getText().toString());
        noteContent.put(NoteDB.COLUMN_NAME_NOTE_DATE, noteData != null ? noteData.getDate() : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        noteContent.put(NoteDB.COLUMN_NAME_NOTE_CONTENT, fragmentNoteEditTextContent.getText().toString());
        noteContent.put(NoteDB.COLUMN_NAME_NOTE_LATITUDE, latitude);
        noteContent.put(NoteDB.COLUMN_NAME_NOTE_LONGITUDE, longitude);

        if (noteData == null) { // New
            noteData = new NoteContent(
                    writeDatabase.insert(NoteDB.TABLE_NAME_NOTES, null, noteContent),
                    noteContent.getAsString(NoteDB.COLUMN_NAME_NOTE_TITLE),
                    noteContent.getAsString(NoteDB.COLUMN_NAME_NOTE_DATE),
                    noteContent.getAsString(NoteDB.COLUMN_NAME_NOTE_CONTENT),
                    mediaListData,
                    null,
                    latitude,
                    longitude);
        } else { // Old
            noteContent.put(NoteDB.COLUMN_ID, noteData.getId());
            writeDatabase.update(NoteDB.TABLE_NAME_NOTES, noteContent, "_id=?", new String[]{noteData.getId() + ""});
        }

        if (mediaListData.size() > 0) {
            for (MediaListCellData media : mediaListData) {
                if (media.id == -1) {
                    ContentValues mediaContent = new ContentValues();
                    mediaContent.put(NoteDB.COLUMN_NAME_MEDIA_PATH, media.path);
                    mediaContent.put(NoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, noteData.getId());
                    writeDatabase.insert(NoteDB.TABLE_NAME_MEDIA, null, mediaContent);
                }
            }
        }

        writeDatabase.close();
    }

    @OnClick({R.id.button_note_save, R.id.button_note_add_photo, R.id.button_note_add_video, R.id.button_note_add_audio, R.id.button_location})
    void noteViewButtonClick(View v) {
        Uri uri;
        Intent i;
        switch (v.getId()) {
            case R.id.button_note_save:
                Logger.d("button note save");
                break;
            case R.id.button_note_add_photo:
                f = new File(getContext().getExternalFilesDir(null), "/media/" + System.currentTimeMillis() + ".jpg");
                uri = FileProvider.getUriForFile(getContext(), FILE_PROVIDER_AUTHORITIES, f);
                i = new Intent();
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, MEDIA_TYPE_PHOTO);
                break;
            case R.id.button_note_add_video:
//                f = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".mp4");
                f = new File(getContext().getExternalFilesDir(null), "/media/" + System.currentTimeMillis() + ".mp4");
                uri = FileProvider.getUriForFile(getContext(), FILE_PROVIDER_AUTHORITIES, f);
                i = new Intent();
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, MEDIA_TYPE_VIDEO);
                break;
            case R.id.button_note_add_audio:
//                Logger.d("button note add audio");
                f = new File(getContext().getExternalFilesDir(null), "/media/" + System.currentTimeMillis() + ".wav");
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                AndroidAudioRecorder.with(getActivity())
                        .setFilePath(f.getAbsolutePath())
                        .setColor(color)
                        .setRequestCode(MEDIA_TYPE_AUDIO)
                        .setSource(AudioSource.MIC)
                        .setChannel(AudioChannel.STEREO)
                        .setSampleRate(AudioSampleRate.HZ_48000)
                        .setAutoStart(false)
                        .setKeepDisplayOn(true)
                        .record();
                break;
            case R.id.button_location:
                i = new Intent(getContext(), MapView.class);
                i.putExtra(EXTRA_NOTE_LATITUDE, latitude);
                i.putExtra(EXTRA_NOTE_LONGITUDE, longitude);
                startActivityForResult(i, Variables.ADD_LOCATION);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_LOCATION:
                if (resultCode == RESULT_OK) {
                    latitude = data.getStringExtra("latitude");
                    longitude = data.getStringExtra("longitude");
                    buttonLocation.setTextColor(getResources().getColor(R.color.royalBlue));
                } else {
                    latitude = " ";
                    longitude = " ";
                    buttonLocation.setTextColor(Color.GRAY);
                }
                break;
            case MEDIA_TYPE_PHOTO:
            case MEDIA_TYPE_VIDEO:
            case MEDIA_TYPE_AUDIO:
                if (resultCode == RESULT_OK) {
                    Logger.e("11");
                    mediaListData.add(new MediaListCellData(f.getAbsolutePath()));
                    mediaList.getAdapter().notifyItemInserted(mediaListData.size());
                } else {
                    if (!f.delete()) {
                        final Snackbar snackbar = Snackbar.make(snackbarContainer, "", Snackbar.LENGTH_SHORT);
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        }).setActionTextColor(Color.WHITE);
                        snackbar.setText("File not deleted, please delete it manually");
                        snackbar.show();
                    }
                }
                break;
            default:
                break;
        }
    }
}
