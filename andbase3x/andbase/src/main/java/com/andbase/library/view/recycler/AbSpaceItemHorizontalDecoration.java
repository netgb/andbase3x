package com.andbase.library.view.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2017/6/16 13:27
 * Email 396196516@qq.com
 * Info  水平 RecyclerView 边距设置
 */

public class AbSpaceItemHorizontalDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public AbSpaceItemHorizontalDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space/2;
        outRect.top = space;
        outRect.bottom = space;
        outRect.right = space/2;
    }

}
