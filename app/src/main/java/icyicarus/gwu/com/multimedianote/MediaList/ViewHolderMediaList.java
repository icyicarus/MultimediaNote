package icyicarus.gwu.com.multimedianote.MediaList;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import icyicarus.gwu.com.multimedianote.R;

/**
 * Created by IcarusXu on 4/4/2017.
 */

public class ViewHolderMediaList extends RecyclerView.ViewHolder {

    @BindView(R.id.media_cell_container) LinearLayoutCompat mediaCellContainer;
    @BindView(R.id.media_cell_image) AppCompatImageView mediaCellImage;
    @BindView(R.id.media_cell_path) AppCompatTextView mediaCellPath;

    private MediaListCellData media;

    public ViewHolderMediaList(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
