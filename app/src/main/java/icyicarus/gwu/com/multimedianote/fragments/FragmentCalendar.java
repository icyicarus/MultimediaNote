package icyicarus.gwu.com.multimedianote.fragments;

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

import icyicarus.gwu.com.multimedianote.R;

/**
 * Created by IcarusXu on 4/16/2017.
 */

public class FragmentCalendar extends Fragment implements OnDateSelectedListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Calendar");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        MaterialCalendarView materialCalendarView = (MaterialCalendarView) v.findViewById(R.id.calendar_view);

        materialCalendarView.setOnDateChangedListener(this);
        Calendar instance = Calendar.getInstance();
        materialCalendarView.setSelectedDate(instance.getTime());
        materialCalendarView.addDecorators(
                new SelectionDecorator(this),
                new TodayDecorator()
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

}
