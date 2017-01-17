package com.zs.droid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;

import com.zs.droid.singlenewview.SingleNewsViewActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    static List<ListItemModel> list=new ArrayList<>();
    static {
        list.add(new ListItemModel(SingleNewsViewActivity.class));
    }
    RecyclerView mRecylcerView;
    MainListAdapter mMainListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mRecylcerView=(RecyclerView)findViewById(R.id.list);
        mRecylcerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecylcerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));
        mMainListAdapter=new MainListAdapter(getApplicationContext());
        mRecylcerView.setAdapter(mMainListAdapter);
        mMainListAdapter.addAll(list);
    }
}
