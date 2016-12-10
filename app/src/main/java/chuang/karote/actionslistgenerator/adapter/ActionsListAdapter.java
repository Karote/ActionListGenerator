package chuang.karote.actionslistgenerator.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import chuang.karote.actionslistgenerator.R;
import chuang.karote.actionslistgenerator.model.TabataAction;

/**
 * Created by karot.chuang on 2016/11/7.
 */

public class ActionsListAdapter extends RecyclerView.Adapter<ActionsListAdapter.ActionsListViewHolder> {
    public static int ADAPTER_MODE_LIST = 0;
    public static int ADAPTER_MODE_EDIT = 1;

    private List<TabataAction> mActionsList;
    private int mAdapterMode;

    public ActionsListAdapter(List<TabataAction> actionsList, int adapterMode) {
        this.mActionsList = actionsList;
        this.mAdapterMode = adapterMode;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemDeleteClick(int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ActionsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mAdapterMode == 0
                ? new ActionsListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.action_list_item, parent, false))
                : new ActionsListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ActionsListViewHolder holder, final int position) {
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
            }
        });
        holder.itemNumber.setText(String.valueOf(position + 1));
        holder.actionName.setText(mActionsList.get(position).getName());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mActionsList.size();
    }

    public class ActionsListViewHolder extends RecyclerView.ViewHolder {
        final RelativeLayout itemLayout;
        final TextView itemNumber;
        final TextView actionName;
        final ImageButton deleteButton;

        public ActionsListViewHolder(View itemView) {
            super(itemView);

            itemLayout = (RelativeLayout) itemView.findViewById(R.id.action_item);
            itemNumber = (TextView) itemView.findViewById(R.id.item_number);
            actionName = (TextView) itemView.findViewById(R.id.action_name);
            deleteButton = (ImageButton) itemView.findViewById(R.id.delete_button);
        }
    }
}
