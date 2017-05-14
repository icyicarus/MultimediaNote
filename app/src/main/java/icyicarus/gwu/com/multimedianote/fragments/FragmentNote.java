package icyicarus.gwu.com.multimedianote.fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
import icyicarus.gwu.com.multimedianote.NoteDB;
import icyicarus.gwu.com.multimedianote.OperationDetail;
import icyicarus.gwu.com.multimedianote.R;
import icyicarus.gwu.com.multimedianote.Variables;
import icyicarus.gwu.com.multimedianote.medialist.AdapterMediaList;
import icyicarus.gwu.com.multimedianote.medialist.MediaContent;
import icyicarus.gwu.com.multimedianote.notelist.NoteContent;
import icyicarus.gwu.com.multimedianote.views.MapView;

import static android.app.Activity.RESULT_OK;
import static icyicarus.gwu.com.multimedianote.Variables.*;

public class FragmentNote extends Fragment {
    @BindView(R.id.fragment_note_edit_text_title) EditText fragmentNoteEditTextTitle;
    @BindView(R.id.fragment_note_edit_text_content) EditText fragmentNoteEditTextContent;
    @BindView(R.id.button_note_save) AppCompatButton buttonNoteSave;
    //    @BindView(R.id.button_note_add_photo) AppCompatButton buttonNoteAddPhoto;
//    @BindView(R.id.button_note_add_video) AppCompatButton buttonNoteAddVideo;
    @BindView(R.id.button_note_add_audio) AppCompatButton buttonNoteAddAudio;
    @BindView(R.id.button_location) AppCompatButton buttonLocation;
    @BindView(R.id.fragment_note_media_list) RecyclerView mediaList;
    @BindView(R.id.snackbar_container_note) CoordinatorLayout snackbarContainer;

