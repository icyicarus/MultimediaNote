package icyicarus.gwu.com.multimedianote.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
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
import icyicarus.gwu.com.multimedianote.R;
import icyicarus.gwu.com.multimedianote.Variables;
import icyicarus.gwu.com.multimedianote.medialist.MediaContent;
import icyicarus.gwu.com.multimedianote.notelist.AdapterNoteList;
import icyicarus.gwu.com.multimedianote.notelist.NoteContent;
import icyicarus.gwu.com.multimedianote.receivers.AlarmReceiver;

import static android.app.Activity.RESULT_OK;
import static icyicarus.gwu.com.multimedianote.Variables.FILE_PROVIDER_AUTHORITIES;
import static icyicarus.gwu.com.multimedianote.Variables.MEDIA_TYPE_AUDIO;
import static icyicarus.gwu.com.multimedianote.Variables.MEDIA_TYPE_PHOTO;
import static icyicarus.gwu.com.multimedianote.Variables.MEDIA_TYPE_VIDEO;

public class FragmentAllNotes extends Fragment {

    @BindView(R.id.note_list) RecyclerView noteList;
    @BindView(R.id.snackbar_container_all_note) CoordinatorLayout snackbarContainer;

    private CalendarDay calendarDay;
    private String queryDate;
    private File f;

    @OnClick(R.id.clear)
    void clearButtonClick() {
        SQLiteDatabase writeDatabase = (new NoteDB(getContext())).getWritableDatabase();
        writeDatabase.delete(NoteDB.TABLE_NAME_NOTES, null, null);
        writeDatabase.delete(NoteDB.TABLE_NAME_MEDIA, null, null);
        ((AdapterNoteList) noteList.getAdapter()).getNotes().clear();
        noteList.getAdapter().notifyDataSetChanged();
    }

    @OnClick({R.id.button_all_note_add_note, R.id.button_all_note_add_photo, R.id.button_all_note_add_video, R.id.button_all_note_add_audio})
    void allNoteViewButtonClick(View v) {
        Uri uri;
        Intent i;
        switch (v.getId()) {
            case R.id.button_all_note_add_note:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, new FragmentNote()).addToBackStack(null).commit();
                break;
            case R.id.button_all_note_add_photo:
                f = new File(getContext().getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
                uri = FileProvider.getUriForFile(getContext(), FILE_PROVIDER_AUTHORITIES, f);
                i = new Intent();
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, MEDIA_TYPE_PHOTO);
                break;
            case R.id.button_all_note_add_video:
                f = new File(getContext().getExternalFilesDir(null), System.currentTimeMillis() + ".mp4");
                uri = FileProvider.getUriForFile(getContext(), FILE_PROVIDER_AUTHORITIES, f);
                i = new Intent();
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, MEDIA_TYPE_VIDEO);
                break;
            case R.id.button_all_note_add_audio:
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
            default:
                break;
        }
    }

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
                getFragmentManager().beginTransaction().replace(R.id.content_user_interface, fragment).addToBackStack(null).commit();
            }
        });
        noteList.setAdapter(adapterNoteList);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
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
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Cursor c = writeDatabase.query(NoteDB.TABLE_NAME_ALARM, null, NoteDB.COLUMN_NAME_ALARM_NOTEID + "=?", new String[]{note.getId() + ""}, null, null, null, null);
        while (c.moveToNext()) {
            Intent i = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(getActivity().getApplicationContext(), c.getInt(c.getColumnIndex(NoteDB.COLUMN_ID)), i, 0);
            am.cancel(pi);
        }
        c.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            SQLiteDatabase readDatabase = (new NoteDB(getContext())).getReadableDatabase();
            SQLiteDatabase writeDatabase = (new NoteDB(getContext())).getWritableDatabase();
            String today = new SimpleDateFormat("yyyy-MM-dd%", Locale.getDefault()).format(new Date(System.currentTimeMillis()));
            Cursor c = readDatabase.query(NoteDB.TABLE_NAME_NOTES, null, "date like ?", new String[]{today}, null, null, null, null);
            int ownerId = -1;
            while (c.moveToNext())
                ownerId = (int) c.getLong(c.getColumnIndex(NoteDB.COLUMN_ID));
            c.close();
            if (ownerId == -1) { // no today note
                ContentValues cv = new ContentValues();
                cv.put(NoteDB.COLUMN_NAME_NOTE_TITLE, "Quick Note");
                cv.put(NoteDB.COLUMN_NAME_NOTE_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                ownerId = (int) writeDatabase.insert(NoteDB.TABLE_NAME_NOTES, null, cv);
                cv = new ContentValues();
                cv.put(NoteDB.COLUMN_NAME_MEDIA_PATH, f.getAbsolutePath());
                cv.put(NoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, ownerId);
                writeDatabase.insert(NoteDB.TABLE_NAME_MEDIA, null, cv);
            } else { // have today note
                ContentValues cv = new ContentValues();
                cv.put(NoteDB.COLUMN_NAME_MEDIA_PATH, f.getAbsolutePath());
                cv.put(NoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID, ownerId);
                writeDatabase.insert(NoteDB.TABLE_NAME_MEDIA, null, cv);
            }
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
    }
}
