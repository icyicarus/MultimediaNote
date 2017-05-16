package com.icyicarus.multimedianote.notelist;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.List;

import com.icyicarus.multimedianote.R;
import com.icyicarus.multimedianote.Variables;

public class AdapterNoteList extends RecyclerView.Adapter<ViewHolderNoteList> {

    private Context context;
    private List<NoteContent> notes;
    private ShareNoteListener shareListener;
    private DeleteNoteListener deleteListener;
    private AlarmNoteListener alarmListener;
    private EditNoteListener editListener;

    public AdapterNoteList(List<NoteContent> notes) {
        this.notes = notes;
    }

    public List<NoteContent> getNotes() {
        return notes;
    }

    @Override
    public ViewHolderNoteList onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolderNoteList(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_note_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolderNoteList holder, int position) {
        final NoteContent noteContent = notes.get(position);
        if (noteContent.getPicturePath() != null) {
            Uri uri = FileProvider.getUriForFile(context, Variables.FILE_PROVIDER_AUTHORITIES, new File(noteContent.getPicturePath()));
            holder.notePhoto.setImageURI(uri);
        } else
            holder.notePhoto.setVisibility(View.GONE);
        holder.noteTitle.setText(noteContent.getTitle());
        holder.noteDate.setText(noteContent.getDate());
        holder.noteDescription.setText(noteContent.getContent());
        holder.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareListener.onNoteShareListener(noteContent);
            }
        });
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onNoteDeleteListener(noteContent, holder.getAdapterPosition());
            }
        });
        holder.buttonAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmListener.onNoteAlarmListener(noteContent);
            }
        });
        holder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editListener.onNoteEditListener(noteContent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public interface ShareNoteListener {
        void onNoteShareListener(NoteContent noteContent);
    }

    public void setOnNoteShareListener(ShareNoteListener listener) {
        this.shareListener = listener;
    }

    public interface DeleteNoteListener {
        void onNoteDeleteListener(NoteContent note, int position);
    }

    public void setOnNoteDeleteListener(DeleteNoteListener listener) {
        this.deleteListener = listener;
    }

    public interface AlarmNoteListener {
        void onNoteAlarmListener(NoteContent noteContent);
    }

    public void setOnNoteAlarmListener(AlarmNoteListener listener) {
        this.alarmListener = listener;
    }

    public interface EditNoteListener {
        void onNoteEditListener(NoteContent noteContent);
    }

    public void setOnNoteEditListener(EditNoteListener listener) {
        this.editListener = listener;
    }
}
