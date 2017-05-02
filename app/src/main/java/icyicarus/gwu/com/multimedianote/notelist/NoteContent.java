package icyicarus.gwu.com.multimedianote.notelist;

import java.io.Serializable;
import java.util.LinkedList;

import icyicarus.gwu.com.multimedianote.medialist.MediaContent;

public class NoteContent implements Serializable {
    private long id;
    private String title;
    private String date;
    private String content;
    private String picturePath;
    private LinkedList<MediaContent> mediaList;
    private String latitude;
    private String longitude;

    public NoteContent(long id, String title, String date, String content, LinkedList<MediaContent> mediaList, String picturePath, String latitude, String longitude) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
        this.mediaList = mediaList;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public LinkedList<MediaContent> getMediaList() {
        return mediaList;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPicturePath() {
        return picturePath;
    }
}
