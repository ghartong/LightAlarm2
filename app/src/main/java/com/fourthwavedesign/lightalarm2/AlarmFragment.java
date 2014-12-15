package com.fourthwavedesign.lightalarm2;

        import android.content.Intent;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.LoaderManager.LoaderCallbacks;
        import android.support.v4.content.CursorLoader;
        import android.support.v4.content.Loader;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ListView;

        import com.fourthwavedesign.lightalarm2.data.AlarmContract;

        import java.util.Date;

/**
 * Encapsulates fetching the alarms and displaying it as a {@link android.widget.ListView} layout.
 */
public class AlarmFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private AlarmAdapter mAlarmAdapter;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout;

    private static final String SELECTED_KEY = "selected_position";

    private static final int ALARM_LOADER = 0;

    // For the alarm view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] ALARM_COLUMNS = {
            AlarmContract.AlarmEntry.TABLE_NAME + "." + AlarmContract.AlarmEntry._ID,
            AlarmContract.AlarmEntry.COLUMN_DATETEXT,
            AlarmContract.AlarmEntry.COLUMN_SHORT_DESC
    };


    // These indices are tied to ALARM_COLUMNS.  If ALARM_COLUMNS changes, these
    // must change.
    public static final int COL_ALARM_ID = 0;
    public static final int COL_ALARM_DATE = 1;
    public static final int COL_ALARM_DESC = 2;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String date);
    }

    public AlarmFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        Log.v(LOG_TAG, "XXXXXXXXXXXXXXXXXXXX IN alarm fragment onCreate ");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.alarmfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateAlarm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "XXXXXXXXXXXXXXXXXXXX IN alarm fragment onCreateView ");

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mAlarmAdapter = new AlarmAdapter(getActivity(), null, 0);
        Log.v(LOG_TAG, "XXXXXXXXXXXXXXXXXXXX IN alarm fragment right after new AlarmAdapter ");

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_alarm);
        mListView.setAdapter(mAlarmAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = mAlarmAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback)getActivity())
                            .onItemSelected(cursor.getString(COL_ALARM_ID));
                }

                mPosition = position;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ALARM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

        Log.v(LOG_TAG, "XXXXXXXXXXXXXXXXXXXX IN alarm fragment onActivityCreated ");
    }

    private void updateAlarm() {
        new FetchAlarmTask(getActivity()).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = AlarmContract.getDbDateString(new Date());

        // Sort order:  Ascending, by date.
        String sortOrder = AlarmContract.AlarmEntry.COLUMN_DATETEXT + " ASC";

        Uri alarmUri = AlarmContract.AlarmEntry.buildAlarmWithStartDate(startDate);

        Log.v(LOG_TAG, "XXXXXXXXXXXXXXXXXXXX IN alarm fragment Loader ");
        Log.v(LOG_TAG, String.valueOf(alarmUri));

        Loader<Cursor> cl = new CursorLoader(
                getActivity(),
                alarmUri,
                ALARM_COLUMNS,
                null,
                null,
                sortOrder
        );

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                alarmUri,
                ALARM_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAlarmAdapter.swapCursor(data);

        if(loader.getId() == 0){
            Log.v(LOG_TAG, "XXXXXXXXXXXXXXXXXXXX good start ");
        }
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAlarmAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mAlarmAdapter != null) {
            mAlarmAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }
}