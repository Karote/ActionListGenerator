package chuang.karote.actionslistgenerator.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import chuang.karote.actionslistgenerator.R;
import chuang.karote.actionslistgenerator.model.TabataAction;

/**
 * Created by karot.chuang on 2016/12/9.
 */

public class CheckListAdapter extends BaseAdapter {
    private Activity activity;
    private List<TabataAction> mList;
    private HashMap<String, Boolean> mResourceMap;

    private static LayoutInflater inflater = null;

    public CheckListAdapter(Activity activity, List<TabataAction> list, HashMap<String, Boolean> resourceMap) {
        mList = list;
        mResourceMap = resourceMap;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.check_list_item, null);
        }
        TextView textView = (TextView) vi.findViewById(R.id.item_number);
        textView.setText(String.valueOf(position + 1));
        CheckedTextView chkBshow = (CheckedTextView) vi.findViewById(R.id.action_name);
        chkBshow.setText(mList.get(position).getName());
        chkBshow.setChecked(mResourceMap.get(mList.get(position).getName()));
        return vi;
    }
}
