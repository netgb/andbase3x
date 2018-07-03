package com.andbase.library.view.sample;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.andbase.library.view.refresh.AbPullToRefreshView;


/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info scrollView与内部ViewPager滑动的XY冲突
 */
public class AbInnerViewPager extends ViewPager {

	/** 父滚动布局 */
	private ViewGroup parentView;

	/** 手势. */
	private GestureDetector gestureDetector;

	/**
	 * 构造函数.
	 * @param context the context
	 */
	public AbInnerViewPager(Context context) {
		super(context);
		gestureDetector = new GestureDetector(new YScrollDetector());
		setFadingEdgeLength(0);
	}

	/**
	 * 构造函数.
	 * @param context the context
	 * @param attrs the attrs
	 */
	public AbInnerViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		gestureDetector = new GestureDetector(new YScrollDetector());
		setFadingEdgeLength(0);
	}
	
	/**
	 * 拦截事件.
	 * @param ev the ev
	 * @return true, if successful
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev) && gestureDetector.onTouchEvent(ev);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		int action = ev.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				if (parentView != null){
					if(parentView instanceof AbPullToRefreshView){
						AbPullToRefreshView pullToRefreshView = (AbPullToRefreshView)parentView;
						pullToRefreshView.setEnabled(false);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if (parentView != null){
					if(parentView instanceof AbPullToRefreshView){
						AbPullToRefreshView pullToRefreshView = (AbPullToRefreshView)parentView;
						pullToRefreshView.setEnabled(true);
					}
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				if (parentView != null){
					if(parentView instanceof AbPullToRefreshView){
						AbPullToRefreshView pullToRefreshView = (AbPullToRefreshView)parentView;
						pullToRefreshView.setEnabled(true);
					}
				}
				break;
		}
		return super.dispatchTouchEvent(ev);
	}


	/**
	 * 设置父级的View.
	 * @param flag 父是否滚动开关
	 */
	private void setParentScrollAble(boolean flag) {
		if(parentView!=null){
			parentView.requestDisallowInterceptTouchEvent(!flag);
		}

	}

	/**
	 * 如果外层有滚动需要设置.
	 * @param parentView the parent view
	 */
	public void setParentView(ViewGroup parentView) {
		this.parentView = parentView;
	}


	/**
	 * 手势类.
	 */
	class YScrollDetector extends SimpleOnGestureListener {
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			
			if (Math.abs(distanceX) >= Math.abs(distanceY)/2) {
				//父亲不滑动
				setParentScrollAble(false);
				return true;
			}else{
				setParentScrollAble(true);
			}
			return false;
		}
	}

}
