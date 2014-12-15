package com.fourthwavedesign.lightalarm2;

/**
 * Created by reddog on 12/14/14.
 */

        import android.content.ContentUris;
        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.util.Log;

        import com.fourthwavedesign.lightalarm2.data.AlarmContract;
        import com.fourthwavedesign.lightalarm2.data.AlarmContract.AlarmEntry;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.Date;
        import java.util.Vector;

public class FetchAlarmTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchAlarmTask.class.getSimpleName();
    private final Context mContext;

    public FetchAlarmTask(Context context) {
        mContext = context;
    }

    /**
     * Helper method to handle insertion of a new alarm in the database.
     *
     * @param dateText The date string used to request updates from the server.
     * @return the row ID of the added alarm.
     */
    private long addAlarm(String dateText) {

        // First, check if the alarm exists in the db
        Cursor cursor = mContext.getContentResolver().query(
                AlarmEntry.CONTENT_URI,
                new String[]{AlarmEntry._ID},
                AlarmEntry.COLUMN_DATETEXT + " = ?",
                new String[]{dateText},
                null);

        if (cursor.moveToFirst()) {
            int alarmIdIndex = cursor.getColumnIndex(AlarmEntry._ID);
            return cursor.getLong(alarmIdIndex);
        } else {
            ContentValues locationValues = new ContentValues();
            locationValues.put(AlarmEntry.COLUMN_DATETEXT, dateText);

            Uri locationInsertUri = mContext.getContentResolver()
                    .insert(AlarmEntry.CONTENT_URI, locationValues);

            return ContentUris.parseId(locationInsertUri);
        }
    }

    private void getAlarmDataFromJson(String alarmJsonStr)
            throws JSONException {

    }

    @Override
    protected Void doInBackground(String... params) {
        return null;
    }
}