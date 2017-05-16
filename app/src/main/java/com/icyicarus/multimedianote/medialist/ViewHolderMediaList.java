package com.icyicarus.multimedianote.medialist;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.icyicarus.multimedianote.R;

import butterknife.BindView;
import butterknife.ButterKnife;

class ViewHolderMediaList extends RecyclerView.ViewHolder {

    @BindView(R.id.media_cell_container) LinearLayoutCompat mediaCellContainer;
    @BindView(R.id.media_cell_image) SimpleDraweeView mediaCellImage;
    @BindView(R.id.media_cell_path) AppCompatTextView mediaCellPath;

    ViewHolderMediaList(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
