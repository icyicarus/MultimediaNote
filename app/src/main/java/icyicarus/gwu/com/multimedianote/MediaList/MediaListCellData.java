package icyicarus.gwu.com.multimedianote.MediaList;

import java.io.Serializable;

import icyicarus.gwu.com.multimedianote.R;
import icyicarus.gwu.com.multimedianote.Variables;

public class MediaListCellData implements Serializable {

    public int type = 0;
    public long id = -1;
    public String path = null;
    public int iconID = R.drawable.logo;

    public MediaListCellData(String path) {
        this.path = path;

        if (path.endsWith(".jpg")) {
            iconID = R.drawable.img_photo;
            type = Variables.MEDIA_TYPE_PHOTO;
        } else if (path.endsWith(".mp4")) {
            iconID = R.drawable.img_video;
            type = Variables.MEDIA_TYPE_VIDEO;
        } else if (path.endsWith(".wav")) {
            iconID = R.drawable.img_audio;
            type = Variables.MEDIA_TYPE_AUDIO;
        }
    }

    public MediaListCellData(long id, String path) {
        this(path);
        this.id = id;
    }
}
