package com.zs.droid;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by zhangshao on 2017/1/17.
 */

public class MainListHolder extends RecyclerView.ViewHolder {

    public TextView mTextView;

    public MainListHolder(View itemView) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(R.id.textview);
    }


}
