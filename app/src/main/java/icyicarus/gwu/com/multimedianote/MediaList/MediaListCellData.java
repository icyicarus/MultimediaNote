package icyicarus.gwu.com.multimedianote.MediaList;

import icyicarus.gwu.com.multimedianote.R;

public class MediaListCellData {

    public int type = 0;
    public int id = -1;
    public String path = null;
    public int iconID = R.drawable.logo;

    public MediaListCellData(String path) {
        this.path = path;

        if (path.endsWith(".jpg")) {
            iconID = R.drawable.img_photo;
            type = 8001;
        } else if (path.endsWith(".mp4")) {
            iconID = R.drawable.img_video;
            type = 8002;
        } else if (path.endsWith(".wav")) {
            iconID = R.drawable.img_audio;
            type = 8003;
        }
    }

    public MediaListCellData(String path, int id) {
        this(path);
        this.id = id;
    }
}
