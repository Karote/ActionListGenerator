package chuang.karote.actionslistgenerator.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by karot.chuang on 2016/8/30.
 */
public class CalenderLogDBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "calenderLog.db";
    private final static int VERSION = 1;
    private static SQLiteDatabase database;

    public CalenderLogDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDataBase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new CalenderLogDBHelper(context, DATABASE_NAME, null, VERSION).getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CalenderLogDataAccessObject.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CalenderLogDataAccessObject.TABLE_NAME);
        onCreate(db);
    }
}