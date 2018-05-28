package com.andbase.library.view.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2017/6/16 13:27
 * Email 396196516@qq.com
 * Info  网格 RecyclerView 边距设置
 */

public class AbSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int column;
    private boolean hasHeader;
    private boolean hasFooter;

    public AbSpaceItemDecoration(int space, int column) {
        this.space = space;
        this.column = column;
        this.hasHeader = false;
    }

    public AbSpaceItemDecoration(int space, int column,boolean hasHeader,boolean hasFooter) {
        this.space = space;
        this.column = column;
        this.hasHeader = hasHeader;
        this.hasFooter = hasFooter;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        outRect.bottom = space;
        int position = parent.getChildLayoutPosition(view);
        if(hasHeader){
            if(position == 0){
                outRect.left = 0;
                outRect.right = 0;
            }else if(position % column == 0){
                outRect.left = space;
                outRect.right = space;
            }else{
                outRect.left = space;
                outRect.right = 0;
            }


        }else{
            if((position+1) % column == 0){
                outRect.left = space;
                outRect.right = space;
            }else{
                outRect.left = space;
                outRect.right = 0;
            }
        }

        //最后一个是footer
        if(hasFooter){
            if(view instanceof AbFooterView){
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = 0;
            }
        }


    }

}
