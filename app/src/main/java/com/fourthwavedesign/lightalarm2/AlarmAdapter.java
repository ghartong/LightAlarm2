package com.fourthwavedesign.lightalarm2;

/**
 * Created by reddog on 12/14/14.
 */

        import android.content.Context;
        import android.database.Cursor;
        import android.support.v4.widget.CursorAdapter;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

/**
 * {@link AlarmAdapter} exposes a list of alarms
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class AlarmAdapter extends CursorAdapter {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int VIEW_TYPE_COUNT = 3;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_EMPTY = 3;

    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;

    /**
     * Cache of the children views for a alarm list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_alarm_textview);
        }
    }

    public AlarmAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        Log.v(LOG_TAG, "XXXXXXXXXXXXXXXXXXXX IN alarm adapter() ");

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(LOG_TAG, "XXXXXXXXXXXXXXXXXXXX IN alarm adapter newView ");

        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_alarm_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_alarm;
                break;
            }
            default:{
                layoutId = R.layout.list_item_alarm_empty;
            }

        }
        Log.v(LOG_TAG, "XXXXXXXXXXXXXXXXXXXX IN alarm adapter layoutID " + layoutId);

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(LOG_TAG, "XXXXXXXXXXXXXXXXXXXX IN alarm adapter bindView ");

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int viewType = getItemViewType(cursor.getPosition());
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                // Get weather icon
//                viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(
//                        cursor.getInt(AlarmFragment.COL_ALARM_ID)));
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                // Get weather icon
//                viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(
//                        cursor.getInt(AlarmFragment.COL_ALARM_ID)));
                break;
            }
        }

        // Read date from cursor
        String dateString = cursor.getString(AlarmFragment.COL_ALARM_DATE);
        // Find TextView and set formatted date on it
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

        // Read weather forecast from cursor
        String description = cursor.getString(AlarmFragment.COL_ALARM_DESC);
        // Find TextView and set weather forecast on it
        viewHolder.descriptionView.setText(description);

        //For accessibility, add a content description to the icon field.
        viewHolder.iconView.setContentDescription(description);

    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        Log.v(LOG_TAG, "XXXXXXXXXXXXXXXXXXXX IN alarm adapter getItemViewType ");

        if (position == 0 && mUseTodayLayout){
            return VIEW_TYPE_TODAY;
        }
        else if(position == -1){
            return VIEW_TYPE_EMPTY;
        }
        else
            return VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}