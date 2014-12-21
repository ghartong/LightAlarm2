package com.fourthwavedesign.lightalarm2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by reddog on 12/21/14.
 */
public class CreditsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public CreditsAdapter(Context context, String[] values) {
        super(context, R.layout.activity_credits, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activity_credits, parent, false);
        TextView compView = (TextView) rowView.findViewById(R.id.credit_company);
        TextView pplView = (TextView) rowView.findViewById(R.id.credit_person);

        String DELIM = "|";

        String str = values[position];
        String comp = str.substring(0, str.indexOf(DELIM));
        String ppl = str.substring(str.indexOf(DELIM)+1);

        compView.setText(comp);
        pplView.setText(ppl);

        return rowView;
    }
}
