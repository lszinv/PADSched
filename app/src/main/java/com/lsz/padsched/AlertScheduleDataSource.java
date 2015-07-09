package com.lsz.padsched;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AlertScheduleDataSource {
    private SQLiteDatabase padScheduleDB;
    private PADSchedHelper padScheduleHelper;
    private Context parentActivity;

    public AlertScheduleDataSource(Context context) {
        padScheduleHelper = new PADSchedHelper(context);
        parentActivity = context;
        open();
    }

    private void open() throws SQLException {
        padScheduleDB = padScheduleHelper.getWritableDatabase();
    }

    public void close() {
        padScheduleDB.close();
    }

    public ArrayList<AlertScheduleObject> buildAlertSchedule() {
        // add schedule as alert schedule objects, then sort them by time
        ScheduleDataSource scheduleDS = new ScheduleDataSource(parentActivity);
        ArrayList<EventScheduleRow> schedule;
        ArrayList<AlertScheduleObject> alertSchedule = new ArrayList<AlertScheduleObject>();
        ContentValues values = new ContentValues();
        schedule = scheduleDS.getSchedule();
        scheduleDS.close();
        AlertScheduleObject curreAlertObject;
        for (EventScheduleRow esr : schedule) {
            for (int group = Constants.GROUP_A; group <= Constants.GROUP_E; group++) {
                curreAlertObject = new AlertScheduleObject(esr.event, group, esr.times[group]);
                alertSchedule.add(curreAlertObject);
            }
        }

        Collections.sort(alertSchedule, new Comparator<AlertScheduleObject>() {
            @Override
            public int compare(AlertScheduleObject lhs, AlertScheduleObject rhs) {
                int result = (int) (lhs.getTime() - rhs.getTime());
                return result;
            }

        });
        clearAlertSchedule();

        for (AlertScheduleObject aso : alertSchedule) {
            values.put(PADSchedHelper.ALERT_SCHEDULE_TABLE_COLUMNS[0], aso.getGroup());
            values.put(PADSchedHelper.ALERT_SCHEDULE_TABLE_COLUMNS[1], aso.getEvent());
            values.put(PADSchedHelper.ALERT_SCHEDULE_TABLE_COLUMNS[2], aso.getTime());
            padScheduleDB.insert(PADSchedHelper.ALERT_SCHEDULE_TABLE_NAME, null, values);
        }

        values = new ContentValues();
        values.put(PADSchedHelper.ALERT_HISTORY_TABLE_COLUMNS[PADSchedHelper.ALERT_HISTORY_HISTORY_COLUMN], getTodayJpDate());
        padScheduleHelper.clearAlertHistorySchedule(padScheduleDB);
        padScheduleDB.insert(PADSchedHelper.ALERT_HISTORY_TABLE_NAME, null, values);
        return alertSchedule;

    }

    private long getTodayJpDate() {
        Calendar jpToday;
        jpToday = Calendar.getInstance(TimeZone.getTimeZone("Japan"));
        jpToday.set(Calendar.HOUR, 0);
        jpToday.set(Calendar.MINUTE, 0);
        jpToday.set(Calendar.SECOND, 0);
        jpToday.set(Calendar.MILLISECOND, 0);
        return jpToday.getTimeInMillis();
    }

    public void clearAlertSchedule() {
        padScheduleHelper.clearAlertSchedule(padScheduleDB);
    }

    public long getAlertHistory() {
        Cursor cursor = padScheduleDB.query(PADSchedHelper.ALERT_HISTORY_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToNext()) {
            return cursor.getLong(PADSchedHelper.ALERT_HISTORY_HISTORY_COLUMN);
        } else {
            return 0;
        }
    }

    public ArrayList<AlertScheduleObject> getAlertSchedule() {
        ArrayList<AlertScheduleObject> alertSchedule = new ArrayList<AlertScheduleObject>();
        AlertScheduleObject currentAso;
        if (getTodayJpDate() > getAlertHistory()) {
            return buildAlertSchedule();
        } else {
            String orderBy = PADSchedHelper.ALERT_SCHEDULE_TABLE_COLUMNS[PADSchedHelper.ALERT_SCHEDULE_TIME_COLUMN] + " ASC";
            Cursor cursor = padScheduleDB.query(PADSchedHelper.ALERT_SCHEDULE_TABLE_NAME, null, null, null, null, null, orderBy);
            while (cursor.moveToNext()) {
                currentAso = new AlertScheduleObject(cursor.getInt(PADSchedHelper.ALERT_SCHEDULE_EVENT_COLUMN), cursor.getInt(PADSchedHelper.ALERT_SCHEDULE_GROUP_COLUMN),
                        cursor.getLong(PADSchedHelper.ALERT_SCHEDULE_TIME_COLUMN));
                alertSchedule.add(currentAso);
            }
            return alertSchedule;
        }
    }
}
