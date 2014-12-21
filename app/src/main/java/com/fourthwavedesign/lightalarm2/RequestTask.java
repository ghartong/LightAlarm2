package com.fourthwavedesign.lightalarm2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by reddog on 12/18/14.
 */
class RequestTask extends AsyncTask<String, String, String> {
    public Context mContext;
    public int mDuration = Toast.LENGTH_LONG;

    private NotificationManager mNotificationManager;
    private int notificationID = 100;
    private int numMessages = 0;

    public RequestTask(Context context){
        mContext=context;
    }

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                //responseString = out.toString();
                responseString = "Successful call to " + uri[0];
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //Log.v("RequestTask", "ClientProtocolException");
            responseString = e.getMessage();
        } catch (IOException e) {
            //Log.v("RequestTask", "IOException");
            responseString = e.getMessage();
        }

        return responseString;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..

        //get api vars from prefs or default to "FALSE"
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Boolean apidebug = sharedPrefs.getBoolean("apidebug", false);
        Boolean notify = sharedPrefs.getBoolean("notify", false);

        if(!result.equals("failed")) {
            Log.v("RequestTask", "Debug: " + apidebug);
            if(apidebug == true) {
                //Log.v("RequestTask", result);
                Toast toast = Toast.makeText(mContext, result, mDuration);
                toast.show();
            }
        }

        if(notify == true) {
            //Log.i("Request Task", "notification started");
          /* Invoking the default notification service */
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mContext);

            mBuilder.setContentTitle(mContext.getString(R.string.notification_title));
            mBuilder.setContentText(mContext.getString(R.string.notification_text));
            mBuilder.setTicker(mContext.getString(R.string.notification_ticker));
            mBuilder.setSmallIcon(R.drawable.ic_launcher);
            mBuilder.setAutoCancel(true);

          /* Increase notification number every time a new notification arrives */
            mBuilder.setNumber(++numMessages);

          /* Creates an explicit intent for an Activity in your app */
            Intent resultIntent = new Intent(mContext, AlarmListActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            mContext,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

          /* notificationID allows you to update the notification later on. */
            mNotificationManager.notify(notificationID, mBuilder.build());
        }
    }
}
