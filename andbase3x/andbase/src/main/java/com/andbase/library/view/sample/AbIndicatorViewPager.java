package com.andbase.library.view.sample;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.andbase.library.R;
import com.andbase.library.app.adapter.AbViewPagerAdapter;
import com.andbase.library.app.model.AbGalleryTransformer;

import java.util.ArrayList;
import java.util.List;


/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 带指示器的ViewPager
 */
public class AbIndicatorViewPager extends LinearLayout {

	/** 上下文. */
	private Context context;

	/** 内部的ViewPager. */
	private AbInnerViewPager viewPager;

	/** 指示器. */
	private AbIndicatorView indicatorView;

	/** List views. */
	private ArrayList<View> viewList = null;

	/** 适配器. */
	private AbViewPagerAdapter viewPagerAdapter = null;

	/** 子View不越界. */
	private boolean clipChildren = true;

	/**
	 * 创建一个AbIndicatorViewPager.
	 * @param context the context
	 */
	public AbIndicatorViewPager(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * 从xml初始化的AbIndicatorViewPager.
	 * @param context the context
	 * @param attrs the attrs
	 */
	public AbIndicatorViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbIndicatorViewPager);
		clipChildren = typedArray.getBoolean(R.styleable.AbIndicatorViewPager_clip_children, true);
		typedArray.recycle();
		initView(context);
	}
	
	/**
	 * 初始化这个View.
	 * @param context the context
	 */
	public void initView(Context context){
		this.context = context;
		this.setOrientation(LinearLayout.VERTICAL);
		View view = null;
		if(clipChildren){
			view = View.inflate(context, R.layout.ab_indicator_view_pager,null);
		}else{
			view = View.inflate(context, R.layout.ab_indicator_view_pager_clip,null);
		}
		//view.setBackgroundResource(R.color.green);
		this.addView(view,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		viewList = new ArrayList<>();
		viewPager = (AbInnerViewPager) view.findViewById(R.id.view_pager);
		indicatorView = (AbIndicatorView)this.findViewById(R.id.indicator_view);
		viewPagerAdapter = new AbViewPagerAdapter(context, viewList);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setPageMargin(20);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setPageTransformer(false, new AbGalleryTransformer());

		//事件分发，处理页面滑动问题
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return viewPager.dispatchTouchEvent(event);
			}
		});
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				indicatorView.setSelect(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

	}

	/**
	 * 更新
	 */
	public void notifyDataSetChanged(){
		indicatorView.setCount(viewList.size());
		viewPagerAdapter.notifyDataSetChanged();
	}

	/**
	 * 获取这个滑动的ViewPager类.
	 *
	 * @return
	 */
	public AbInnerViewPager getViewPager() {
		return viewPager;
	}
	
	/**
	 * 获取当前的View的数量.
	 *
	 * @return
	 */
	public int getCount() {
		return viewList.size();
	}

	/**
	 * 添加View.
	 * @param views the views
	 */
	public void addItemViews(List<View> views){
		viewList.addAll(views);
		notifyDataSetChanged();
	}

	/**
	 * 添加View.
	 * @param view the view
	 */
	public void addItemView(View view){
		viewList.add(view);
		notifyDataSetChanged();
	}

	/**
	 * 添加View.
	 * @param view the view
	 */
	public void addItemView(View view,boolean notify){
		viewList.add(view);
		if(notify){
			notifyDataSetChanged();
		}

	}

	/**
	 * 删除View.
	 */
	public void removeAllItemViews(){
		viewList.clear();
		notifyDataSetChanged();
	}

	public AbIndicatorView getIndicatorView() {
		return indicatorView;
	}

	/** 用与轮换的 handler. */
	private Handler handler = new Handler();

	/** 用于轮播的线程. */
	private Runnable runnable = new Runnable() {
		public void run() {
			try{
				int count = viewList.size();
				int i = viewPager.getCurrentItem();
				viewPager.setCurrentItem((i+1)%count, true);
				handler.postDelayed(runnable, 5000);
			}catch(Exception e){
				e.printStackTrace();
			}

		}
	};


	/**
	 * 自动轮播.
	 */
	public void startPlay(){
		handler.removeCallbacks(runnable);
		handler.postDelayed(runnable, 5000);
	}

	/**
	 * 自动轮播.
	 */
	public void stopPlay(){
		handler.removeCallbacks(runnable);
	}


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacks(runnable);
                break;
            case MotionEvent.ACTION_UP:
                startPlay();
                break;
            case MotionEvent.ACTION_CANCEL:
                startPlay();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
