package icyicarus.gwu.com.multimedianote.notelist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import icyicarus.gwu.com.multimedianote.NoteDB;
import icyicarus.gwu.com.multimedianote.R;
import icyicarus.gwu.com.multimedianote.Variables;
import icyicarus.gwu.com.multimedianote.fragments.FragmentNote;
import icyicarus.gwu.com.multimedianote.medialist.MediaContent;
import icyicarus.gwu.com.multimedianote.receivers.AlarmReceiver;

public class AdapterNoteList extends RecyclerView.Adapter<ViewHolderNoteList> {

    private Context context;
    private List<NoteContent> notes;
    private deleteNoteListener deleteListener;
    private FragmentManager fragmentManager;

    public AdapterNoteList(Context context, List<NoteContent> notes, FragmentManager fragmentManager) {
        this.context = context;
        this.notes = notes;
        this.fragmentManager = fragmentManager;
    }

    public List<NoteContent> getNotes() {
        return notes;
    }

    @Override
    public ViewHolderNoteList onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cell_note_list, parent, false);
        return new ViewHolderNoteList(v, context);
    }

    @Override
    public void onBindViewHolder(final ViewHolderNoteList holder, int position) {
        final NoteContent noteContent = notes.get(position);
        final Bundle bundle = new Bundle();
        bundle.putSerializable(Variables.EXTRA_NOTE_DATA, noteContent);
        if (noteContent.getPicturePath() != null) {
            Uri uri = FileProvider.getUriForFile(context, Variables.FILE_PROVIDER_AUTHORITIES, new File(noteContent.getPicturePath()));
            holder.notePhoto.setImageURI(uri);
        } else
            holder.notePhoto.setVisibility(View.GONE);
        holder.noteTitle.setText(noteContent.getTitle());
        holder.noteDate.setText(noteContent.getDate());
        holder.noteDescription.setText(noteContent.getContent());
        holder.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedList<MediaContent> medias = noteContent.getMediaList();
                final ArrayList<Uri> uris = new ArrayList<>();
                if (medias != null && !medias.isEmpty())
                    for (MediaContent data : medias)
                        uris.add(FileProvider.getUriForFile(context, Variables.FILE_PROVIDER_AUTHORITIES, new File(data.path)));

                final EditText emailField = new EditText(context);
                emailField.setHint("email@example.com");
                emailField.setInputType(32);
                new AlertDialog.Builder(context).setTitle("Share to:")
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
                                context.startActivity(emailIntent);
                            }
                        })
                        .setNegativeButton("Cancel", null).show();
            }
        });
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onNoteDeleteListener(noteContent, holder.getAdapterPosition());
            }
        });
        holder.buttonAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SQLiteDatabase writeDatabase = (new NoteDB(context)).getWritableDatabase();
                final StringBuilder sb = new StringBuilder();
                final ContentValues cv = new ContentValues();
                final Calendar calendar = Calendar.getInstance();
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
                                                    new AlertDialog.Builder(context)
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
                                                    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                                    Intent i = new Intent(context, AlarmReceiver.class);
                                                    i.putExtra(Variables.EXTRA_ALARM_ID, alarmID);
                                                    i.putExtra(Variables.EXTRA_NOTE_DATA, bundle);

                                                    PendingIntent pi = PendingIntent.getBroadcast(context, alarmID, i, 0);
                                                    try {
                                                        am.set(AlarmManager.RTC_WAKEUP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(sb.toString()).getTime(), pi);
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        })
                                        .setStartTime(new Date().getHours(), new Date().getMinutes());
                                rdp.show(fragmentManager, "Time Picker Fragment");
                            }
                        })
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setDateRange(new MonthAdapter.CalendarDay(), null);
                cdp.show(fragmentManager, "Date Picker Fragment");
            }
        });
        holder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FragmentNote();
                fragment.setArguments(bundle);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public interface deleteNoteListener {
        void onNoteDeleteListener(NoteContent note, int position);
    }

    public void setOnNoteDeleteListener(deleteNoteListener listener) {
        this.deleteListener = listener;
    }
}
