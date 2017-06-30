package com.sage.bigscalephotoviewanim.choose;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sage.bigscalephotoviewanim.R;


import java.io.File;
import java.util.List;

/**
 * Created by Sage on 2015/11/13.
 */
public class AdapterChoosePic extends BaseAdapter {


    private List<File> names;
    private ActivityChoosePic  activity;

    public AdapterChoosePic(List<File> names, ActivityChoosePic activity) {
        this.names = names;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return names==null?0:names.size();
    }

    @Override
    public File getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_pic,parent,false);
        }
        final ImageView iv_show= (ImageView) convertView.findViewById(R.id.iv_show);
        final ImageView iv_state= (ImageView) convertView.findViewById(R.id.iv_state);
        final String path=getItem(position).getAbsolutePath();

      try {
          ImageLoader.getInstance().displayImage("file://" + path, iv_show);
          View.OnClickListener l=new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  if (activity.mSelectedImage.contains(path)) {
                      activity.mSelectedImage.remove(path);
                      iv_state.setImageResource(R.drawable.picture_unselected);
                      iv_show.setColorFilter(null);
                  } else {
                      if(activity.canSelect()){
                          activity.mSelectedImage.add(path);
                          iv_state.setImageResource(R.drawable.pictures_selected);
                          iv_show.setColorFilter(Color.parseColor("#99000000"));
                          ImageChooseUtils.saveSmallPic(path);
                      }

                  }
                  activity.change();
              }
          };
          //iv_state.setOnClickListener(l);
          iv_show.setOnClickListener(l);
          if (activity.mSelectedImage.contains(path)) {
              iv_state.setImageResource(R.drawable.pictures_selected);
              iv_show.setColorFilter(Color.parseColor("#99000000"));
          } else {
              iv_state.setImageResource(R.drawable.picture_unselected);
              iv_show.setColorFilter(null);
          }
      }catch (Exception e){
          e.printStackTrace();
      }
        return convertView;
    }
}
