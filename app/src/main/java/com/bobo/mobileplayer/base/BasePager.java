package com.bobo.mobileplayer.base;

import android.content.Context;
import android.view.View;

/**
 * Created by Leon on 2017/12/31.
 * Functions:基类，公共类
 * VideoPage
 * AudioPage
 * NetVideoPage
 * NetAudioPage
 * 继承BasePage
 */

public abstract class BasePager {

    /**上下文*/
    public final Context context;
    public View rootView;
    public boolean isInitData = false;//页面的初始化状态默认为false

    public BasePager(Context context){
        this.context = context;
        rootView = initView();
    }
    /**强制由子类实现特定的效果*/
    public abstract View initView();
    /**当子页面需要初始化数据，联网请求数据，或者绑定数据的时候要重写该方法*/
    public void initData(){

    }
}
