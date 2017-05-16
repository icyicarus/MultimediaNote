package com.icyicarus.multimedianote.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.icyicarus.multimedianote.FontManager;
import com.icyicarus.multimedianote.NoteDB;
import com.icyicarus.multimedianote.R;
import com.icyicarus.multimedianote.Variables;
import com.icyicarus.multimedianote.medialist.MediaContent;
import com.icyicarus.multimedianote.notelist.AdapterNoteList;
import com.icyicarus.multimedianote.notelist.NoteContent;
import com.icyicarus.multimedianote.receivers.AlarmReceiver;
import com.orhanobut.logger.Logger;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.icyicarus.multimedianote.Variables.FILE_PROVIDER_AUTHORITIES;
import static com.icyicarus.multimedianote.Variables.TAG_FRAGMENT_NOTE;

public class FragmentAllNotes extends Fragment {

    @BindView(R.id.note_list) RecyclerView noteList;

    private CalendarDay calendarDay;
    private String queryDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            calendarDay = getArguments().getParcelable(Variables.EXTRA_NOTE_DATE);
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

        Typeface iconFont = FontManager.getTypeface(getContext(), FontManager.FONT_AWESOME);
        FontManager.markAsIconContainer(v.findViewById(R.id.container_all_note), iconFont);

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
            LinkedList<MediaContent> mediaList = new LinkedList<>();
            cc = readDatabase.query(NoteDB.TABLE_NAME_MEDIA, null, "owner=?", new String[]{c.getInt(c.getColumnIndex(NoteDB.COLUMN_ID)) + ""}, null, null, null, null);
            while (cc.moveToNext()) {
                MediaContent media = new MediaContent(cc.getLong(cc.getColumnIndex(NoteDB.COLUMN_ID)), cc.getString(cc.getColumnIndex(NoteDB.COLUMN_NAME_MEDIA_PATH)));
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
        AdapterNoteList adapterNoteList = new AdapterNoteList(query);
        adapterNoteList.setOnNoteShareListener(new AdapterNoteList.ShareNoteListener() {
            @Override
            public void onNoteShareListener(final NoteContent noteContent) {
                LinkedList<MediaContent> medias = noteContent.getMediaList();
                final ArrayList<Uri> uris = new ArrayList<>();
                if (medias != null && !medias.isEmpty())
                    for (MediaContent data : medias)
                        uris.add(FileProvider.getUriForFile(getContext(), FILE_PROVIDER_AUTHORITIES, new File(data.path)));

                final EditText emailField = new EditText(getContext());
                emailField.setHint("email@example.com");
                emailField.setInputType(32);
                new AlertDialog.Builder(getContext()).setTitle("Share to:")
                        .setView(emailField, 50, 0, 50, 0)
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent emailIntent = new Intent();
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, noteContent.getTitle());
                                emailIntent.putExtra(Intent.EXTRA_TEXT, noteContent.getContent());
                                if (uris.isEmpty()) {
                                    emailIntent.setAction(Intent.ACTION_SENDTO);
                                    emailIntent.setData(Uri.parse("mailto:" + emailField.getText()));
                                } else {
                                    emailIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                    String[] emailRecipients = {String.valueOf(emailField.getText())};
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL, emailRecipients);
                                    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                                    emailIntent.setType("*/*");
                                }
                                startActivity(emailIntent);
                            }
                        })
                        .setNegativeButton("Cancel", null).show();
            }
        });
        adapterNoteList.setOnNoteAlarmListener(new AdapterNoteList.AlarmNoteListener() {
            @Override
            public void onNoteAlarmListener(final NoteContent noteContent) {
                final SQLiteDatabase writeDatabase = (new NoteDB(getContext())).getWritableDatabase();
                final StringBuilder sb = new StringBuilder();
                final ContentValues cv = new ContentValues();
                final Calendar calendar = Calendar.getInstance();
                final Bundle bundle = new Bundle();
                bundle.putSerializable(Variables.EXTRA_NOTE_DATA, noteContent);
                cv.put(NoteDB.COLUMN_NAME_ALARM_NOTEID, noteContent.getId());
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                sb.append(year).append("-").append(monthOfYear + 1).append("-").append(dayOfMonth).append(" ");
                                RadialTimePickerDialogFragment rdp = new RadialTimePickerDialogFragment()
                                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                                if (hourOfDay < calendar.get(Calendar.HOUR_OF_DAY)) {
                                                    new AlertDialog.Builder(getContext())
                                                            .setCancelable(false)
                                                            .setTitle("Time expired")
                                                            .setMessage("Cannot set time prior to current time, please reset")
                                                            .setPositiveButton("OK", null)
                                                            .create()
                                                            .show();
                                                } else {
                                                    sb.append(hourOfDay).append(":");
                                                    if (minute < 10)
                                                        sb.append(0);
                                                    sb.append(minute).append(":00");

                                                    cv.put(NoteDB.COLUMN_NAME_ALARM_TITLE, noteContent.getTitle());
                                                    cv.put(NoteDB.COLUMN_NAME_ALARM_TIME, sb.toString());
                                                    int alarmID = (int) writeDatabase.insert(NoteDB.TABLE_NAME_ALARM, null, cv);
                                                    AlarmManager am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                                                    Intent i = new Intent(getContext(), AlarmReceiver.class);
                                                    i.putExtra(Variables.EXTRA_ALARM_ID, alarmID);
                                                    i.putExtra(Variables.EXTRA_NOTE_DATA, bundle);

                                                    PendingIntent pi = PendingIntent.getBroadcast(getContext(), alarmID, i, 0);
                                                    try {
                                                        am.set(AlarmManager.RTC_WAKEUP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(sb.toString()).getTime(), pi);
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        })
                                        .setStartTime(new Date().getHours(), new Date().getMinutes());
                                rdp.show(getFragmentManager(), "Time Picker Fragment");
                            }
                        })
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setDateRange(new MonthAdapter.CalendarDay(), null);
                cdp.show(getFragmentManager(), "Date Picker Fragment");
            }
        });
        adapterNoteList.setOnNoteDeleteListener(new AdapterNoteList.DeleteNoteListener() {
            @Override
            public void onNoteDeleteListener(final NoteContent note, final int position) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete this note?")
                        .setMessage("This cannot be reversed")
                        .setCancelable(false)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                query.remove(note);
                                noteList.getAdapter().notifyItemRemoved(position);
                                deleteNote(note);
                            }
                        })
                        .create()
                        .show();
            }
        });
        adapterNoteList.setOnNoteEditListener(new AdapterNoteList.EditNoteListener() {
            @Override
            public void onNoteEditListener(NoteContent noteContent) {
                final Bundle bundle = new Bundle();
                bundle.putSerializable(Variables.EXTRA_NOTE_DATA, noteContent);
                Fragment fragment = new FragmentNote();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_user_interface, fragment, TAG_FRAGMENT_NOTE).addToBackStack(null).commit();
            }
        });
        noteList.setAdapter(adapterNoteList);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.button_add_note).setVisibility(View.VISIBLE);
        if (calendarDay != null)
            getActivity().setTitle("All Notes on " + calendarDay.getYear() + "-" + (calendarDay.getMonth() + 1) + "-" + calendarDay.getDay());
        else
            getActivity().setTitle("All Notes");
    }

    private void deleteNote(NoteContent note) {
        SQLiteDatabase writeDatabase = (new NoteDB(getContext())).getWritableDatabase();
        writeDatabase.delete(NoteDB.TABLE_NAME_NOTES, "_id=?", new String[]{note.getId() + ""});
        writeDatabase.delete(NoteDB.TABLE_NAME_MEDIA, "owner=?", new String[]{note.getId() + ""});
        LinkedList<MediaContent> mediaList = note.getMediaList();
        File file;
        for (MediaContent media : mediaList) {
            file = new File(media.path);
            if (!file.delete())
                Logger.e("Some media files are not deleted, please delete them manually");
        }
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Cursor c = writeDatabase.query(NoteDB.TABLE_NAME_ALARM, null, NoteDB.COLUMN_NAME_ALARM_NOTEID + "=?", new String[]{note.getId() + ""}, null, null, null, null);
        while (c.moveToNext()) {
            Intent i = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(getActivity().getApplicationContext(), c.getInt(c.getColumnIndex(NoteDB.COLUMN_ID)), i, 0);
            am.cancel(pi);
        }
        c.close();
    }

    public RecyclerView getNoteList() {
        return noteList;
    }
}
