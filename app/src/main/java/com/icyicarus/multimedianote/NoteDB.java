package com.icyicarus.multimedianote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public static final String COLUMN_ID = "_id";

    public static final String TABLE_NAME_NOTES = "notes";
    public static final String COLUMN_NAME_NOTE_TITLE = "title";
    public static final String COLUMN_NAME_NOTE_CONTENT = "content";
    public static final String COLUMN_NAME_NOTE_DATE = "date";
    public static final String COLUMN_NAME_NOTE_LATITUDE = "latitude";
    public static final String COLUMN_NAME_NOTE_LONGITUDE = "longitude";

    public static final String TABLE_NAME_MEDIA = "media";
    public static final String COLUMN_NAME_MEDIA_PATH = "path";
    public static final String COLUMN_NAME_MEDIA_OWNER_NOTE_ID = "owner";

    public static final String TABLE_NAME_ALARM = "alarm";
    public static final String COLUMN_NAME_ALARM_TITLE = "title";
    public static final String COLUMN_NAME_ALARM_TIME = "time";
    public static final String COLUMN_NAME_ALARM_NOTEID = "noteid";

    public NoteDB(Context context) {
        super(context, "notes", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME_NOTES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME_NOTE_TITLE + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_NOTE_CONTENT + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_NOTE_DATE + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_NOTE_LATITUDE + " TEXT NOT NULL DEFAULT \" \","
                + COLUMN_NAME_NOTE_LONGITUDE + " TEXT NOT NULL DEFAULT \" \""
                + ");");
        db.execSQL("CREATE TABLE " + TABLE_NAME_MEDIA + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME_MEDIA_PATH + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_MEDIA_OWNER_NOTE_ID + " INTEGER NOT NULL DEFAULT 0"
                + ");");
        db.execSQL("CREATE TABLE " + TABLE_NAME_ALARM + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME_ALARM_TITLE + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_ALARM_TIME + " TEXT NOT NULL DEFAULT \"\","
                + COLUMN_NAME_ALARM_NOTEID + " INTEGER NOT NULL DEFAULT \"\""
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
