package com.andbase.library.view.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.andbase.library.view.listener.AbOnScrollChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2017/4/24 13:27
 * Email 396196516@qq.com
 * Info 返回滚动距离的ScrollView
 */
public class AbScrollView extends ScrollView {

    private List<AbOnScrollChangedListener> scrollChangedListeners;

    public AbScrollView(Context context) {
        this(context, null);
    }

    public AbScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addOnScrollChangedListener(AbOnScrollChangedListener onScrollChangedListener) {
        if(this.scrollChangedListeners == null) {
            this.scrollChangedListeners = new ArrayList();
        }
        this.scrollChangedListeners.add(onScrollChangedListener);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.scrollChangedListeners != null && this.scrollChangedListeners.size() > 0) {
            for(AbOnScrollChangedListener listener:this.scrollChangedListeners){
                if(listener!=null){
                    listener.onScrollChanged(l, t, oldl, oldt);
                }

            }

        }
    }

}


