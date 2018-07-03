package com.upu173.andbase.global;

import android.app.Application;
import android.content.SharedPreferences;
import com.andbase.library.app.base.AbBaseActivity;
import com.andbase.library.util.AbSharedUtil;
import com.upu173.andbase.R;

/**
 * Created by Administrator on 2017/1/16.
 */

public class MyApplication extends Application {

    public SharedPreferences sharedPreferences = null;

    /**当前主题的索引*/
    public int themeIndex = 0;

    public int[] themeArray = new int[]{R.style.AppThemeBlue,R.style.AppThemeGreen,R.style.AppThemeRed};

    /** 信息有变化 */
    public boolean isThemeUpdate = false;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = AbSharedUtil.getDefaultSharedPreferences(this);
        initTheme();

    }

    public void initTheme(){
        int themeId = sharedPreferences.getInt(AbBaseActivity.THEME_ID,-1);
        if(themeId==-1){
            themeId = themeArray[themeIndex];
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(AbBaseActivity.THEME_ID, themeId);
            editor.commit();
        }
        this.setTheme(themeId);
    }

    public void updateTheme(int position){
        this.themeIndex = position;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(AbBaseActivity.THEME_ID);
        editor.putInt(AbBaseActivity.THEME_ID, themeArray[themeIndex]);
        editor.commit();
        this.isThemeUpdate = true;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
