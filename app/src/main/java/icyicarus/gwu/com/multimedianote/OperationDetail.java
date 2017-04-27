package icyicarus.gwu.com.multimedianote;

import icyicarus.gwu.com.multimedianote.medialist.MediaListCellData;

public class OperationDetail {

    public static final int OPERATION_DEL = 1;
    public static final int OPERATION_ADD = 2;

    private int operation = 0;
    private MediaListCellData media = null;

    public OperationDetail(int operation, MediaListCellData mediaListCellData) {
        this.operation = operation;
        this.media = mediaListCellData;
    }

    public int getOperation() {
        return operation;
    }

    public MediaListCellData getMedia() {
        return media;
    }
}
