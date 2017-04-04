package icyicarus.gwu.com.multimedianote.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icyicarus.gwu.com.multimedianote.NoteContent;
import icyicarus.gwu.com.multimedianote.NoteDB;
import icyicarus.gwu.com.multimedianote.NoteList.AdapterNoteList;
import icyicarus.gwu.com.multimedianote.R;

/**
 * Created by Icarus on 1/1/2017.
 */

public class FragmentAllNotes extends Fragment {

    @BindView(R.id.button) AppCompatButton button;
    @BindView(R.id.clear) AppCompatButton clear;
    @BindView(R.id.note_list) RecyclerView noteList;

    @OnClick(R.id.button)
    void testButtonClick() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentNote()).addToBackStack(null).commit();
    }

    @OnClick(R.id.clear)
    void clearButtonClick() {
        SQLiteDatabase writeDatabase = (new NoteDB(getContext())).getWritableDatabase();
        writeDatabase.delete(NoteDB.TABLE_NAME_NOTES, null, null);
        noteList.getAdapter().notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("All Notes");

        View v = inflater.inflate(R.layout.fragment_all_notes, container, false);
        ButterKnife.bind(this, v);

        ArrayList<NoteContent> query = new ArrayList<>();
        SQLiteDatabase readDatabase = (new NoteDB(getContext())).getReadableDatabase();
        Cursor c = readDatabase.query(NoteDB.TABLE_NAME_NOTES, null, null, null, null, null, null, null);
        while (c.moveToNext()) {
//            String sb = c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_TITLE)) +
//                    "#*#" +
//                    c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_CONTENT));
            query.add(new NoteContent(
                    c.getInt(c.getColumnIndex(NoteDB.COLUMN_ID)),
                    c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_TITLE)),
                    c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_DATE)),
                    c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_CONTENT))
            ));
        }
        c.close();
        readDatabase.close();

        noteList.setLayoutManager(new LinearLayoutManager(getActivity()));
        noteList.setAdapter(new AdapterNoteList(getContext(), query));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