    private LinkedList<MediaContent> mediaListData = null;
    private File f;
    private Boolean showOKButton = false;
    private LinkedList<OperationDetail> operationQueue = new LinkedList<>();
    private String latitude = " ";
    private String longitude = " ";
    private NoteContent noteData = null;
    private Snackbar snackbar = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("New Note");
        if (getArguments() != null) {
            noteData = (NoteContent) getArguments().getSerializable(Variables.EXTRA_NOTE_DATA);
            getActivity().setTitle(noteData.getTitle());
        }
        showOKButton = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(Variables.SOB, false);
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
            latitude = noteData.getLatitude();
            longitude = noteData.getLongitude();
            mediaListData = noteData.getMediaList();
        } else
            mediaListData = new LinkedList<>();

        mediaList.setLayoutManager(new LinearLayoutManager(getActivity()));
        AdapterMediaList adapterMediaList = new AdapterMediaList(mediaListData);
        adapterMediaList.setOnMediaClickListener(new AdapterMediaList.ClickMediaListener() {
            @Override
            public void onMediaClickListener(MediaContent mediaContent) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(getContext(), Variables.FILE_PROVIDER_AUTHORITIES, new File(mediaContent.path));
                switch (mediaContent.type) {
                    case Variables.MEDIA_TYPE_PHOTO:
                        i.setDataAndType(uri, "image/jpg");
                        break;
                    case Variables.MEDIA_TYPE_VIDEO:
                        i.setDataAndType(uri, "video/mp4");
                        break;
                    case Variables.MEDIA_TYPE_AUDIO:
                        i.setDataAndType(uri, "audio/wav");
                        break;
                    default:
                        break;
                }
                startActivity(i);
            }
        });
        adapterMediaList.setOnMediaDeleteListener(new AdapterMediaList.DeleteMediaListener() {
            @Override
            public void onMediaDeleteListener(final MediaContent media, final int position) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete this media?")
                        .setMessage("This can be reversed by not saving the note")
                        .setCancelable(false)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mediaListData.remove(media);
                                mediaList.getAdapter().notifyItemRemoved(position);
                                operationQueue.add(new OperationDetail(OperationDetail.OPERATION_DEL, media));
                            }
                        })
                        .create()
                        .show();
            }
        });
        mediaList.setAdapter(adapterMediaList);

        if (!longitude.equals(" ") && !longitude.equals(" "))
            buttonLocation.setTextColor(getResources().getColor(R.color.royalBlue));

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            buttonLocation.setEnabled(false);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            buttonNoteAddAudio.setEnabled(false);

        if (!showOKButton)
            buttonNoteSave.setVisibility(View.GONE);

        snackbar = Snackbar.make(snackbarContainer, "", Snackbar.LENGTH_SHORT);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).setActionTextColor(Color.WHITE);
        return v;
    }

    private void saveNote() {
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
        clearQueue(writeDatabase);
        writeDatabase.close();
    }

    private void clearQueue(SQLiteDatabase writeDatabase) {
        while (operationQueue.peek() != null) {
            OperationDetail operationDetail = operationQueue.poll();
            MediaContent media = operationDetail.getMedia();
            if (operationDetail.getOperation() == OperationDetail.OPERATION_ADD) {
                ContentValues mediaContent = new ContentValues();
                mediaContent.put(NoteDB.COLUMN_NAME_MEDIA_PATH, media.path);
                mediaContent.put(NoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, noteData.getId());
                writeDatabase.insert(NoteDB.TABLE_NAME_MEDIA, null, mediaContent);
            } else if (operationDetail.getOperation() == OperationDetail.OPERATION_DEL) {
                writeDatabase.delete(NoteDB.TABLE_NAME_MEDIA, "_id=?", new String[]{media.id + ""});
                File file = new File(media.path);
                if (file.delete())
                    snackbar.setText("File deleted");
                else
                    snackbar.setText("File not deleted, please delete it manually");
                snackbar.show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (showOKButton) {
            for (OperationDetail operationDetail : operationQueue) {
                if (operationDetail.getOperation() == OperationDetail.OPERATION_ADD) {
                    File file = new File(operationDetail.getMedia().path);
                    if (file.delete())
                        snackbar.setText("File deleted");
                    else
                        snackbar.setText("File not deleted, please delete it manually");
                    snackbar.show();
                }
            }
        } else
            saveNote();
    }

    @OnClick({R.id.button_note_save, R.id.button_note_add_photo, R.id.button_note_add_video, R.id.button_note_add_audio, R.id.button_location})
    void noteViewButtonClick(View v) {
        Uri uri;
        Intent i;
        switch (v.getId()) {
            case R.id.button_note_save:
                saveNote();
//                getActivity().getSupportFragmentManager().popBackStack();
                snackbar.setText("Note saved");
                snackbar.show();
                break;
            case R.id.button_note_add_photo:
                f = new File(getContext().getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
                uri = FileProvider.getUriForFile(getContext(), FILE_PROVIDER_AUTHORITIES, f);
                i = new Intent();
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, MEDIA_TYPE_PHOTO);
                break;
            case R.id.button_note_add_video:
//                f = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".mp4");
                f = new File(getContext().getExternalFilesDir(null), System.currentTimeMillis() + ".mp4");
                uri = FileProvider.getUriForFile(getContext(), FILE_PROVIDER_AUTHORITIES, f);
                i = new Intent();
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, MEDIA_TYPE_VIDEO);
                break;
            case R.id.button_note_add_audio:
                f = new File(getContext().getExternalFilesDir(null), System.currentTimeMillis() + ".wav");
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
                    MediaContent media = new MediaContent(f.getAbsolutePath());
                    mediaListData.add(media);
                    mediaList.getAdapter().notifyItemInserted(mediaListData.size());
                    operationQueue.add(new OperationDetail(OperationDetail.OPERATION_ADD, media));
                } else {
                    if (requestCode != MEDIA_TYPE_PHOTO && !f.delete()) {
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
