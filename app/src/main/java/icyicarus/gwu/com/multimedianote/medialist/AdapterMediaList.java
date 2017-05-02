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

public class AdapterMediaList extends RecyclerView.Adapter<ViewHolderMediaList> {

    private Context context;
    private List<MediaContent> mediaList;
    private deleteMediaListener deleteListener = null;

    public AdapterMediaList(Context context, List<MediaContent> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @Override
    public ViewHolderMediaList onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_media_list, parent, false);
        return new ViewHolderMediaList(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolderMediaList holder, final int position) {
        final MediaContent mediaContent = mediaList.get(position);
        Uri uri;
        if (mediaContent.type == Variables.MEDIA_TYPE_PHOTO)
            uri = FileProvider.getUriForFile(context, Variables.FILE_PROVIDER_AUTHORITIES, new File(mediaContent.path));
        else if (mediaContent.type == Variables.MEDIA_TYPE_VIDEO)
            uri = Uri.parse("res://" + context.getPackageName() + "/" + R.drawable.img_video);
//            uri = FileProvider.getUriForFile(context, Variables.FILE_PROVIDER_AUTHORITIES, new File(mediaContent.path));
        else
            uri = Uri.parse("res://" + context.getPackageName() + "/" + R.drawable.img_audio);
        holder.mediaCellImage.setImageURI(uri);
        String[] pathSplit = mediaContent.path.split("/");
        holder.mediaCellPath.setText(pathSplit[pathSplit.length - 1]);
        holder.mediaCellContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(context, Variables.FILE_PROVIDER_AUTHORITIES, new File(mediaContent.path));
                switch (mediaContent.type) {
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
                deleteListener.onMediaDeleteListener(mediaContent, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public interface deleteMediaListener {
        void onMediaDeleteListener(MediaContent media, int position);
    }

    public void setOnMediaDeleteListener(deleteMediaListener listener) {
        this.deleteListener = listener;
    }
}
