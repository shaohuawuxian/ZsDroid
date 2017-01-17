package com.zs.droid.singlenewview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zs.droid.R;
import com.zs.droid.widget.singlenewsview.SingleNewsViewAdapter;

/**
 * Created by zhangshao on 2017/1/4.
 * 当当快讯的Adapter，首页和主题馆在使用中……
 */

public class FlashNewsAdapter extends SingleNewsViewAdapter<FlashNewsEntry> {
    Context mContext;

    public FlashNewsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.flashnewslayout_item, null);
            viewHolder = new ViewHolder();
            viewHolder.promoText = (TextView) convertView.findViewById(R.id.flashnews_item_promo);
            viewHolder.contextText = (TextView) convertView.findViewById(R.id.flashnews_item_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final FlashNewsEntry entry = mList.get(position);
        viewHolder.promoText.setText(entry.entryTip);
        viewHolder.contextText.setText(entry.entryContent);
        final int pos = position;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView promoText = null;
        TextView contextText = null;
    }
}
