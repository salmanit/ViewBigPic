package com.sage.test.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sage on 2016/4/20.
 */
public abstract class MySimpleRvAdapter<T> extends RecyclerView.Adapter<MyRvViewHolder> {
    public ArrayList<View> headViews=new ArrayList<>();
    public List<T> lists=new ArrayList<>();
    public MySimpleRvAdapter() {
    }
    public MySimpleRvAdapter(List<T> lists) {
        this.lists = lists;
    }
    public MySimpleRvAdapter(T[] arrays) {
        if(arrays==null){
            this.lists=null;
        }else{
            this.lists= Arrays.asList(arrays);
        }
    }
    public ArrayList<T> getArrayLists(){
        if(lists==null){
            return  null;
        }
        else{
            ArrayList<T> arrayList=new ArrayList<>();
            for(int i=0;i<lists.size();i++){
                arrayList.add(lists.get(i));
            }
            return arrayList;
        }
    }
    public List<T> getLists() {
        return lists;
    }
    public boolean isRefreshHeader(){
        return true;
    }
    public void setLists(List<T> lists) {
        this.lists = lists;
        if(isRefreshHeader()){
            notifyDataSetChanged();
        }else{
            notifyItemRangeChanged(headViews.size(),lists.size());
        }
    }

    public abstract int layoutId(int viewType);
    public abstract void handleData(MyRvViewHolder holder, int position, T data);
    @Override
    public int getItemViewType(int position) {
        for(int i=0;i<headViews.size();i++){
            if(i==position)
                return i;
        }
        return -1;
    }

    public void addHeader(View header){
        headViews.add(header);
    }
    @Override
    public MyRvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        for (int i=0;i<headViews.size();i++){
            if(i==viewType)
                return MyRvViewHolder.get(parent,headViews.get(i));
        }
        return MyRvViewHolder.get(parent,layoutId(viewType));
    }

    @Override
    public void onBindViewHolder(MyRvViewHolder holder, final int position) {
        if(position>headViews.size()-1) {
            final int which = position - headViews.size();
            handleData(holder, which, getItem(which));
            if (mOnRvItemClickListener != null) {
                holder.getmConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnRvItemClickListener.onItemClick(which, getItem(which));
                    }
                });
            }
            if(mOnRvItemLongClickListener!=null){
                holder.getmConvertView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnRvItemLongClickListener.onItemLongClick(which,getItem(which));
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        int add=headViews.size();
        return lists==null?add:lists.size()+add;
    }

    public T getItem(int position){
        if(position>=lists.size()){
            return null;
        }
        return lists.get(position);
    }


    public interface OnRvItemClickListener<T>{
        public abstract void onItemClick(int position, T data);
    }

    public OnRvItemClickListener<T> mOnRvItemClickListener;

    public OnRvItemClickListener<T> getmOnRvItemClickListener() {
        return mOnRvItemClickListener;
    }

    public void setmOnRvItemClickListener(OnRvItemClickListener<T> mOnRvItemClickListener) {
        this.mOnRvItemClickListener = mOnRvItemClickListener;
    }

    public interface OnRvItemLongClickListener<T>{
        public abstract void onItemLongClick(int position, T data);
    }

    public OnRvItemLongClickListener<T> mOnRvItemLongClickListener;

    public OnRvItemLongClickListener<T> getmOnRvItemLongClickListener() {
        return mOnRvItemLongClickListener;
    }

    public void setmOnRvItemLongClickListener(OnRvItemLongClickListener<T> mOnRvItemLongClickListener) {
        this.mOnRvItemLongClickListener = mOnRvItemLongClickListener;
    }



}
