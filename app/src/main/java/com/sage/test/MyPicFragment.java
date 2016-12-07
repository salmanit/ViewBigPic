package com.sage.test;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sage.bigscalephotoviewanim.ViewPagerFragment;

/**
 * Created by Sage on 2016/12/7.
 */

public class MyPicFragment extends ViewPagerFragment {
    public static MyPicFragment getInstance(Bundle imgs) {
        MyPicFragment fragment = new MyPicFragment();
        fragment.setArguments(imgs);
        return fragment;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tv_action= (TextView) view.findViewById(R.id.tv_action);
        tv_action.setTextColor(Color.RED);
        System.out.println("--------------------------");
        tv_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("+++++++++++++++++++");
                showToast("just ignore what just happened,okey?");
            }
        });
    }

    public void showToast(String msg){
        Toast.makeText(getActivity(),msg+"\n"+getCurrentPic(),Toast.LENGTH_SHORT).show();
    }
}
