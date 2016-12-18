package chuang.karote.actionslistgenerator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CalendarHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import chuang.karote.actionslistgenerator.R;
import hirondelle.date4j.DateTime;

/**
 * Created by Karote on 2016/12/18.
 */

public class CalenderLogAdapter extends CaldroidGridAdapter {
    /**
     * Constructor
     *
     * @param context
     * @param month
     * @param year
     * @param caldroidData
     * @param extraData
     */
    public CalenderLogAdapter(Context context, int month, int year, Map<String, Object> caldroidData, Map<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cellView = convertView;

        if (convertView == null) {
            cellView = inflater.inflate(R.layout.calender_log_cell, null);
        }
        TextView dateTextView = (TextView) cellView.findViewById(R.id.date_text_view);
        LinearLayout counterLayout = (LinearLayout) cellView.findViewById(R.id.counter_layout);
        TextView counterTextView = (TextView) cellView.findViewById(R.id.counter_text_view);

        DateTime dateTime = this.datetimeList.get(position);
        HashMap<Date, Integer> calenderCounterMap = (HashMap<Date, Integer>) extraData.get("calenderLogMap");

        if (dateTime.equals(getToday())) {
            cellView.setBackgroundResource(com.caldroid.R.drawable.red_border);
        } else {
            cellView.setBackgroundResource(com.caldroid.R.drawable.cell_bg);
        }
        dateTextView.setText("" + dateTime.getDay());

        Date calendarDate = CalendarHelper.convertDateTimeToDate(dateTime);

        if (calenderCounterMap.containsKey(calendarDate)) {
            int counter = calenderCounterMap.get(calendarDate);
            if (counter > 0) {
                counterLayout.setVisibility(View.VISIBLE);
                counterTextView.setText(String.valueOf(counter));
            }
        }

        setCustomResources(dateTime, cellView, dateTextView);

        return cellView;
    }
}
