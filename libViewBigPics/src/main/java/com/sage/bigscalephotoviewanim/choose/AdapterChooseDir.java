package com.sage.bigscalephotoviewanim.choose;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sage.bigscalephotoviewanim.R;

import java.util.List;

/**
 * Created by Sage on 2015/11/13.
 */
public class AdapterChooseDir extends BaseAdapter {
    List<ImageFolder> datas;

    public AdapterChooseDir(List<ImageFolder> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas==null?0:datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_dir, parent, false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        ImageFolder imageFolder=datas.get(position);
        ImageLoader.getInstance().displayImage("file://" + imageFolder.getFirstImagePath(), holder.idDirItemImage);
        holder.idDirItemName.setText(imageFolder.getName());
        holder.idDirItemCount.setText(imageFolder.getCount()+"");
        return convertView;
    }

    static class ViewHolder {
        ImageView idDirItemImage;
        TextView idDirItemName;
        TextView idDirItemCount;

        ViewHolder(View view) {
          idDirItemImage= (ImageView) view.findViewById(R.id.id_dir_item_image);
            idDirItemName= (TextView) view.findViewById(R.id.id_dir_item_name);
            idDirItemCount= (TextView) view.findViewById(R.id.id_dir_item_count);
        }
    }
}
