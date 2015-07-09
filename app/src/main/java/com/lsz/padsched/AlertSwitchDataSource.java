package com.lsz.padsched;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AlertSwitchDataSource {
    private SQLiteDatabase db;
    private PADSchedHelper dbHelper;

    public AlertSwitchDataSource(Context context) {
        dbHelper = new PADSchedHelper(context);
        open();
    }

    private void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public AlertSwitchObject getSwitches(int group) {
        boolean toggles[] = new boolean[Constants.DUNGEON_COUNT];
        Cursor cursor = db.query(PADSchedHelper.ALERT_SWITCH_TABLE_NAME, null, PADSchedHelper.ALERT_SWITCH_COLUMNS[PADSchedHelper.ALERT_SWITCH_GROUP_COLUMN] + " = " + group, null, null, null,
                PADSchedHelper.ALERT_SWITCH_COLUMNS[PADSchedHelper.ALERT_SWITCH_EVENT_COLUMN] + " ASC");
        while (cursor.moveToNext()) {
            toggles[cursor.getInt(PADSchedHelper.ALERT_SWITCH_EVENT_COLUMN)] = cursor.getInt(PADSchedHelper.ALERT_SWITCH_VALUE_COLUMN) == 1 ? true : false;
        }
        return new AlertSwitchObject(group, toggles);
    }

    public void setToggle(int group, int event, boolean value) {
        ContentValues values = new ContentValues();
        int intValue = value ? 1 : 0;
        values.put(PADSchedHelper.ALERT_SWITCH_COLUMNS[PADSchedHelper.ALERT_SWITCH_GROUP_COLUMN], group);
        values.put(PADSchedHelper.ALERT_SWITCH_COLUMNS[PADSchedHelper.ALERT_SWITCH_EVENT_COLUMN], event);
        values.put(PADSchedHelper.ALERT_SWITCH_COLUMNS[PADSchedHelper.ALERT_SWITCH_VALUE_COLUMN], intValue);
        db.update(PADSchedHelper.ALERT_SWITCH_TABLE_NAME, values, PADSchedHelper.ALERT_SWITCH_COLUMNS[PADSchedHelper.ALERT_SWITCH_GROUP_COLUMN] + " = " + group + " AND "
                + PADSchedHelper.ALERT_SWITCH_COLUMNS[PADSchedHelper.ALERT_SWITCH_EVENT_COLUMN] + " = " + event, null);

    }

}
