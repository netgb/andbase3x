package com.andbase.library.app.base;


import android.content.Context;
import android.support.v4.app.Fragment;

import com.andbase.library.http.AbHttpUtil;
import com.andbase.library.image.AbImageLoader;


/**
 * Copyright amsoft.cn
 * Author 还如一梦中
 * Date 2016/6/16 13:27
 * Email 396196516@qq.com
 * Info 所有Fragment要继承这个父类，便于统一管理
 */
public class AbBaseFragment extends Fragment {

    public AbBaseActivity activity;

    /** 当Activity结束会中止请求*/
    public AbHttpUtil httpUtil = null;

    /** 当Activity结束会中止请求*/
    public AbImageLoader imageLoader = null;


    @Override
    public void onAttach(Context context) {
        this.activity = (AbBaseActivity)context;
        this.httpUtil = this.activity.httpUtil;
        this.imageLoader = this.activity.imageLoader;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        this.activity = null;
        this.httpUtil = null;
        this.imageLoader = null;
        super.onDetach();
    }
}
