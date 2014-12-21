package com.fourthwavedesign.lightalarm2;

import android.app.ListActivity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;

/**
 * Created by reddog on 12/21/14.
 */
public class CreditsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //change to theme in pref or default
        ThemeUtils.changeToTheme(this);
        super.onCreate(savedInstanceState);

        Resources res = getResources();
        String[] values = res.getStringArray(R.array.credits_company);

        CreditsAdapter adapter = new CreditsAdapter(this, values);
        setListAdapter(adapter);
    }


}
