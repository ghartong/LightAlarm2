package com.fourthwavedesign.lightalarm2;

/**
 * Created by reddog on 12/14/14.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MyActivity extends ActionBarActivity {
//    public final static String EXTRA_MESSAGE = "com.fourthwavedesign.lightAlarm2.MESSAGE";
    private CustomCursorAdapter customAdapter;
    private AlarmDatabaseHelper databaseHelper;
    private static final int ENTER_DATA_REQUEST_CODE = 1;
    private ListView listView;

    private static final String TAG = MyActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        databaseHelper = new AlarmDatabaseHelper(this);

        listView = (ListView) findViewById(R.id.list_data);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "clicked on item: " + position);
                editAlarm(view, id);
            }
        });

        // Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        // Handler, will handle this stuff for you <img src="http://s0.wp.com/wp-includes/images/smilies/icon_smile.gif?m=1129645325g" alt=":)" class="wp-smiley">

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                customAdapter = new CustomCursorAdapter(MyActivity.this, databaseHelper.getAllData());
                listView.setAdapter(customAdapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_add) {
            startActivityForResult(new Intent(this, EnterDataActivity.class), ENTER_DATA_REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickToggleActive(View btnAdd) {
        Log.v(TAG, "TOGGLE ACTIVE");

    }

    public void editAlarm(View view, long alarm_id){
//        Log.v(TAG, "id passed to sql: " + alarm_id);
        Intent intent = new Intent(this, EnterDataActivity.class).putExtra("extra_id", String.valueOf(alarm_id));
        startActivityForResult(intent, ENTER_DATA_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ENTER_DATA_REQUEST_CODE && resultCode == RESULT_OK) {

            databaseHelper.insertData(data.getExtras().getString("tag_person_name"), data.getExtras().getString("tag_person_pin"));

            customAdapter.changeCursor(databaseHelper.getAllData());
        }
    }
}