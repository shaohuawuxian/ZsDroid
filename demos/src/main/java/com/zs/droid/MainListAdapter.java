package com.zs.droid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.start;
import static android.media.CamcorderProfile.get;

/**
 * Created by zhangshao on 2017/1/17.
 */

public class MainListAdapter extends RecyclerView.Adapter<MainListHolder> {

    Context mContext;
    List<ListItemModel> mList = new ArrayList<>();
    LayoutInflater layoutInflater = null;

    public MainListAdapter(Context context) {
        mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
    }

    public void addAll(List<ListItemModel> list) {
        if (list != null && list.size() >= 0) {
            mList.addAll(list);
            notifyItemChanged(mList.size() - list.size(), mList.size() - 1);
        }

    }

    public void add(ListItemModel model) {
        if (model != null) {
            mList.add(model);
            notifyItemChanged(mList.size() - 1);
        }

    }

    @Override
    public MainListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_mainlist, parent, false);
        return new MainListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MainListHolder holder, int position) {
        final ListItemModel model = mList.get(position);
        holder.mTextView.setText(model.targetName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, model.targetClass);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
