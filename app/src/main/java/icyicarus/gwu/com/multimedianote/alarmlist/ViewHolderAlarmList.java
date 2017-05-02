package icyicarus.gwu.com.multimedianote.alarmlist;

import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import icyicarus.gwu.com.multimedianote.FontManager;
import icyicarus.gwu.com.multimedianote.R;

class ViewHolderAlarmList extends RecyclerView.ViewHolder {

    @BindView(R.id.alarm_title) AppCompatTextView alarmTitle;
    @BindView(R.id.alarm_date) AppCompatTextView alarmTime;
    @BindView(R.id.button_delete_alarm) AppCompatButton buttonDeleteAlarm;

    public ViewHolderAlarmList(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        Typeface iconFont = FontManager.getTypeface(itemView.getContext(), FontManager.FONT_AWESOME);
        FontManager.markAsIconContainer(itemView, iconFont);
    }
}
