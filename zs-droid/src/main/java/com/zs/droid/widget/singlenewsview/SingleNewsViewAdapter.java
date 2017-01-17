package com.zs.droid.widget.singlenewsview;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangshao on 2016/12/26.
 */

public abstract class SingleNewsViewAdapter<T> {

    protected List<T> mList=new ArrayList<>();

    public void addList(List<T> list){

        if(list!=null&&!list.isEmpty()){
            mList.clear();
            mList.addAll(list);
        }
    }
    public void add(T t){
        mList.add(t);
    }
    public int getCount(){
        return mList==null?0:mList.size();
    }
    public abstract View getView(int position, View convertView);

    protected void destroy(){
        if(mList!=null){
            mList.clear();
        }
    }
}
