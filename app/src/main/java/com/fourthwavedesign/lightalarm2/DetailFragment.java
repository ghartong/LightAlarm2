package com.fourthwavedesign.lightalarm2;

/**
 * Created by reddog on 12/14/14.
 */

        import android.content.Context;
        import android.content.Intent;
        import android.content.res.Resources;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.LoaderManager;
        import android.support.v4.content.CursorLoader;
        import android.support.v4.content.Loader;
        import android.support.v4.view.MenuItemCompat;
        import android.support.v7.widget.ShareActionProvider;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ImageView;
        import android.widget.TextView;

        import java.util.HashMap;

        import com.fourthwavedesign.lightalarm2.data.AlarmContract;
        import com.fourthwavedesign.lightalarm2.data.AlarmContract.AlarmEntry;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String ALARM_SHARE_HASHTAG = " #LightAlarm";

    private static final String LOCATION_KEY = "location";

    private ShareActionProvider mShareActionProvider;
    private String mAlarm;
    private String mDateStr;

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            AlarmEntry.TABLE_NAME + "." + AlarmEntry._ID,
            AlarmEntry.COLUMN_DATETEXT,
            AlarmEntry.COLUMN_SHORT_DESC
    };

    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;
    private ImageView mHolidayView;
    private TextView mHolidayTextView;
    private Integer mImgName;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mDateStr = arguments.getString(DetailActivity.DATE_KEY);
        }

        if (savedInstanceState != null) {
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mHolidayView = (ImageView) rootView.findViewById(R.id.detail_holiday);
        mHolidayTextView = (TextView) rootView.findViewById(R.id.detail_holiday_textview);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_alarm_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailActivity.DATE_KEY) ) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mAlarm != null) {
            mShareActionProvider.setShareIntent(createShareAlarmIntent());
        }
    }

    private Intent createShareAlarmIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mAlarm + ALARM_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
        }

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailActivity.DATE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Sort order:  Ascending, by date.
        String sortOrder = AlarmContract.AlarmEntry.COLUMN_DATETEXT + " ASC";

        Uri alarmForDateUri = AlarmContract.AlarmEntry.buildAlarmWithStartDate(mDateStr);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                alarmForDateUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            // Read date from cursor and update views for day of week and date
            String date = data.getString(data.getColumnIndex(AlarmEntry.COLUMN_DATETEXT));
            String friendlyDateText = Utility.getDayName(getActivity(), date);
            String dateText = Utility.getFormattedMonthDay(getActivity(), date);
            mFriendlyDateView.setText(friendlyDateText);
            mDateView.setText(dateText);

            //if Date is holiday. show holiday message and image
            HashMap holidayHM = new HashMap();
            holidayHM = (HashMap) Utility.getHoliday(getActivity(), dateText);

            if(holidayHM.get("isholiday").toString().equals("true")) {

                if (holidayHM.get("message") != null) {
                    String holidayMsg = holidayHM.get("message").toString();
                    mHolidayTextView.setText(holidayMsg);
                }
                if (holidayHM.get("image") != null) {
                    String holidayImg = holidayHM.get("image").toString();
                    mImgName = getResources().getIdentifier(holidayImg, "drawable", getActivity().getPackageName());
                    mHolidayView.setImageResource(mImgName);
                }

            }else{
                mIconView.setImageResource(mImgName);
            }

            // Read description from cursor and update view
            String description = data.getString(data.getColumnIndex(
                    AlarmEntry.COLUMN_SHORT_DESC));
            mDescriptionView.setText(description);

            // Add a content description to the art icon for accessibility.
            mIconView.setContentDescription(description);

            // We still need this for the share intent
            mAlarm = String.format("%s - %s", dateText, description);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareAlarmIntent());
            }


            mIconView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View imgView) {
                    Intent fullImgIntent = createFullImageIntent();
                    startActivity(fullImgIntent);
                }
            });
            mHolidayView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View imgView) {
                    Intent fullImgIntent = createFullImageIntent();
                    startActivity(fullImgIntent);
                }
            });

        }

    }

    private Intent createFullImageIntent(){
        // Sending image id to FullScreenActivity
        Intent fullImgIntent = new Intent(getActivity(), FullImageActivity.class)
                .putExtra("imgID", mImgName);

        return fullImgIntent;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
