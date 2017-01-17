package com.zs.droid;

/**
 * Created by zhangshao on 2017/1/17.
 */

public class ListItemModel {
    public ListItemModel(Class targetClass){
        this.targetClass=targetClass;
        targetName=targetClass.getSimpleName();
    }
    String targetName;
    Class targetClass;
}
