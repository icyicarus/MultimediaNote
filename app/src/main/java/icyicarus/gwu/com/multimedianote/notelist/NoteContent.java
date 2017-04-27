package icyicarus.gwu.com.multimedianote.notelist;

import java.io.Serializable;
import java.util.LinkedList;

import icyicarus.gwu.com.multimedianote.medialist.MediaListCellData;

public class NoteContent implements Serializable {
    private long id;
    private String title;
    private String date;
    private String content;
    private String picturePath;
    private LinkedList<MediaListCellData> mediaList;
    private String latitude;
    private String longitude;

    public NoteContent(long id, String title, String date, String content, LinkedList<MediaListCellData> mediaList, String picturePath, String latitude, String longitude) {
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

    public LinkedList<MediaListCellData> getMediaList() {
        return mediaList;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPicturePath() {
        return picturePath;
    }
}
