package co.fanstories.android.pages;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mohsal on 7/3/17.
 */

public class PagesDB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Pages.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PagesContract.PagesEntry.TABLE_NAME + " (" +
                    PagesContract.PagesEntry._ID + " INTEGER PRIMARY KEY," +
                    PagesContract.PagesEntry.COLUMN_NAME_ID + " TEXT," +
                    PagesContract.PagesEntry.COLUMN_NAME_NAME + " TEXT," +
                    PagesContract.PagesEntry.COLUMN_NAME_PAGE_ID + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PagesContract.PagesEntry.TABLE_NAME;

    public PagesDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
