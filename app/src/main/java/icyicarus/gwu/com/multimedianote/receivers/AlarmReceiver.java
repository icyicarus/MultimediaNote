package icyicarus.gwu.com.multimedianote.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.NotificationCompat;

import icyicarus.gwu.com.multimedianote.NoteDB;
import icyicarus.gwu.com.multimedianote.Variables;
import icyicarus.gwu.com.multimedianote.notelist.NoteContent;
import icyicarus.gwu.com.multimedianote.views.FragmentContainerView;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NoteContent noteContent = (NoteContent) intent.getBundleExtra(Variables.EXTRA_NOTE_DATA).getSerializable(Variables.EXTRA_NOTE_DATA);

        intent.setClass(context, FragmentContainerView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int alarmID = intent.getIntExtra(Variables.EXTRA_ALARM_ID, -1);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        assert noteContent != null;
        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentTitle(noteContent.getTitle())
//                .setTicker("Ticker")
                .setWhen(System.currentTimeMillis())
                .setContentText(noteContent.getContent())
                .setContentIntent(PendingIntent.getActivity(context, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm);

        notificationManager.notify(alarmID, builder.build());

        SQLiteDatabase writeDatabase = (new NoteDB(context)).getWritableDatabase();
        writeDatabase.delete(NoteDB.TABLE_NAME_ALARM, NoteDB.COLUMN_ID + "=?", new String[]{alarmID + ""});
    }
}