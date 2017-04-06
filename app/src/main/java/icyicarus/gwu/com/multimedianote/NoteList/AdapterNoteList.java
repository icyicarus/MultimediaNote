package icyicarus.gwu.com.multimedianote.NoteList;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;

import icyicarus.gwu.com.multimedianote.Fragments.FragmentNote;
import icyicarus.gwu.com.multimedianote.R;
import icyicarus.gwu.com.multimedianote.Variables;

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
        final Bundle bundle = new Bundle();
        bundle.putSerializable("NOTE_DATA", noteContent);
        if (noteContent.getPicturePath() != null) {
            Uri uri = FileProvider.getUriForFile(context, Variables.FILE_PROVIDER_AUTHORITIES, new File(noteContent.getPicturePath()));
            holder.notePhoto.setImageURI(uri);
        }
        holder.noteTitle.setText(noteContent.getTitle());
        holder.noteDate.setText(noteContent.getDate());
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
        holder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FragmentNote();
                fragment.setArguments(bundle);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
