package com.fourthwavedesign.lightalarm2;

/**
 * Created by reddog on 12/14/14.
 */

        import android.annotation.TargetApi;
        import android.content.Intent;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.v7.app.ActionBarActivity;
        import android.util.Log;
        import android.widget.ImageView;

public class FullImageActivity extends ActionBarActivity {

    private static final String LOG_TAG = FullImageActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        // get intent data
        Intent i = getIntent();
        Integer imgID = i.getExtras().getInt("imgID");
        Log.v(LOG_TAG, "intent passed in: " + imgID);

        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        imageView.setImageResource(imgID);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    //this makes the up button keep the details of parent. so you go back to correct place
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

}