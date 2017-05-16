package com.icyicarus.multimedianote.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;

import com.icyicarus.multimedianote.NoteDB;
import com.icyicarus.multimedianote.Variables;
import com.icyicarus.multimedianote.medialist.MediaContent;
import com.icyicarus.multimedianote.notelist.NoteContent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SQLiteDatabase readDatabase = (new NoteDB(context)).getReadableDatabase();
            Cursor c = readDatabase.query(NoteDB.TABLE_NAME_ALARM, null, null, null, null, null, null, null);
            Cursor cc = null, ccc = null;
            while (c.moveToNext()) {
                int alarmID = c.getInt(c.getColumnIndex(NoteDB.COLUMN_ID));
                int noteID = c.getInt(c.getColumnIndex(NoteDB.COLUMN_NAME_ALARM_NOTEID));
                cc = readDatabase.query(NoteDB.TABLE_NAME_NOTES, null, NoteDB.COLUMN_ID + "=?", new String[]{noteID + ""}, null, null, null, null);
                cc.moveToNext();
                ccc = readDatabase.query(NoteDB.TABLE_NAME_MEDIA, null, NoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{noteID + ""}, null, null, null, null);
                LinkedList<MediaContent> mediaList = new LinkedList<>();
                while (ccc.moveToNext())
                    mediaList.add(new MediaContent(ccc.getString(ccc.getColumnIndex(NoteDB.COLUMN_NAME_MEDIA_PATH))));
                NoteContent noteContent = new NoteContent(
                        cc.getInt(cc.getColumnIndex(NoteDB.COLUMN_ID)),
                        cc.getString(cc.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_TITLE)),
                        cc.getString(cc.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_DATE)),
                        cc.getString(cc.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_CONTENT)),
                        mediaList,
                        null,
                        cc.getString(cc.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_LATITUDE)),
                        cc.getString(cc.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_LONGITUDE))
                );
                Bundle bundle = new Bundle();
                bundle.putSerializable(Variables.EXTRA_NOTE_DATA, noteContent);
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(context, AlarmReceiver.class);
                i.putExtra(Variables.EXTRA_ALARM_ID, alarmID);
                i.putExtra(Variables.EXTRA_NOTE_DATA, bundle);
                PendingIntent pi = PendingIntent.getBroadcast(context, alarmID, i, 0);
                try {
                    Logger.e("register");
                    am.set(AlarmManager.RTC_WAKEUP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_ALARM_TIME))).getTime(), pi);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (ccc != null)
                ccc.close();
            if (cc != null)
                cc.close();
            c.close();
        }
    }
}
