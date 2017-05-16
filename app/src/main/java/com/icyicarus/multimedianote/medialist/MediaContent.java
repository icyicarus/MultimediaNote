package com.icyicarus.multimedianote.medialist;

import com.icyicarus.multimedianote.R;
import com.icyicarus.multimedianote.Variables;

import java.io.Serializable;

public class MediaContent implements Serializable {

    public int type = 0;
    public long id = -1;
    public String path = null;
    public int iconID = R.drawable.logo;

    public MediaContent(String path) {
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

    public MediaContent(long id, String path) {
        this(path);
        this.id = id;
    }
}
