package chuang.karote.actionslistgenerator.ui;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import chuang.karote.actionslistgenerator.adapter.CalenderLogAdapter;

/**
 * Created by Karote on 2016/12/18.
 */

public class CalenderLogFragment extends CaldroidFragment {

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new CalenderLogAdapter(getActivity(), month, year, getCaldroidData(), extraData);
    }
}
