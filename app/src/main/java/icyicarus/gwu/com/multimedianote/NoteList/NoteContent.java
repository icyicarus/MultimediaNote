package icyicarus.gwu.com.multimedianote.NoteList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.LinkedList;

import icyicarus.gwu.com.multimedianote.MediaList.MediaListCellData;
import icyicarus.gwu.com.multimedianote.NoteDB;

/**
 * Created by IcarusXu on 3/3/2017.
 */

public class NoteContent implements Serializable {
    private long id;
    private String title;
    private String date;
    private String content;
    private String picturePath;
    private LinkedList<MediaListCellData> mediaList;

    public NoteContent(long id, String title, String date, String content, LinkedList<MediaListCellData> mediaList, String picturePath) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
        this.mediaList = mediaList;
        if (picturePath != null)
            this.picturePath = picturePath;
    }

    public long getId() {
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

    public LinkedList<MediaListCellData> getMediaList() {
        return mediaList;
    }

    public String getPicturePath() {
        return picturePath;
    }

    private Boolean checkMedia(int id, Context context) {
        NoteDB db = new NoteDB(context);
        SQLiteDatabase dbRead;
        dbRead = db.getReadableDatabase();

        Cursor c = dbRead.query(NoteDB.TABLE_NAME_MEDIA, null, NoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{id + ""}, null, null, null);
        while (c.moveToNext()) {
            String path = c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_MEDIA_PATH));
            mediaList.add(new MediaListCellData(path));
            if (path.endsWith(".jpg") && this.picturePath.equals("")) {
                this.picturePath = path;
            }
        }
        c.close();
        return !this.picturePath.equals("");
    }
}
