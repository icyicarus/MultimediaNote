package icyicarus.gwu.com.multimedianote.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icyicarus.gwu.com.multimedianote.NoteDB;
import icyicarus.gwu.com.multimedianote.R;
import icyicarus.gwu.com.multimedianote.Variables;
import icyicarus.gwu.com.multimedianote.medialist.MediaListCellData;
import icyicarus.gwu.com.multimedianote.notelist.AdapterNoteList;
import icyicarus.gwu.com.multimedianote.notelist.NoteContent;

/**
 * Created by Icarus on 1/1/2017.
 */

public class FragmentAllNotes extends Fragment {

    @BindView(R.id.button) AppCompatButton button;
    @BindView(R.id.clear) AppCompatButton clear;
    @BindView(R.id.note_list) RecyclerView noteList;
    @BindView(R.id.snackbar_container_all_note) CoordinatorLayout snackbarContainer;

    private CalendarDay calendarDay;
    private String queryDate;

    @OnClick(R.id.button)
    void testButtonClick() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentNote()).addToBackStack(null).commit();
    }

    @OnClick(R.id.clear)
    void clearButtonClick() {
        SQLiteDatabase writeDatabase = (new NoteDB(getContext())).getWritableDatabase();
        writeDatabase.delete(NoteDB.TABLE_NAME_NOTES, null, null);
        writeDatabase.delete(NoteDB.TABLE_NAME_MEDIA, null, null);
        ((AdapterNoteList) noteList.getAdapter()).getNotes().clear();
        noteList.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            calendarDay = getArguments().getParcelable("NOTE_DATE");
        if (calendarDay != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(calendarDay.getYear()).append("-");
            if (calendarDay.getMonth() < 9)
                sb.append(0);
            sb.append(calendarDay.getMonth() + 1);
            if (calendarDay.getDay() < 9)
                sb.append(0);
            sb.append("-");
            sb.append(calendarDay.getDay());
            sb.append("%");
            queryDate = sb.toString();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_notes, container, false);
        ButterKnife.bind(this, v);

        final ArrayList<NoteContent> query = new ArrayList<>();
        SQLiteDatabase readDatabase = (new NoteDB(getContext())).getReadableDatabase();
        Cursor c;
        if (calendarDay != null)
            c = readDatabase.query(NoteDB.TABLE_NAME_NOTES, null, "date like ?", new String[]{queryDate}, null, null, null, null);
        else
            c = readDatabase.query(NoteDB.TABLE_NAME_NOTES, null, null, null, null, null, null, null);
        Cursor cc;
        while (c.moveToNext()) {
            String picturePath = null;
            LinkedList<MediaListCellData> mediaList = new LinkedList<>();
            cc = readDatabase.query(NoteDB.TABLE_NAME_MEDIA, null, "owner=?", new String[]{c.getInt(c.getColumnIndex(NoteDB.COLUMN_ID)) + ""}, null, null, null, null);
            while (cc.moveToNext()) {
                MediaListCellData media = new MediaListCellData(cc.getLong(cc.getColumnIndex(NoteDB.COLUMN_ID)), cc.getString(cc.getColumnIndex(NoteDB.COLUMN_NAME_MEDIA_PATH)));
                if (media.type == Variables.MEDIA_TYPE_PHOTO)
                    picturePath = media.path;
                mediaList.add(media);
            }
            query.add(new NoteContent(
                    c.getLong(c.getColumnIndex(NoteDB.COLUMN_ID)),
                    c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_TITLE)),
                    c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_DATE)),
                    c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_CONTENT)),
                    mediaList,
                    picturePath,
                    c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_LATITUDE)),
                    c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_LONGITUDE))
            ));
            cc.close();
        }
        c.close();
        readDatabase.close();

        noteList.setLayoutManager(new LinearLayoutManager(getActivity()));
        AdapterNoteList adapterNoteList = new AdapterNoteList(getContext(), query, getActivity().getSupportFragmentManager());
        adapterNoteList.setOnNoteDeleteListener(new AdapterNoteList.deleteNoteListener() {
            @Override
            public void onNoteDeleteListener(NoteContent note, int position) {
                query.remove(note);
                noteList.getAdapter().notifyItemRemoved(position);
                deleteNote(note);
            }
        });
        noteList.setAdapter(adapterNoteList);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (calendarDay != null)
            getActivity().setTitle("All Notes on " + calendarDay.getYear() + " " + (calendarDay.getMonth() + 1) + " " + calendarDay.getDay());
        else
            getActivity().setTitle("All Notes");
    }

    private void deleteNote(NoteContent note) {
        SQLiteDatabase writeDatabase = (new NoteDB(getContext())).getWritableDatabase();
        writeDatabase.delete(NoteDB.TABLE_NAME_NOTES, "_id=?", new String[]{note.getId() + ""});
        writeDatabase.delete(NoteDB.TABLE_NAME_MEDIA, "owner=?", new String[]{note.getId() + ""});
        writeDatabase.close();
        LinkedList<MediaListCellData> mediaList = note.getMediaList();
        File file;
        for (MediaListCellData media : mediaList) {
            file = new File(media.path);
            if (!file.delete()) {
                final Snackbar snackbar = Snackbar.make(snackbarContainer, "", Snackbar.LENGTH_SHORT);
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                }).setActionTextColor(Color.WHITE);
                snackbar.setText("Some media files are not deleted, please delete them manually");
                snackbar.show();
            }
        }
    }
}
