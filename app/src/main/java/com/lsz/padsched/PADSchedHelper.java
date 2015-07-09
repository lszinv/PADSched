package com.lsz.padsched;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PADSchedHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    public static final String ALERT_SCHEDULE_TABLE_NAME = "alert";
    public static final String ALERT_HISTORY_TABLE_NAME = "alert_history";
    public static final String SCHEDULE_TABLE_NAME = "schedule";
    public static final String MASTER_TOGGLE_TABLE_NAME = "master_toggles";
    public static final String ALERT_SWITCH_TABLE_NAME = "alert_switches";

    public static final String[] ALERT_SCHEDULE_TABLE_COLUMNS = { "event_group", "event_id", "event_time" };
    public static final int ALERT_SCHEDULE_GROUP_COLUMN = 0;
    public static final int ALERT_SCHEDULE_EVENT_COLUMN = 1;
    public static final int ALERT_SCHEDULE_TIME_COLUMN = 2;

    public static final String[] ALERT_HISTORY_TABLE_COLUMNS = { "alert_history" };
    public static final int ALERT_HISTORY_HISTORY_COLUMN = 0;

    public static final String SCHEDULE_EVENT_COLUMN = "event";
    public static final String[] SCHEDULE_TABLE_COLUMNS = { "groupA", "groupB", "groupC", "groupD", "groupE", SCHEDULE_EVENT_COLUMN };

    public static final int[] SCHEDULE_GROUP_INDEXES = { 0, 1, 2, 3, 4 };
    public static final int SCHEDULE_EVENT_INDEX = 5;

    public static final String MASTER_TOGGLE_GROUP_COLUMN = "group_id";
    public static final String MASTER_TOGGLE_VALUE_COLUMN = "value";

    public static final String[] ALERT_SWITCH_COLUMNS = { "group_id", "event_id", "value" };
    public static final int ALERT_SWITCH_GROUP_COLUMN = 0;
    public static final int ALERT_SWITCH_EVENT_COLUMN = 1;
    public static final int ALERT_SWITCH_VALUE_COLUMN = 2;

    private static final String CREATE_ALERT_HISTORY_TABLE = "CREATE TABLE " + ALERT_HISTORY_TABLE_NAME + " ( " + ALERT_HISTORY_TABLE_COLUMNS[ALERT_HISTORY_HISTORY_COLUMN] + " INTEGER );";

    private static final String CREATE_ALERT_SCHEDULE_TABLE = "CREATE TABLE " + ALERT_SCHEDULE_TABLE_NAME + " ( " + ALERT_SCHEDULE_TABLE_COLUMNS[ALERT_SCHEDULE_GROUP_COLUMN] + " INTEGER, "
            + ALERT_SCHEDULE_TABLE_COLUMNS[ALERT_SCHEDULE_EVENT_COLUMN] + " INTEGER, " + ALERT_SCHEDULE_TABLE_COLUMNS[ALERT_SCHEDULE_TIME_COLUMN] + " INTEGER );";

    private static final String CREATE_SCHEDULE_TABLE = "CREATE TABLE " + SCHEDULE_TABLE_NAME + " ( " + SCHEDULE_TABLE_COLUMNS[Constants.GROUP_A] + " TEXT, "
            + SCHEDULE_TABLE_COLUMNS[Constants.GROUP_B] + " TEXT, " + SCHEDULE_TABLE_COLUMNS[Constants.GROUP_C] + " TEXT, " + SCHEDULE_TABLE_COLUMNS[Constants.GROUP_D] + " TEXT, "
            + SCHEDULE_TABLE_COLUMNS[Constants.GROUP_E] + " TEXT, " + SCHEDULE_EVENT_COLUMN + " INTEGER);";

    private static final String CREATE_MASTER_TOGGLE_TABLE = "CREATE TABLE " + MASTER_TOGGLE_TABLE_NAME + " ( " + MASTER_TOGGLE_GROUP_COLUMN + " INTEGER, " + MASTER_TOGGLE_VALUE_COLUMN
            + " INTEGER );";

    private static final String CREATE_ALERT_SWITCH_TABLE = "CREATE TABLE " + ALERT_SWITCH_TABLE_NAME + " ( " + ALERT_SWITCH_COLUMNS[ALERT_SWITCH_GROUP_COLUMN] + " INTEGER,"
            + ALERT_SWITCH_COLUMNS[ALERT_SWITCH_EVENT_COLUMN] + " INTEGER," + ALERT_SWITCH_COLUMNS[ALERT_SWITCH_VALUE_COLUMN] + " INTEGER );";

    public PADSchedHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ALERT_HISTORY_TABLE);
        db.execSQL(CREATE_ALERT_SCHEDULE_TABLE);
        db.execSQL(CREATE_SCHEDULE_TABLE);
        db.execSQL(CREATE_MASTER_TOGGLE_TABLE);
        db.execSQL(CREATE_ALERT_SWITCH_TABLE);
        populateMasterToggles(db);
        populateAlertSwitches(db);

    }

    private void populateMasterToggles(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        for (int group = Constants.GROUP_A; group <= Constants.GROUP_E; group++) {
            values.put(MASTER_TOGGLE_GROUP_COLUMN, group);
            values.put(MASTER_TOGGLE_VALUE_COLUMN, 0);
            db.insert(MASTER_TOGGLE_TABLE_NAME, null, values);
        }
    }

    private void populateAlertSwitches(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        for (int group = Constants.GROUP_A; group <= Constants.GROUP_E; group++) {
            for (int event = 0; event < Constants.DUNGEON_COUNT; event++) {
                values.put(ALERT_SWITCH_COLUMNS[ALERT_SWITCH_EVENT_COLUMN], event);
                values.put(ALERT_SWITCH_COLUMNS[ALERT_SWITCH_GROUP_COLUMN], group);
                values.put(ALERT_SWITCH_COLUMNS[ALERT_SWITCH_VALUE_COLUMN], 0);
                db.insert(ALERT_SWITCH_TABLE_NAME, null, values);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ALERT_SCHEDULE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ALERT_HISTORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MASTER_TOGGLE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ALERT_SWITCH_TABLE_NAME);
        onCreate(db);

    }

    public void clearAlertSchedule(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + ALERT_SCHEDULE_TABLE_NAME);
        db.execSQL(CREATE_ALERT_SCHEDULE_TABLE);
    }

    public void clearAlertHistorySchedule(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + ALERT_HISTORY_TABLE_NAME);
        db.execSQL(CREATE_ALERT_HISTORY_TABLE);
    }

    public void clearSchedule(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE_TABLE_NAME);
        db.execSQL(CREATE_SCHEDULE_TABLE);
    }

    public void clearMasterToggleData(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + MASTER_TOGGLE_TABLE_NAME);
        db.execSQL(CREATE_MASTER_TOGGLE_TABLE);
        populateMasterToggles(db);
    }

    public void clearAlertSwitchData(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + ALERT_SWITCH_TABLE_NAME);
        db.execSQL(CREATE_ALERT_SWITCH_TABLE);
        populateAlertSwitches(db);
    }
}
