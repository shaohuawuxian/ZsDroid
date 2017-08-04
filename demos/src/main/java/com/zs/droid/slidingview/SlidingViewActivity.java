package com.zs.droid.slidingview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.zs.droid.BaseActivity;
import com.zs.droid.R;
import com.zs.droid.utils.AndroidUtils;
import com.zs.droid.widget.SlidingView;

/**
 * Created by zhangshao on 2017/8/2.
 */

public class SlidingViewActivity extends BaseActivity {

    SlidingView mSlidingView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSlidingView=new SlidingView(getApplicationContext(),
                AndroidUtils.getDisplayMetrics(this).widthPixels,AndroidUtils.getDisplayMetrics(this).heightPixels);
        View mianView= LayoutInflater.from(this).inflate(R.layout.slidingview_main,null);
        View leftView= LayoutInflater.from(this).inflate(R.layout.slidingview_left,null);
        View rightView= LayoutInflater.from(this).inflate(R.layout.slidingview_right,null);
        mSlidingView.initScreenSize(mianView,leftView,rightView);
        setContentView(mSlidingView);
    }
}
