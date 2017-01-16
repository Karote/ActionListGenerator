package chuang.karote.actionslistgenerator.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;
import java.util.HashMap;

import chuang.karote.actionslistgenerator.model.CalenderLog;

/**
 * Created by karot.chuang on 2016/8/30.
 */
public class CalenderLogDataAccessObject {
    public static final String TABLE_NAME = "calenderLog";

    public final static String KEY_ID = "_id";
    public final static String COLUMN_CALENDER_DATE = "calenderDate";
    public final static String COLUMN_COUNTER = "counter";

    private final static int INDEX_ID = 0;
    private final static int INDEX_CALENDER_DATE = 1;
    private final static int INDEX_COUNTER = 2;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CALENDER_DATE + " INTEGER NOT NULL, " +
                    COLUMN_COUNTER + " INTEGER NOT NULL)";

    private SQLiteDatabase db;

    public CalenderLogDataAccessObject(Context context) {
        db = CalenderLogDBHelper.getDataBase(context);
    }

    public void close() {
        db.close();
    }

    public CalenderLog insert(CalenderLog item) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_CALENDER_DATE, item.getCalenderDate().getTime());
        cv.put(COLUMN_COUNTER, item.getCounter());

        long id = db.insert(TABLE_NAME, null, cv);
        item.setId(id);
        return item;
    }

    public boolean delete(long id) {
        String where = KEY_ID + "=" + id;
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    public CalenderLog getRecord(Cursor cursor) {
        CalenderLog.Builder resultBuilder = new CalenderLog.Builder();
        CalenderLog result = resultBuilder.create();

        result.setId(cursor.getLong(INDEX_ID));
        result.setCalenderDate(new Date(cursor.getLong(INDEX_CALENDER_DATE)));
        result.setCounter(cursor.getInt(INDEX_COUNTER));

        return result;
    }

    public HashMap<Date, Integer> getAll() {
        HashMap<Date, Integer> result = new HashMap<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_CALENDER_DATE + " DESC", null);

        while (cursor.moveToNext()) {
            result.put(getRecord(cursor).getCalenderDate(), getRecord(cursor).getCounter());
        }

        cursor.close();
        return result;
    }

    public CalenderLog get(long id) {
        CalenderLog item = null;
        String where = KEY_ID + "=" + id;

        Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);

        if (result.moveToFirst()) {
            item = getRecord(result);
        }

        result.close();
        return item;
    }

    public CalenderLog getRecordByDate(Date date) {
        CalenderLog item = null;
        String where = COLUMN_CALENDER_DATE + "=" + date.getTime();

        Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);

        if (result.moveToFirst()) {
            item = getRecord(result);
        }

        result.close();
        return item;
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        cursor.close();
        return result;
    }

    public boolean update(CalenderLog item) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_CALENDER_DATE, item.getCalenderDate().getTime());
        cv.put(COLUMN_COUNTER, item.getCounter());
        String where = KEY_ID + "=" + item.getId();
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }
}
