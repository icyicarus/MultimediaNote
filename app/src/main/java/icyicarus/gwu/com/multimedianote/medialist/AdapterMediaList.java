package icyicarus.gwu.com.multimedianote.medialist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.List;

import icyicarus.gwu.com.multimedianote.R;
import icyicarus.gwu.com.multimedianote.Variables;

/**
 * Created by IcarusXu on 4/4/2017.
 */

public class AdapterMediaList extends RecyclerView.Adapter<ViewHolderMediaList> {

    private Context context;
    private List<MediaListCellData> mediaList;
    private deleteMediaListener deleteListener = null;

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
    public void onBindViewHolder(final ViewHolderMediaList holder, final int position) {
        final MediaListCellData mediaListCellData = mediaList.get(position);
        Uri uri;
        if (mediaListCellData.type == Variables.MEDIA_TYPE_PHOTO)
            uri = FileProvider.getUriForFile(context, Variables.FILE_PROVIDER_AUTHORITIES, new File(mediaListCellData.path));
        else if (mediaListCellData.type == Variables.MEDIA_TYPE_VIDEO)
            uri = Uri.parse("res://" + context.getPackageName() + "/" + R.id.button_note_add_video);
        else
            uri = Uri.parse("res://" + context.getPackageName() + "/" + R.id.button_note_add_audio);
        holder.mediaCellImage.setImageURI(uri);
        String[] pathSplit = mediaListCellData.path.split("/");
        holder.mediaCellPath.setText(pathSplit[pathSplit.length - 1]);
        holder.mediaCellContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(context, Variables.FILE_PROVIDER_AUTHORITIES, new File(mediaListCellData.path));
                switch (mediaListCellData.type) {
                    case Variables.MEDIA_TYPE_PHOTO:
                        i.setDataAndType(uri, "image/jpg");
                        break;
                    case Variables.MEDIA_TYPE_VIDEO:
                        i.setDataAndType(uri, "video/mp4");
                        break;
                    case Variables.MEDIA_TYPE_AUDIO:
                        i.setDataAndType(uri, "audio/wav");
                        break;
                    default:
                        break;
                }
                context.startActivity(i);
            }
        });

        holder.mediaCellContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteListener.onMediaDeleteListener(mediaListCellData, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public interface deleteMediaListener {
        void onMediaDeleteListener(MediaListCellData media, int position);
    }

    public void setOnMediaDeleteListener(deleteMediaListener listener) {
        this.deleteListener = listener;
    }
}
