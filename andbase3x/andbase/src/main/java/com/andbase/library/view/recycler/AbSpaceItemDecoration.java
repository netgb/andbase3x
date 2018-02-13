package com.andbase.library.view.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Think on 2017/5/18.
 */

public class AbSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int column;

    public AbSpaceItemDecoration(int space, int column) {
        this.space = space;
        this.column = column;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = 0;
        outRect.bottom = space;
        outRect.right = space;
        //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
        if (parent.getChildLayoutPosition(view) % column == 0) {
            outRect.left = space;
        }
    }

}
