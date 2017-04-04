package icyicarus.gwu.com.multimedianote.NoteList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.util.List;

import icyicarus.gwu.com.multimedianote.NoteContent;
import icyicarus.gwu.com.multimedianote.R;

/**
 * Created by IcarusXu on 3/3/2017.
 */

public class AdapterNoteList extends RecyclerView.Adapter<ViewHolderNoteList> {

    private Context context;
    private List<NoteContent> notes;

    public AdapterNoteList(Context context, List<NoteContent> notes) {
        this.context = context;
        this.notes = notes;
    }

    @Override
    public ViewHolderNoteList onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_note_list, parent, false);
        return new ViewHolderNoteList(v, context);
    }

    @Override
    public void onBindViewHolder(final ViewHolderNoteList holder, int position) {
        final NoteContent noteContent = notes.get(position);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), noteContent.getPicture(), getBitmapOption(4));
//        holder.notePhoto.setImageBitmap(bitmap);
        holder.noteTitle.setText(noteContent.getTitle());
//        holder.noteDate.setText(noteContent.getDate());
        holder.noteDescription.setText(noteContent.getContent());
        holder.setTag(noteContent);
        holder.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.e("share " + noteContent.getId() + " " + noteContent.getTitle() + " " + noteContent.getContent());
            }
        });
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.e("delete " + noteContent.getId() + " " + noteContent.getTitle() + " " + noteContent.getContent());
            }
        });
        holder.buttonAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.e("");
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    private BitmapFactory.Options getBitmapOption(int size) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        return options;
    }
}
