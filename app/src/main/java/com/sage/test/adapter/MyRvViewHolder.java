package com.sage.test.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**w
 * Created by Sage on 2016/4/20.
 */
public class MyRvViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    private View mConvertView;
    public ViewGroup parent;
    private MyRvViewHolder(View itemView, ViewGroup parent) {
        super(itemView);
        mConvertView = itemView;
        this.parent=parent;
        mViews = new SparseArray<>();
    }


    public static MyRvViewHolder get(ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent,false);
        return new MyRvViewHolder(itemView, parent);
    }
    public static MyRvViewHolder get(ViewGroup parent, View itemView) {
        return new MyRvViewHolder(itemView, parent);
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


    public View getmConvertView(){
        return mConvertView;
    }

    public void setText(int viewId,CharSequence msg){
        TextView textView=getView(viewId);
        textView.setText(msg);
        textView.setVisibility(View.VISIBLE);
    }
    public void setImageResource(int viewId,int resId){
        ImageView imageView=getView(viewId);
        imageView.setImageResource(resId);
    }
//    public void setImageUri(int viewId,String uri){
//        setImageUri(viewId,uri, Utils.getHeaderOption());
//    }
//    public void setPicUri(int viewId,String uri){
//        setImageUri(viewId,uri, Utils.getPicOption(R.mipmap.default_error_icon));
//    }
//    public void setMoudleUri(int viewId,String uri){
//        setImageUri(viewId,uri, Utils.getPicOption(R.mipmap.mouder_icon));
//    }
    public void setImageUri(int viewId,String uri,DisplayImageOptions options){
        ImageView imageView=getView(viewId);
        ImageLoader.getInstance().displayImage(uri,imageView,options);
    }

    public void setTextVisible(int viewId,String text){
        setTextVisibleGone(viewId,true,text);
    }
    public void setViewGone(int viewId){
        getView(viewId).setVisibility(View.GONE);
    }
    public void setViewVisibleGone(int viewId,boolean visible){
        getView(viewId).setVisibility(visible?View.VISIBLE:View.GONE);
    }
    public void setTextVisibleGone(int viewId,boolean visibility,String text){
        TextView textView=getView(viewId);
        textView.setVisibility(visibility?View.VISIBLE:View.GONE);
        textView.setText(text);
    }
}