package icyicarus.gwu.com.multimedianote.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import icyicarus.gwu.com.multimedianote.NoteDB;
import icyicarus.gwu.com.multimedianote.R;

/**
 * Created by IcarusXu on 4/16/2017.
 */

public class FragmentCalendar extends Fragment implements OnDateSelectedListener {

    @BindView(R.id.calendar_view) MaterialCalendarView materialCalendarView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Calendar");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, v);

        SQLiteDatabase readDatabase = (new NoteDB(getContext())).getReadableDatabase();
        Cursor c = readDatabase.query(NoteDB.TABLE_NAME_NOTES, new String[]{NoteDB.COLUMN_NAME_NOTE_DATE}, null, null, null, null, null, null);
        String date;
        String[] dateSplit;
        HashSet<CalendarDay> dateSet = new HashSet<>();
        while (c.moveToNext()) {
            date = c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_DATE));
            dateSplit = date.split(" ");
            dateSplit = dateSplit[0].split("-");
            dateSet.add(new CalendarDay(Integer.valueOf(dateSplit[0]), Integer.valueOf(dateSplit[1]) - 1, Integer.valueOf(dateSplit[2])));
        }
        c.close();

        materialCalendarView.setOnDateChangedListener(this);
        Calendar instance = Calendar.getInstance();
        materialCalendarView.setSelectedDate(instance.getTime());
        materialCalendarView.addDecorators(
                new SelectionDecorator(this),
                new TodayDecorator(),
                new HaveNoteDateDecorator(dateSet)
        );

        return v;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("NOTE_DATE", date);
        Fragment fragment = new FragmentAllNotes();
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_user_interface, fragment, date.toString()).addToBackStack(null).commit();
    }

    private class SelectionDecorator implements DayViewDecorator {

        private Drawable drawable;

        SelectionDecorator(Fragment context) {
            drawable = context.getResources().getDrawable(R.drawable.my_selector);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return true;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(drawable);
        }
    }

    private class TodayDecorator implements DayViewDecorator {

        private CalendarDay date;

        TodayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new ForegroundColorSpan(Color.WHITE));
            view.addSpan(new RelativeSizeSpan(1.4f));
            view.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff616161")));
        }
    }

    private class HaveNoteDateDecorator implements DayViewDecorator {

        private HashSet<CalendarDay> dateSet;
        private CalendarDay today;

        HaveNoteDateDecorator(HashSet<CalendarDay> dateSet) {
            this.dateSet = dateSet;
            today = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dateSet.contains(day) && !day.equals(today);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.4f));
        }
    }
}
