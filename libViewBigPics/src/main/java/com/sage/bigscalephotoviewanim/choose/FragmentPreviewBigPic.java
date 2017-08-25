package com.sage.bigscalephotoviewanim.choose;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sage.bigscalephotoviewanim.R;
import com.sage.bigscalephotoviewanim.widget.PhotoView;
import com.sage.bigscalephotoviewanim.widget.ReboundViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruce Too
 * On 9/28/15.
 * At 19:37
 * ViewPagerFragment add into MainActivity
 */
public class FragmentPreviewBigPic extends Fragment {

    TextView include_title;

    private ReboundViewPager viewPager;
    private List<String> pics;

    public static FragmentPreviewBigPic getInstance(ArrayList<String> pics) {
        FragmentPreviewBigPic fragment = new FragmentPreviewBigPic();
        Bundle bundle=new Bundle();
        bundle.putSerializable("data", pics);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_preview_big_pics, null);
        include_title= (TextView) view.findViewById(R.id.include_title);
        return view;
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().findViewById(R.id.include_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        viewPager = (ReboundViewPager) view.findViewById(R.id.viewpager);

        Bundle bundle = getArguments();
        pics = bundle.getStringArrayList("data");
        if(pics==null){
            return;
        }
        include_title.setText("1/"+pics.size());
        viewPager.getOverscrollView().setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return pics.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int pos) {
                PhotoView photoView=new PhotoView(container.getContext());
                photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                ImageLoader.getInstance().displayImage("file://" + pics.get(pos), photoView);
                photoView.enable();
                photoView.setOnClickListener(onClickListener);
                container.addView(photoView);

                return photoView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });

        viewPager.getOverscrollView().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                include_title.setText((position + 1) + "/" + pics.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popFragment();
        }
    };
    private void popFragment() {
        if (!FragmentPreviewBigPic.this.isResumed()) {
            return;
        }
        final FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.popBackStack();
        }
    }

}
