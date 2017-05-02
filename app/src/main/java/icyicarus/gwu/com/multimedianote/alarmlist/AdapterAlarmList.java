package icyicarus.gwu.com.multimedianote.alarmlist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import icyicarus.gwu.com.multimedianote.R;

public class AdapterAlarmList extends RecyclerView.Adapter<ViewHolderAlarmList> {

    private List<AlarmContent> alarmList;
    private deleteAlarmListener deleteListener = null;

    public AdapterAlarmList(List<AlarmContent> alarmList) {
        this.alarmList = alarmList;
    }

    @Override
    public ViewHolderAlarmList onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_alarm_list, parent, false);
        return new ViewHolderAlarmList(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolderAlarmList holder, int position) {
        final AlarmContent alarmContent = alarmList.get(position);

        holder.alarmTitle.setText(alarmContent.getTitle());
        holder.alarmTime.setText(alarmContent.getTime());
        holder.buttonDeleteAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onAlarmDeleteListener(alarmContent, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public interface deleteAlarmListener {
        void onAlarmDeleteListener(AlarmContent alarmContent, int position);
    }

    public void setOnAlarmDeleteListener(deleteAlarmListener listener) {
        this.deleteListener = listener;
    }

    public void removeItem(int position) {
        alarmList.remove(position);
    }
}
