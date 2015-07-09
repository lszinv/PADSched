package com.lsz.padsched;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ScheduleDataSource {
    private SQLiteDatabase db;
    private PADSchedHelper dbHelper;

    public ScheduleDataSource(Context context) {
        dbHelper = new PADSchedHelper(context);
        open();
    }

    private void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addEvent(EventScheduleRow event) {
        ContentValues values = new ContentValues();
        for (int group = PADSchedHelper.SCHEDULE_GROUP_INDEXES[Constants.GROUP_A]; group <= PADSchedHelper.SCHEDULE_GROUP_INDEXES[Constants.GROUP_E]; group++) {
            values.put(PADSchedHelper.SCHEDULE_TABLE_COLUMNS[group], event.times[group]);
        }
        values.put(PADSchedHelper.SCHEDULE_EVENT_COLUMN, event.event);
        db.insert(PADSchedHelper.SCHEDULE_TABLE_NAME, null, values);
    }

    public void clearSchedule() {
        dbHelper.clearSchedule(db);
    }

    public ArrayList<EventScheduleRow> getSchedule() {
        ArrayList<EventScheduleRow> schedule = new ArrayList<EventScheduleRow>();
        String timeString;
        EventScheduleRow currentEvent;
        Cursor cursor = db.query(PADSchedHelper.SCHEDULE_TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            currentEvent = new EventScheduleRow();
            currentEvent.event = cursor.getInt(PADSchedHelper.SCHEDULE_EVENT_INDEX);
            for (int group = PADSchedHelper.SCHEDULE_GROUP_INDEXES[Constants.GROUP_A]; group <= PADSchedHelper.SCHEDULE_GROUP_INDEXES[Constants.GROUP_E]; group++) {
                timeString = cursor.getString(PADSchedHelper.SCHEDULE_GROUP_INDEXES[group]);
                currentEvent.times[group] = Long.parseLong(timeString);
            }
            schedule.add(currentEvent);
            cursor.moveToNext();
        }
        cursor.close();
        return schedule;
    }
}
