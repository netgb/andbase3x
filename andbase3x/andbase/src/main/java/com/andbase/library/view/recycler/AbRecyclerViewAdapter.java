package com.andbase.library.view.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public abstract class AbRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private View headerView;
    private View footerView;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_FOOTER = 3;
    public static final int TYPE_NORMAL = 2;
    private List<?> list;

    public AbRecyclerViewAdapter(Context context, List<?> list) {
        this.context = context;
        this.list = list;
    }

    /**
     * 需要重写的
     * @param parent
     * @param viewType
     * @return
     */
    public abstract  RecyclerView.ViewHolder onCreateRecyclerViewHolder(ViewGroup parent, int viewType);

    /**
     * 需要重写的
     * @param holder
     * @param position
     */
    public abstract void onBindRecyclerViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(headerView != null && viewType == TYPE_HEADER) {
            return new AbHeaderRecyclerViewHolder(headerView);
        }else if(footerView != null && viewType == TYPE_FOOTER) {
            return new AbFooterRecyclerViewHolder(footerView);
        }else{
            return onCreateRecyclerViewHolder(parent,viewType);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,int position) {
        int type = getItemViewType(position);
        if(type == TYPE_HEADER){
        }
        else  if(type == TYPE_FOOTER) {

        }else if(type == TYPE_NORMAL) {
            int index = position;
            if(headerView != null){
                index = position - 1;
            }
            onBindRecyclerViewHolder(holder,index);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null){
            return TYPE_HEADER;
        } else if (position == getItemCount()-1 && footerView != null){
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        int count = list.size();
        if(headerView != null){
            count ++;
        }
        if(footerView != null){
            count ++;
        }
        return count;
    }

    public void removeItem(int index){
        list.remove(index);
        notifyDataSetChanged();
    }

    public void setList(List<?> list) {
        this.list = list;
    }


    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }


    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }
}
