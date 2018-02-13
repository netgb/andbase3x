package com.upu173.andbase.global;

import android.app.Application;
import android.content.SharedPreferences;
import com.andbase.library.global.AbConstant;
import com.andbase.library.http.AbHttpUtil;
import com.andbase.library.http.listener.AbStringHttpResponseListener;
import com.andbase.library.http.model.AbRequestParams;
import com.andbase.library.http.model.AbResult;
import com.andbase.library.util.AbFileUtil;
import com.andbase.library.util.AbJsonUtil;
import com.andbase.library.util.AbSharedUtil;
import com.andbase.library.util.AbStrUtil;
import com.upu173.andbase.R;

/**
 * Created by Administrator on 2017/1/16.
 */

public class MyApplication extends Application {

    public SharedPreferences sharedPreferences = null;

    /** 主题*/
    public int themeId = -1;

    public boolean isLogin = false;

    /**HTTP*/
    public AbHttpUtil httpUtil;


    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = AbSharedUtil.getDefaultSharedPreferences(this);
        initTheme();

    }

    public void initTheme(){

        themeId = sharedPreferences.getInt(AbConstant.THEME_ID,-1);
        if(themeId==-1){
            themeId = R.style.AppTheme1;
            this.setTheme(themeId);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(AbConstant.THEME_ID, themeId);
            editor.commit();

        }
    }

    public void updateTheme(int themeId){
        this.themeId = themeId;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(AbConstant.THEME_ID);
        editor.putInt(AbConstant.THEME_ID, this.themeId);
        editor.commit();
    }


    public void logout(){
        isLogin = false;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
