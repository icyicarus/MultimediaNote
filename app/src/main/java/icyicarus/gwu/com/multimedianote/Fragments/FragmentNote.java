package icyicarus.gwu.com.multimedianote.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icyicarus.gwu.com.multimedianote.BuildConfig;
import icyicarus.gwu.com.multimedianote.FontManager;
import icyicarus.gwu.com.multimedianote.MediaList.MediaAdapter;
import icyicarus.gwu.com.multimedianote.MediaList.MediaListCellData;
import icyicarus.gwu.com.multimedianote.NoteDB;
import icyicarus.gwu.com.multimedianote.R;

import static android.app.Activity.RESULT_OK;

public class FragmentNote extends Fragment {
    @BindView(R.id.fragment_note_edit_text_title) EditText fragmentNoteEditTextTitle;
    @BindView(R.id.fragment_note_edit_text_content) EditText fragmentNoteEditTextContent;
    @BindView(R.id.button_note_save) AppCompatButton buttonNoteSave;
    @BindView(R.id.button_note_add_photo) AppCompatButton buttonNoteAddPhoto;
    @BindView(R.id.button_note_add_video) AppCompatButton buttonNoteAddVideo;
    @BindView(R.id.button_note_add_audio) AppCompatButton buttonNoteAddAudio;
    @BindView(R.id.fragment_note_media_list) ListViewCompat mediaList;

    private final String FILE_PROVIDER_AUTHORITIES = BuildConfig.APPLICATION_ID + ".Fragments.FragmentNote";

    private MediaAdapter adapter;
    private File f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note, container, false);
        ButterKnife.bind(this, v);
        Typeface iconFont = FontManager.getTypeface(getContext(), FontManager.FONT_AWESOME);
        FontManager.markAsIconContainer(v.findViewById(R.id.container_note_view), iconFont);
        adapter = new MediaAdapter(getContext());
        mediaList.setAdapter(adapter);
        mediaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaListCellData data = adapter.getItem(position);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", new File(data.path));

                switch (data.type) {
                    case 8001:
//                        uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", new File(data.path));
                        i.setDataAndType(uri, "image/jpg");
//                        startActivity(i);
                        break;
                    case 8002:
//                        uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", new File(data.path));
                        i.setDataAndType(uri, "video/mp4");
//                        startActivity(i);
                        break;
                    case 8003:
//                        uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", new File(data.path));
                        i.setDataAndType(uri, "audio/wav");
//                        startActivity(i);
                        break;
                    default:
                        break;
                }
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        SQLiteDatabase writeDatabase;
        writeDatabase = (new NoteDB(getContext())).getWritableDatabase();
        ContentValues noteContent = new ContentValues();
        if (Objects.equals(fragmentNoteEditTextTitle.getText().toString(), "")) {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            noteContent.put(NoteDB.COLUMN_NAME_NOTE_TITLE, "Created on " + date);
        } else
            noteContent.put(NoteDB.COLUMN_NAME_NOTE_TITLE, fragmentNoteEditTextTitle.getText().toString());
        noteContent.put(NoteDB.COLUMN_NAME_NOTE_CONTENT, fragmentNoteEditTextContent.getText().toString());
        writeDatabase.insert(NoteDB.TABLE_NAME_NOTES, null, noteContent);
        writeDatabase.close();
    }

    @OnClick({R.id.button_note_save, R.id.button_note_add_photo, R.id.button_note_add_video, R.id.button_note_add_audio})
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
                startActivityForResult(i, 8001);
                break;
            case R.id.button_note_add_video:
                f = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
                uri = FileProvider.getUriForFile(getContext(), FILE_PROVIDER_AUTHORITIES, f);
                i = new Intent();
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, 8002);
                break;
            case R.id.button_note_add_audio:
                Logger.d("button note add audio");
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 8000:
                break;
            case 8001:
                if (resultCode != RESULT_OK) return;
                else {
                    adapter.add(new MediaListCellData(f.getAbsolutePath()));
                    adapter.notifyDataSetChanged();
                    Logger.e(adapter.getCount() + " ");
                }
                break;
            case 8002:
                break;
            default:
                break;
        }
    }
}
