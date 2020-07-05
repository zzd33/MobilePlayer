package com.bobo.mobileplayer.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Leon on 2018/1/1.
 * Functions:继承Fragment重写onCreateView方法实现类的加载
 */

@SuppressLint("ValidFragment")
public class MyFragment extends Fragment {

    private BasePager basePager;

    @SuppressLint("ValidFragment")
    public MyFragment(BasePager basePager) {
        super();
        this.basePager = basePager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (basePager != null){
            //各个页面的视图
            return basePager.rootView;
        }
        return null;
    }
}
