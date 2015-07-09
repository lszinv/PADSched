package com.lsz.padsched;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AlertMasterToggleDataSource {
    private SQLiteDatabase db;
    private PADSchedHelper dbHelper;

    public AlertMasterToggleDataSource(Context context) {
        dbHelper = new PADSchedHelper(context);
        open();
    }

    private void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean getMasterToggle(int group) {
        Cursor cursor = db.query(PADSchedHelper.MASTER_TOGGLE_TABLE_NAME, new String[] { PADSchedHelper.MASTER_TOGGLE_VALUE_COLUMN }, PADSchedHelper.MASTER_TOGGLE_GROUP_COLUMN + " = " + group, null,
                null, null, null, null);
        cursor.moveToFirst();
        return cursor.getInt(0) == 1;
    }

    public void setMasterToggle(int group, boolean value) {
        ContentValues values = new ContentValues();
        int intValue = value ? 1 : 0;
        values.put(PADSchedHelper.MASTER_TOGGLE_VALUE_COLUMN, intValue);
        db.update(PADSchedHelper.MASTER_TOGGLE_TABLE_NAME, values, PADSchedHelper.MASTER_TOGGLE_GROUP_COLUMN + " = " + group, null);
    }
}
