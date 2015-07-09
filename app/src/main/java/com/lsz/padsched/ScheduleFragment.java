package com.lsz.padsched;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.lsz.padsched.Constants;

@SuppressLint("InflateParams")
public class ScheduleFragment extends Fragment {

    public static final long ANIMATION_DURATION = 500;

    private LayoutInflater myInflater;
    private View myView;
    private TableLayout eventTable;
    private TableRow headerRow;
    private SwipeRefreshLayout swipeLayout;

    private boolean animate;

    public TableLayout getEventTable() {
        return eventTable;
    }

    public SwipeRefreshLayout getSwipeLayout() {
        return swipeLayout;
    }

    public boolean getAnimate() {
        return animate;
    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.schedule_fragment, container, false);
        myInflater = inflater;
        eventTable = (TableLayout) myView.findViewById(R.id.scheduleTable);
        headerRow = (TableRow) myInflater.inflate(R.layout.schedule_header_row, null);
        swipeLayout = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_container);

        // Setup paznet link
        TextView sourceTextView = (TextView) myView.findViewById(R.id.sourceTextView);
        sourceTextView.setMovementMethod(LinkMovementMethod.getInstance());
        String linkSource = "Schedule Source: <a href=\"http://paznet.net\">Paznet</a>";
        sourceTextView.setText(Html.fromHtml(linkSource));

        swipeLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearAndUpdateSchedule();
                    }
                }, 0);

            }

        });
        swipeLayout.setRefreshing(true);
        clearAndUpdateSchedule();

        return myView;
    }

    private void clearAndUpdateSchedule() {
        animate = true;
        eventTable.animate().setDuration(ANIMATION_DURATION).translationY(-200).alpha(0.0f).setListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                eventTable.removeAllViewsInLayout();
                eventTable.setVisibility(View.GONE);
                eventTable.addView(headerRow);
                updateSchedule();
            }

        });
    }

    private void updateSchedule() {
        ScheduleTask eventScheduleTask = new ScheduleTask();
        eventScheduleTask.execute(this);
    }

    @SuppressLint("InflateParams")
    public void addScheduleRow(EventScheduleRow eventSchedule) {
        TableRow addRow;
        TextView[] groupDates;
        TextView[] groupTexts;
        LinearLayout[] groupLayouts;
        if (eventSchedule.event == Constants.NO_EVENT) {
            addRow = (TableRow) myInflater.inflate(R.layout.schedule_no_data_row, null);
            eventTable.addView(addRow);
            return;
        }

        addRow = (TableRow) myInflater.inflate(R.layout.schedule_row, null);
        ImageView img = (ImageView) addRow.findViewById(R.id.eventIcon);
        GregorianCalendar eventDate = eventSchedule.jpDate;
        Calendar currentTime = Calendar.getInstance();
        Calendar eventEndTime;

        TextView eventText = (TextView) addRow.findViewById(R.id.eventText);
        TextView groupAtext = (TextView) addRow.findViewById(R.id.groupATimeTextView);
        TextView groupADate = (TextView) addRow.findViewById(R.id.groupADateTextView);
        TextView groupBtext = (TextView) addRow.findViewById(R.id.groupBTimeTextView);
        TextView groupBDate = (TextView) addRow.findViewById(R.id.groupBDateTextView);
        TextView groupCtext = (TextView) addRow.findViewById(R.id.groupCTimeTextView);
        TextView groupCDate = (TextView) addRow.findViewById(R.id.groupCDateTextView);
        TextView groupDtext = (TextView) addRow.findViewById(R.id.groupDTimeTextView);
        TextView groupDDate = (TextView) addRow.findViewById(R.id.groupDDateTextView);
        TextView groupEtext = (TextView) addRow.findViewById(R.id.groupETimeTextView);
        TextView groupEDate = (TextView) addRow.findViewById(R.id.groupEDateTextView);

        LinearLayout layoutA = (LinearLayout) addRow.findViewById(R.id.groupACell);
        LinearLayout layoutB = (LinearLayout) addRow.findViewById(R.id.groupBCell);
        LinearLayout layoutC = (LinearLayout) addRow.findViewById(R.id.groupCCell);
        LinearLayout layoutD = (LinearLayout) addRow.findViewById(R.id.groupDCell);
        LinearLayout layoutE = (LinearLayout) addRow.findViewById(R.id.groupECell);

        groupDates = new TextView[] { groupADate, groupBDate, groupCDate, groupDDate, groupEDate };
        groupTexts = new TextView[] { groupAtext, groupBtext, groupCtext, groupDtext, groupEtext };
        groupLayouts = new LinearLayout[] { layoutA, layoutB, layoutC, layoutD, layoutE };

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma", Locale.ENGLISH);

        EventInfoObject eio = new EventInfoObject(eventSchedule.event);
        img.setImageDrawable(getResources().getDrawable(eio.getEventDrawable()));
        eventText.setText(getResources().getText(eio.getEventName()));

        for (int group = Constants.GROUP_A; group <= Constants.GROUP_E; group++) {
            if (eventSchedule.times[group] > 0) {
                eventDate.setTimeInMillis(eventSchedule.times[group]);
                groupDates[group].setText(dateFormat.format(eventDate.getTime()));
                groupTexts[group].setText(timeFormat.format(eventDate.getTime()).toLowerCase(Locale.ENGLISH));

                // Current event highlight
                eventEndTime = (Calendar) eventDate.clone();
                eventEndTime.add(Calendar.HOUR_OF_DAY, 1);

                if (currentTime.getTimeInMillis() >= eventDate.getTimeInMillis() && currentTime.getTimeInMillis() < eventEndTime.getTimeInMillis()) {
                    groupLayouts[group].setBackground(getResources().getDrawable(R.drawable.blue_cell_shape));
                } else if (currentTime.getTimeInMillis() > eventEndTime.getTimeInMillis()) {
                    groupLayouts[group].setBackground(getResources().getDrawable(R.drawable.grey_cell_shape));
                }

            }
        }

        eventTable.addView(addRow);
    }

}
