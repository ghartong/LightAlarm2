package com.fourthwavedesign.lightalarm2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by reddog on 12/16/14.
 */
public class ThemeUtils {

    private static int cTheme;
    public final static int DARK = 1;
    public final static int LITE = 2;

    public static void changeToTheme(Activity activity){
        //get theme from prefs or default to 1
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String theme = sharedPrefs.getString("theme","1");

        cTheme = Integer.parseInt(theme);

        switch (cTheme){
            default:
            case DARK:
                activity.setTheme(R.style.AppBaseTheme);
                break;
            case LITE:
                activity.setTheme(R.style.AppThemeLight);
                break;
        }
    }

}