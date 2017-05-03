package icyicarus.gwu.com.multimedianote.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import icyicarus.gwu.com.multimedianote.NoteDB;
import icyicarus.gwu.com.multimedianote.R;
import icyicarus.gwu.com.multimedianote.alarmlist.AdapterAlarmList;
import icyicarus.gwu.com.multimedianote.alarmlist.AlarmContent;
import icyicarus.gwu.com.multimedianote.receivers.AlarmReceiver;

public class FragmentAlarm extends Fragment {

    @BindView(R.id.alarm_list) RecyclerView alarmList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alarm, container, false);
        ButterKnife.bind(this, v);

        final ArrayList<AlarmContent> query = new ArrayList<>();
        SQLiteDatabase readDatabase = (new NoteDB(getContext())).getReadableDatabase();
        Cursor c = readDatabase.query(NoteDB.TABLE_NAME_ALARM, null, null, null, null, null, null, null);
        while (c.moveToNext()) {
            query.add(new AlarmContent(
                    c.getInt(c.getColumnIndex(NoteDB.COLUMN_ID)),
                    c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_ALARM_TITLE)),
                    c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_ALARM_TIME)),
                    c.getInt(c.getColumnIndex(NoteDB.COLUMN_NAME_ALARM_NOTEID))
            ));
        }
        c.close();
        readDatabase.close();

        alarmList.setLayoutManager(new LinearLayoutManager(getActivity()));
        final AdapterAlarmList adapterAlarmList = new AdapterAlarmList(query);
        adapterAlarmList.setOnAlarmDeleteListener(new AdapterAlarmList.deleteAlarmListener() {
            @Override
            public void onAlarmDeleteListener(final AlarmContent alarmContent, final int position) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete this Alarm?")
                        .setMessage("This cannot be reversed")
                        .setCancelable(false)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(getContext(), AlarmReceiver.class);
                                AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                                PendingIntent pi = PendingIntent.getBroadcast(getContext(), alarmContent.getId(), i, 0);
                                try {
                                    am.set(AlarmManager.RTC_WAKEUP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(alarmContent.getTime()).getTime(), pi);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                adapterAlarmList.notifyItemRemoved(position);
                                adapterAlarmList.removeItem(position);
                                SQLiteDatabase writeDatabase = (new NoteDB(getContext())).getWritableDatabase();
                                writeDatabase.delete(NoteDB.TABLE_NAME_ALARM, NoteDB.COLUMN_ID + "=?", new String[]{alarmContent.getId() + ""});
                            }
                        })
                        .create()
                        .show();
            }
        });
        alarmList.setAdapter(adapterAlarmList);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Alarms");
    }
}
