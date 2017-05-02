package icyicarus.gwu.com.multimedianote;

import icyicarus.gwu.com.multimedianote.medialist.MediaContent;

public class OperationDetail {

    public static final int OPERATION_DEL = 1;
    public static final int OPERATION_ADD = 2;

    private int operation = 0;
    private MediaContent media = null;

    public OperationDetail(int operation, MediaContent mediaContent) {
        this.operation = operation;
        this.media = mediaContent;
    }

    public int getOperation() {
        return operation;
    }

    public MediaContent getMedia() {
        return media;
    }
}
