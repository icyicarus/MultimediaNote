package icyicarus.gwu.com.multimedianote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;

/**
 * Created by IcarusXu on 3/3/2017.
 */

public class NoteContent {
    private int id;
    private String title;
    private String date;
    private String content;
    private String picturePath = "";
    private LinkedList<String> mediaList;

    public NoteContent(int id, String title, String date, String content) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public int getPicture() {
        return 0;
    }

    private Boolean checkMedia(int id, Context context) {
        NoteDB db = new NoteDB(context);
        SQLiteDatabase dbRead;
        dbRead = db.getReadableDatabase();

        Cursor c = dbRead.query(NoteDB.TABLE_NAME_MEDIA, null, NoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{id + ""}, null, null, null);
        while (c.moveToNext()) {
            String path = c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_MEDIA_PATH));
            mediaList.add(path);
            if (path.endsWith(".jpg") && this.picturePath.equals("")) {
                this.picturePath = path;
            }
        }
        c.close();
        return !this.picturePath.equals("");
    }
}
