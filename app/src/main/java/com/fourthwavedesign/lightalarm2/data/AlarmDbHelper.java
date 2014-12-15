package com.fourthwavedesign.lightalarm2.data;

/**
 * Created by reddog on 12/14/14.
 */

    import android.content.Context;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;

    import com.fourthwavedesign.lightalarm2.data.AlarmContract.AlarmEntry;

/**
 * Manages a local database for alarm data.
 */
public class AlarmDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "alarm.db";

    public AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_ALARM_TABLE = "CREATE TABLE " + AlarmEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for alarms
                // it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the alarm data
                // should be sorted accordingly.
                AlarmEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                AlarmEntry.COLUMN_DATETEXT + " TEXT NOT NULL, " +
                AlarmEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL" +

                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
