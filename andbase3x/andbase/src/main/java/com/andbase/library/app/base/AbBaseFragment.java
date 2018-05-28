package com.andbase.library.app.base;


import android.content.Context;
import android.support.v4.app.Fragment;

import com.andbase.library.R;
import com.andbase.library.util.AbColorUtil;


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

    }

    @Override
    public void onAttach(Context context) {
        this.activity = (AbBaseActivity)context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        this.activity = null;
        super.onDetach();
    }

}
