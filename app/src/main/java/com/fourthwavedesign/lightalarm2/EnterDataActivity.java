package com.fourthwavedesign.lightalarm2;

/**
 * Created by reddog on 12/14/14.
 */
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.fourthwavedesign.lightalarm2.AlarmDatabaseHelper;

public class EnterDataActivity extends ActionBarActivity {
    private static final String TAG = EnterDataActivity.class.getSimpleName();

    EditText editTextPersonName;
    EditText editTextPersionPIN;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_data);

        editTextPersonName = (EditText) findViewById(R.id.et_person_name);
        editTextPersionPIN = (EditText) findViewById(R.id.et_person_pin);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null && extras.containsKey("extra_id")) {
            String sID = getIntent().getStringExtra("extra_id");
            if (sID.length() > 0) {
                //            Log.v(TAG, "Intent ID recieved: " + sID);
                AlarmDatabaseHelper dbHelper = new AlarmDatabaseHelper(this);
                Cursor db = dbHelper.getAlarm(sID);
                //            Log.v(TAG, "Cursor result: " + db);
                if (db.moveToFirst()) { //data exists
                    String dbPersonName = db.getString(db.getColumnIndex("person_name"));
                    editTextPersonName.setText(dbPersonName);
                }
                db.close();
            }
        }

    }

    public void onClickAdd (View btnAdd) {

        String personName = editTextPersonName.getText().toString();
        String personPIN = editTextPersionPIN.getText().toString();

        if ( personName.length() != 0 && personPIN.length() != 0 ) {

            Intent newIntent = getIntent();
            newIntent.putExtra("tag_person_name", personName);
            newIntent.putExtra("tag_person_pin", personPIN);

            this.setResult(RESULT_OK, newIntent);

            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enter_data, menu);
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
        return super.onOptionsItemSelected(item);
    }

}