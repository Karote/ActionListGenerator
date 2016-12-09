package chuang.karote.actionslistgenerator.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import chuang.karote.actionslistgenerator.R;
import chuang.karote.actionslistgenerator.model.TabataAction;

/**
 * Created by karot.chuang on 2016/11/7.
 */

public class SelectedListAdapter extends RecyclerView.Adapter<SelectedListAdapter.SelectedListViewHolder> {
    private List<TabataAction> mSelectedList;

    public SelectedListAdapter(List<TabataAction> actionsList) {
        this.mSelectedList = actionsList;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public SelectedListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectedListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SelectedListViewHolder holder, final int position) {
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
            }
        });
        holder.itemNumber.setText(String.valueOf(position + 1));
        holder.actionName.setText(mSelectedList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mSelectedList.size();
    }

    public class SelectedListViewHolder extends RecyclerView.ViewHolder {
        final RelativeLayout itemLayout;
        final TextView itemNumber;
        final TextView actionName;

        public SelectedListViewHolder(View itemView) {
            super(itemView);

            itemLayout = (RelativeLayout) itemView.findViewById(R.id.action_item);
            itemNumber = (TextView) itemView.findViewById(R.id.item_number);
            actionName = (TextView) itemView.findViewById(R.id.action_name);
        }
    }
}
