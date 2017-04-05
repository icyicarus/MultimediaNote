package icyicarus.gwu.com.multimedianote.MediaList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.util.List;

import icyicarus.gwu.com.multimedianote.R;

/**
 * Created by IcarusXu on 4/4/2017.
 */

public class AdapterMediaList extends RecyclerView.Adapter<ViewHolderMediaList> {

    private Context context;
    private List<MediaListCellData> mediaList;

    public AdapterMediaList(Context context, List<MediaListCellData> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @Override
    public ViewHolderMediaList onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cell_media_list, parent, false);
        return new ViewHolderMediaList(v);
    }

    @Override
    public void onBindViewHolder(ViewHolderMediaList holder, int position) {
        final MediaListCellData mediaListCellData = mediaList.get(position);
        holder.mediaCellImage.setImageBitmap(null);
        holder.mediaCellPath.setText(mediaListCellData.path);
        holder.mediaCellContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.e("cell click");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }
}
