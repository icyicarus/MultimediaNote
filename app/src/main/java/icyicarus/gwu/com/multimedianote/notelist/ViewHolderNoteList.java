package icyicarus.gwu.com.multimedianote.notelist;

import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import icyicarus.gwu.com.multimedianote.FontManager;
import icyicarus.gwu.com.multimedianote.R;

class ViewHolderNoteList extends RecyclerView.ViewHolder {
    @BindView(R.id.note_content_block) LinearLayoutCompat noteContentBlock;
    @BindView(R.id.note_photo) SimpleDraweeView notePhoto;
    @BindView(R.id.note_title) AppCompatTextView noteTitle;
    @BindView(R.id.note_date) AppCompatTextView noteDate;
    @BindView(R.id.note_description) AppCompatTextView noteDescription;
    @BindView(R.id.button_share) AppCompatButton buttonShare;
    @BindView(R.id.button_delete) AppCompatButton buttonDelete;
    @BindView(R.id.button_set_alarm) AppCompatButton buttonAlarm;
    @BindView(R.id.button_edit) AppCompatButton buttonEdit;

    ViewHolderNoteList(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        Typeface iconFont = FontManager.getTypeface(itemView.getContext(), FontManager.FONT_AWESOME);
        FontManager.markAsIconContainer(itemView, iconFont);
    }
}
