package chuang.karote.actionslistgenerator.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import chuang.karote.actionslistgenerator.model.SavedActionList;

/**
 * Created by karot.chuang on 2016/8/30.
 */
public class SavedActionListDataAccessObject {
    public static final String TABLE_NAME = "savedActionList";

    public final static String KEY_ID = "_id";
    public final static String COLUMN_TIME_STAMP = "timeStamp";
    public final static String COLUMN_LIST_NAME = "listName";
    public final static String COLUMN_JSON_STRING = "jsonString";

    private final static int INDEX_ID = 0;
    private final static int INDEX_TIME_STAMP = 1;
    private final static int INDEX_LIST_NAME = 2;
    private final static int INDEX_JSON_STRING = 3;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TIME_STAMP + " INTEGER NOT NULL, " +
                    COLUMN_LIST_NAME + " TEXT NOT NULL, " +
                    COLUMN_JSON_STRING + " TEXT NOT NULL)";

    private SQLiteDatabase db;

    public SavedActionListDataAccessObject(Context context) {
        db = SavedActionListDBHelper.getDataBase(context);
    }

    public void close() {
        db.close();
    }

    public SavedActionList insert(SavedActionList item) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TIME_STAMP, item.getTimeStamp());
        cv.put(COLUMN_LIST_NAME, item.getListName());
        cv.put(COLUMN_JSON_STRING, item.getJsonString());

        long id = db.insert(TABLE_NAME, null, cv);
        item.setId(id);
        return item;
    }

    public boolean delete(long id) {
        String where = KEY_ID + "=" + id;
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    public SavedActionList getRecord(Cursor cursor) {
        SavedActionList.Builder resultBuilder = new SavedActionList.Builder();
        SavedActionList result = resultBuilder.create();

        result.setId(cursor.getLong(INDEX_ID));
        result.setTimeStamp(cursor.getLong(INDEX_TIME_STAMP));
        result.setListName(cursor.getString(INDEX_LIST_NAME));
        result.setJsonString(cursor.getString(INDEX_JSON_STRING));

        return result;
    }

    public List<SavedActionList> getAll() {
        List<SavedActionList> result = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_TIME_STAMP + " DESC", null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public SavedActionList get(long id) {
        SavedActionList item = null;
        String where = KEY_ID + "=" + id;

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
}
