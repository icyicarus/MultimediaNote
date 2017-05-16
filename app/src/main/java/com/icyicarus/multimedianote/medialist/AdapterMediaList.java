package com.icyicarus.multimedianote.medialist;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icyicarus.multimedianote.R;
import com.icyicarus.multimedianote.Variables;

import java.io.File;
import java.util.List;

public class AdapterMediaList extends RecyclerView.Adapter<ViewHolderMediaList> {

    private Context context;
    private List<MediaContent> mediaList;
    private ClickMediaListener clickListener = null;
    private DeleteMediaListener deleteListener = null;

    public AdapterMediaList(List<MediaContent> mediaList) {
        this.mediaList = mediaList;
    }

    @Override
    public ViewHolderMediaList onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolderMediaList(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_media_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolderMediaList holder, int position) {
        final MediaContent mediaContent = mediaList.get(position);
        Uri uri;
        if (mediaContent.type == Variables.MEDIA_TYPE_PHOTO)
            uri = FileProvider.getUriForFile(context, Variables.FILE_PROVIDER_AUTHORITIES, new File(mediaContent.path));
        else if (mediaContent.type == Variables.MEDIA_TYPE_VIDEO)
            uri = Uri.parse("res://" + context.getPackageName() + "/" + R.drawable.img_video);
        else
            uri = Uri.parse("res://" + context.getPackageName() + "/" + R.drawable.img_audio);
        holder.mediaCellImage.setImageURI(uri);
        String[] pathSplit = mediaContent.path.split("/");
        holder.mediaCellPath.setText(pathSplit[pathSplit.length - 1]);
        holder.mediaCellContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onMediaClickListener(mediaContent);
            }
        });

        holder.mediaCellContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteListener.onMediaDeleteListener(mediaContent, holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public interface ClickMediaListener {
        void onMediaClickListener(MediaContent mediaContent);
    }

    public void setOnMediaClickListener(ClickMediaListener listener) {
        this.clickListener = listener;
    }

    public interface DeleteMediaListener {
        void onMediaDeleteListener(MediaContent media, int position);
    }

    public void setOnMediaDeleteListener(DeleteMediaListener listener) {
        this.deleteListener = listener;
    }

    public List<MediaContent> getMediaList() {
        return mediaList;
    }
}
