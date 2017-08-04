package com.zs.droid.singlenewview;

import android.os.Bundle;
import com.zs.droid.BaseActivity;
import com.zs.droid.R;
import com.zs.droid.widget.singlenewsview.SingleNewsView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangshao on 2017/1/16.
 */

public class SingleNewsViewActivity extends BaseActivity {

    SingleNewsView mVerticalView, mHorizontalView;
    FlashNewsAdapter mVerticalAdapter, mHorizontalAdapter;
    static List<FlashNewsEntry> list = new ArrayList<>();

    {
        list.add(new FlashNewsEntry("图书满200减100", "促销"));
        list.add(new FlashNewsEntry("女装满599减200", "优惠券"));
        list.add(new FlashNewsEntry("男装满200减100", "满减"));
        list.add(new FlashNewsEntry("童书满150见50", "童书"));
        list.add(new FlashNewsEntry("跨店满200见100", "促销"));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashnewslayout);
        mVerticalView = (SingleNewsView) findViewById(R.id.autoview_v_aview);
        mVerticalView.setOrientation(SingleNewsView.VERTICAL);
        mVerticalView.setInterval(1500);
        mVerticalAdapter = new FlashNewsAdapter(getApplicationContext());
        mVerticalAdapter.addList(list);
        mVerticalView.setAdapter(mVerticalAdapter);
        mVerticalView.autoPlay();

        mHorizontalView = (SingleNewsView) findViewById(R.id.autoview_h_aview);
        mHorizontalView.setOrientation(SingleNewsView.HORIZONTAL);
        mHorizontalView.setInterval(2000);
        mHorizontalAdapter = new FlashNewsAdapter(getApplicationContext());
        mHorizontalAdapter.addList(list);
        mHorizontalView.setAdapter(mHorizontalAdapter);
        mHorizontalView.autoPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVerticalView != null) {
            mVerticalView.onDestroy();
        }
        if (mHorizontalView != null) {
            mHorizontalView.onDestroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVerticalView != null) {
            mVerticalView.onPause();
        }
        if (mHorizontalView != null) {
            mHorizontalView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVerticalView != null) {
            mVerticalView.onResume();
        }
        if (mHorizontalView != null) {
            mHorizontalView.onResume();
        }
    }
}
