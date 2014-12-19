package com.fourthwavedesign.lightalarm2;

import android.content.Context;
import android.os.AsyncTask;
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
        if(!result.equals("failed")) {
            //Log.v("RequestTask", result);
            Toast toast = Toast.makeText(mContext, result, mDuration);
            toast.show();
        }
    }
}
