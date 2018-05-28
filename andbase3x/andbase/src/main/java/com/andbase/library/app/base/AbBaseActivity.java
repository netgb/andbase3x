package com.andbase.library.app.base;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.andbase.library.R;
import com.andbase.library.app.global.AbActivityManager;
import com.andbase.library.http.AbHttpUtil;
import com.andbase.library.image.AbImageLoader;
import com.andbase.library.util.AbColorUtil;
import com.andbase.library.util.AbSharedUtil;

/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/16 13:27
 * Email 396196516@qq.com
 * Info 所有Activity要继承这个父类，便于统一管理
 * 自动加载SharedPreferences中的key为AbConstant.THEME_ID ="themeId"的主题
 */
public abstract class AbBaseActivity extends AppCompatActivity {

	/** 主题的key. */
	public static final String THEME_ID = "themeId";

    /** 主题*/
	private int themeId = -1;

    /** 网络请求 */
	public AbHttpUtil httpUtil = null;

    /** 图片下载 */
    public AbImageLoader imageLoader = null;

	public int colorPrimary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        themeId = AbSharedUtil.getInt(this, THEME_ID,-1);

        if(themeId!=-1){
            this.setTheme(themeId);
        }else{
            this.setTheme(android.R.style.Theme_NoTitleBar);
        }
		super.onCreate(savedInstanceState);

		this.colorPrimary = AbColorUtil.getAttrColor(this,R.attr.colorPrimary);
		
		AbActivityManager.getInstance().addActivity(this);
		httpUtil = AbHttpUtil.getInstance(this);
        imageLoader = AbImageLoader.getInstance(this);
    }

	
	/**
	 * 设置主题ID
	 * @param themeId
	 */
    public void setAppTheme(int themeId){
		this.themeId = themeId;
        this.recreate();  
    }
    
    /**
	 * 返回默认
	 * @param view
	 */
	public void back(View view){
		finish();
	}

    /**
	 * 结束
	 */
	@Override
	public void finish() {
		AbActivityManager.getInstance().removeActivity(this);
		if(httpUtil!=null){
			httpUtil.cancelCurrentTask();
			httpUtil = null;
		}
		if(imageLoader!=null){
			imageLoader.cancelCurrentTask();
			imageLoader = null;
		}

		super.finish();
	}

}

