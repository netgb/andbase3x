package com.andbase.library.app.base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andbase.library.util.AbLogUtil;


/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/16 13:27
 * Email 396196516@qq.com
 * Info 所有Fragment要继承这个父类，便于统一管理
 */
public class AbBaseFragment extends Fragment {

    public AbBaseActivity activity;

    public AbBaseFragment() {
        AbLogUtil.e(this.getClass(),"------------------Fragment构造-----------------------");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AbLogUtil.e(this.getClass(),"------------------onCreateView-----------------------");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        this.activity = (AbBaseActivity)context;
        AbLogUtil.e(this.getClass(),"------------------onAttach-----------------------");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        AbLogUtil.e(this.getClass(),"------------------onDetach-----------------------");
        this.activity = null;
        super.onDetach();
    }

    @Override
    public void onStart() {
        AbLogUtil.e(this.getClass(),"------------------onStart-----------------------");
        super.onStart();
    }

    @Override
    public void onResume() {
        AbLogUtil.e(this.getClass(),"------------------onResume-----------------------");
        super.onResume();
    }

    @Override
    public void onPause() {
        AbLogUtil.e(this.getClass(),"------------------onResume-----------------------");
        super.onPause();
    }

    @Override
    public void onStop() {
        AbLogUtil.e(this.getClass(),"------------------onStop-----------------------");
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        AbLogUtil.e(this.getClass(),"------------------onLowMemory-----------------------");
        super.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        AbLogUtil.e(this.getClass(),"------------------onDestroyView-----------------------");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        AbLogUtil.e(this.getClass(),"------------------onDestroy-----------------------");
        super.onDestroy();
    }
}
