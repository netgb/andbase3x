package com.andbase.library.app.base;


import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.andbase.library.R;
import com.andbase.library.app.global.AbActivityManager;
import com.andbase.library.http.AbHttpUtil;
import com.andbase.library.image.AbImageLoader;
import com.andbase.library.util.AbColorUtil;
import com.andbase.library.util.AbLogUtil;
import com.andbase.library.util.AbSharedUtil;
import com.andbase.library.util.AbViewUtil;

/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/16 13:27
 * Email 396196516@qq.com
 * Info 所有Activity要继承这个父类，便于统一管理
 * 自动加载SharedPreferences中的key为"themeId"的主题
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

	/** 主色调 */
	public int colorPrimary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

    	//有设置主题
        themeId = AbSharedUtil.getInt(this, THEME_ID,-1);

        if(themeId!=-1){
            this.setTheme(themeId);
        }

		super.onCreate(savedInstanceState);

		AbLogUtil.e(this,"------------------onCreate-----------------------");

        //主色调
		this.colorPrimary = AbColorUtil.getAttrColor(this,R.attr.colorPrimary);
		
		AbActivityManager.getInstance().addActivity(this);
		httpUtil = AbHttpUtil.getInstance(this);
        imageLoader = AbImageLoader.getInstance(this);
    }

	/**
	 * 万能屏幕适配的setContentView方法
	 * @param layoutResID  布局ID
	 */
	public void setAbContentView(@LayoutRes int layoutResID) {
		View contentView = View.inflate(this,layoutResID,null);
		AbViewUtil.scaleContentView(contentView);
		setContentView(contentView);
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

    @Override
	protected void onStart() {
        AbLogUtil.e(this,"------------------onStart-----------------------");
        super.onStart();
    }

    @Override
	protected void onResume() {
        AbLogUtil.e(this,"------------------onResume-----------------------");
        super.onResume();
    }

    @Override
	protected void onPause() {
        AbLogUtil.e(this,"------------------onResume-----------------------");
        super.onPause();
    }

    @Override
	protected void onStop() {
        AbLogUtil.e(this,"------------------onStop-----------------------");
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        AbLogUtil.e(this,"------------------onLowMemory-----------------------");
        super.onLowMemory();
    }


    @Override
	protected void onDestroy() {
        AbLogUtil.e(this,"------------------onDestroy-----------------------");
        super.onDestroy();
    }

    /**
	 * 结束
	 */
	@Override
	public void finish() {
		AbLogUtil.e(this,"------------------finish-----------------------");
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

