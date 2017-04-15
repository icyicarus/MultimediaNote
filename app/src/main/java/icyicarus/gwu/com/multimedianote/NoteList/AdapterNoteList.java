package icyicarus.gwu.com.multimedianote.NoteList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import icyicarus.gwu.com.multimedianote.Fragments.FragmentNote;
import icyicarus.gwu.com.multimedianote.MediaList.MediaListCellData;
import icyicarus.gwu.com.multimedianote.R;
import icyicarus.gwu.com.multimedianote.Variables;

/**
 * Created by IcarusXu on 3/3/2017.
 */

public class AdapterNoteList extends RecyclerView.Adapter<ViewHolderNoteList> {

    private Context context;
    private List<NoteContent> notes;
    private deleteNoteListener deleteListener;

    public AdapterNoteList(Context context, List<NoteContent> notes) {
        this.context = context;
        this.notes = notes;
    }

    public List<NoteContent> getNotes() {
        return notes;
    }

    @Override
    public ViewHolderNoteList onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_note_list, parent, false);
        return new ViewHolderNoteList(v, context);
    }

    @Override
    public void onBindViewHolder(final ViewHolderNoteList holder, final int position) {
        final NoteContent noteContent = notes.get(position);
        final Bundle bundle = new Bundle();
        bundle.putSerializable("NOTE_DATA", noteContent);
        if (noteContent.getPicturePath() != null) {
            Uri uri = FileProvider.getUriForFile(context, Variables.FILE_PROVIDER_AUTHORITIES, new File(noteContent.getPicturePath()));
            holder.notePhoto.setImageURI(uri);
        } else
            holder.notePhoto.setVisibility(View.GONE);
        holder.noteTitle.setText(noteContent.getTitle());
        holder.noteDate.setText(noteContent.getDate());
        holder.noteDescription.setText(noteContent.getContent());
        holder.setTag(noteContent);
        holder.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedList<MediaListCellData> medias = noteContent.getMediaList();
                final ArrayList<Uri> uris = new ArrayList<>();
                if (medias != null && !medias.isEmpty())
                    for (MediaListCellData data : medias)
                        uris.add(FileProvider.getUriForFile(context, Variables.FILE_PROVIDER_AUTHORITIES, new File(data.path)));

                final EditText emailField = new EditText(context);
                emailField.setHint("email@example.com");
                emailField.setInputType(32);
                new AlertDialog.Builder(context).setTitle("Share to:")
                        .setView(emailField, 50, 0, 50, 0)
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent emailIntent = new Intent();
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, noteContent.getTitle());
                                emailIntent.putExtra(Intent.EXTRA_TEXT, noteContent.getContent());
                                if (uris.isEmpty()) {
                                    emailIntent.setAction(Intent.ACTION_SENDTO);
                                    emailIntent.setData(Uri.parse("mailto:" + emailField.getText()));
                                } else {
                                    emailIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                    String[] emailRecipients = {String.valueOf(emailField.getText())};
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL, emailRecipients);
                                    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                                    emailIntent.setType("*/*");
                                }
                                context.startActivity(emailIntent);
                            }
                        })
                        .setNegativeButton("Cancel", null).show();
            }
        });
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onNoteDeleteListener(noteContent, position);
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

    public interface deleteNoteListener {
        void onNoteDeleteListener(NoteContent note, int position);
    }

    public void setOnNoteDeleteListener(deleteNoteListener listener) {
        this.deleteListener = listener;
    }
}
