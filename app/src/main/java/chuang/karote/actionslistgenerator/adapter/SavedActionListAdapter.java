package chuang.karote.actionslistgenerator.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import chuang.karote.actionslistgenerator.R;
import chuang.karote.actionslistgenerator.model.SavedActionList;

/**
 * Created by karot.chuang on 2016/11/29.
 */

public class SavedActionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SavedActionList> adapterList;

    private OnSavedListItemClickListener onSavedListItemClickListener;

    public SavedActionListAdapter(List<SavedActionList> adapterList) {
        this.adapterList = adapterList;
    }

    public interface OnSavedListItemClickListener {
        void onItemLayoutClick(String listJsonString);

        void onItemDeleteClick(long id);
    }

    public void setOnSavedListItemClickListener(OnSavedListItemClickListener onSavedListItemClickListener) {
        this.onSavedListItemClickListener = onSavedListItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_list_item, parent, false);
        return new SavedActionListItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SavedActionList savedActionList = adapterList.get(position);
        ((SavedActionListItemViewHolder) holder).listNameTextView.setText(savedActionList.getListName());
        ((SavedActionListItemViewHolder) holder).listItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSavedListItemClickListener.onItemLayoutClick(savedActionList.getJsonString());
            }
        });
        ((SavedActionListItemViewHolder) holder).deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSavedListItemClickListener.onItemDeleteClick(savedActionList.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }

    private class SavedActionListItemViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout listItemLayout;
        private TextView listNameTextView;
        private View deleteButton;

        public SavedActionListItemViewHolder(View itemView) {
            super(itemView);
            listItemLayout = (RelativeLayout) itemView.findViewById(R.id.saved_list_item_layout);
            listNameTextView = (TextView) itemView.findViewById(R.id.list_name_text);
            deleteButton = itemView.findViewById(R.id.list_delete_button);
        }
    }

    public void update(List<SavedActionList> lists) {
        this.adapterList = lists;
        notifyDataSetChanged();
    }
}
