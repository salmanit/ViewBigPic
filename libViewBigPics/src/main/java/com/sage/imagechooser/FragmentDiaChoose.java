package com.sage.imagechooser;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sage.bigscalephotoviewanim.R;

import java.util.List;

/**
 * Created by Sage on 2015/7/27.
 */
public class FragmentDiaChoose extends DialogFragment implements View.OnClickListener{
    ListView lv_choose;
    private int tag;//主要防止同一个页面多次调用这个，区分点击的是哪个
    String[] data;
    /**0，默认的文字是相机相册，1男女的选择*/
    public static FragmentDiaChoose create(int tag,String[] data){
        FragmentDiaChoose fragment=new FragmentDiaChoose();
        Bundle bundle=new Bundle();
        bundle.putInt("tag",tag);
        bundle.putStringArray("data",data);
        fragment.setArguments(bundle);
        return fragment;
    }
    /**0，默认的文字是相机相册，1男女的选择*/
    public static FragmentDiaChoose create(int tag,List<String> data){
        FragmentDiaChoose fragment=new FragmentDiaChoose();
        Bundle bundle=new Bundle();
        bundle.putInt("tag", tag);
        if(data!=null){
            bundle.putStringArray("data",data.toArray(new String[data.size()]));
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    public FragmentDiaChoose() {
        setStyle(STYLE_NO_TITLE, R.style.MyCustomTheme);//取消标题
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dia_choose, container, false);
        lv_choose= (ListView) view.findViewById(R.id.lv_choose);
        return view;
    }

    private void initData(){
        if(getArguments()!=null){
            tag=getArguments().getInt("tag");
            data=getArguments().getStringArray("data");
        }
        getView().findViewById(R.id.tv_cancel).setOnClickListener(this);
        lv_choose.setAdapter(new SimpleAdapter());
        lv_choose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                which(position);
                dismiss();
            }
        });
    }
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        initData();
        Window window=getDialog().getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;//这里的坐标是相对默认重心 center而言的。
        wl.y = getResources().getDisplayMetrics().heightPixels;
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        wl.dimAmount=0.6f;
        wl.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        // 设置显示位置
        getDialog().onWindowAttributesChanged(wl);
        //window.setAttributes(wl);
        // 设置点击外围隐藏
        getDialog().setCanceledOnTouchOutside(true);

    }


    public void onClick(View v) {
        dismiss();
    }

    private void which(int index){
        if(mChooseListener!=null){
            mChooseListener.click(index,tag);
        }
    }

    public interface ChooseClickListener{
           void click(int index, int tag);
    }

    public FragmentDiaChoose setmChooseListener(ChooseClickListener mChooseListener) {
        this.mChooseListener = mChooseListener;
        return this;
    }

    private ChooseClickListener mChooseListener;


    public class SimpleAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return data==null?0:data.length;
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
            if(convertView==null){
                convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_text,parent,false);
            }
            ((TextView)convertView).setText(data[position]);
            return convertView;
        }
    }

}
