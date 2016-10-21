package com.sage.bigscalephotoviewanim;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sage.bigscalephotoviewanim.common.CommonTag;
import com.sage.bigscalephotoviewanim.common.CommonUtils;
import com.sage.bigscalephotoviewanim.widget.ImageInfo;
import com.sage.bigscalephotoviewanim.widget.MaterialProgressBar;
import com.sage.bigscalephotoviewanim.widget.PhotoView;
import com.sage.bigscalephotoviewanim.widget.ReboundViewPager;
import com.sage.imagechooser.FragmentDiaChoose;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by Bruce Too
 * On 9/28/15.
 * At 19:37
 * ViewPagerFragment add into MainActivity
 */
public class ViewPagerFragment extends Fragment {

    private ReboundViewPager viewPager;
    private TextView tips; //viewpager indicator
    private ArrayList<String> imgs;
    private ImageInfo imageInfo;
    private View mask;//background view
    private ArrayList<ImageInfo> imageInfos;
    private int position;
    private boolean first=true;
    public static ViewPagerFragment getInstance(Bundle imgs) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        fragment.setArguments(imgs);
        return fragment;
    }
    /**@param urls 图片地址集合
     * @param infos 所有的图片info信息集合
     * @param imageInfo 点击的图片信息
     * @param  position 点击的图片索引*/
    public static ViewPagerFragment getInstance(ArrayList<String> urls,ArrayList<ImageInfo> infos,ImageInfo imageInfo,int position) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        Bundle imgs=new Bundle();
        imgs.putStringArrayList(CommonTag.KEY_IMAGE_LIST,urls);
        imgs.putParcelableArrayList(CommonTag.KEY_ALL_IMAGE_INFO, infos);
        imgs.putParcelable(CommonTag.KEY_IMAGE_INFO,imageInfo);
        imgs.putInt(CommonTag.KEY_CLICK_POSITION, position);
        fragment.setArguments(imgs);
        return fragment;
    }
    /**@param url 图片地址
     * @param imageInfo 图片信息*/
    public static ViewPagerFragment getInstance(String url,ImageInfo imageInfo) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        Bundle imgs=new Bundle();
        ArrayList<String> paths=new ArrayList<>();
        paths.add(url);
        imgs.putStringArrayList(CommonTag.KEY_IMAGE_LIST,paths);
        imgs.putParcelable(CommonTag.KEY_IMAGE_INFO,imageInfo);
        fragment.setArguments(imgs);
        return fragment;
    }
    /**@param url 图片地址
     * @param imageInfo 图片信息*/
    public static void simpleShowBig(FragmentManager manager,String url,ImageInfo imageInfo){
        manager.beginTransaction().replace(Window.ID_ANDROID_CONTENT, ViewPagerFragment.getInstance(url,imageInfo),
                "ViewPagerFragment")
                .addToBackStack(null).commit();
    }
    public static void simpleShowBig(FragmentManager manager,ArrayList<String> urls,ArrayList<ImageInfo> infos,ImageInfo imageInfo,int position){
        manager.beginTransaction().replace(Window.ID_ANDROID_CONTENT, ViewPagerFragment.getInstance(urls,infos,imageInfo,position),
                "ViewPagerFragment")
                .addToBackStack(null).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_viewpager, null);
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!ImageLoader.getInstance().isInited()){
            CommonUtils.initImageloader(getActivity());
        }
        viewPager = (ReboundViewPager) view.findViewById(R.id.viewpager);
        tips = (TextView) view.findViewById(R.id.text);
        mask = view.findViewById(R.id.mask);
        Bundle bundle = getArguments();

        runEnterAnimation();

        imgs = bundle.getStringArrayList(CommonTag.KEY_IMAGE_LIST);
        imageInfo = bundle.getParcelable(CommonTag.KEY_IMAGE_INFO);
        if(imgs.size()==1){//for just one pic
            imageInfos=new ArrayList<>();
            imageInfos.add(imageInfo);
            position=0;
            tips.setVisibility(View.GONE);
        }else{
            imageInfos = bundle.getParcelableArrayList(CommonTag.KEY_ALL_IMAGE_INFO);
            position = bundle.getInt(CommonTag.KEY_CLICK_POSITION, 0);
        }

        tips.setText((position + 1) + "/" + imgs.size());

        viewPager.getOverscrollView().setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imgs.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int pos) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_view_detail, null, false);
                final PhotoView photoView = (PhotoView) view.findViewById(R.id.image_detail);
                final MaterialProgressBar progressBar = (MaterialProgressBar) view.findViewById(R.id.progress);
                if (first&&position == pos && ImageLoader.getInstance().getDiskCache().get(imgs.get(pos)) != null) {//only animate when position equals u click in pre layout
                    photoView.animateFrom(imageInfo);
                    first=false;
                }
                //load pic from remote
                String path=imgs.get(pos);
                if(!TextUtils.isEmpty(path)){
                    if(path.startsWith("http://")||path.startsWith("file://")||path.startsWith("assets://")
                            ||path.startsWith("drawable://")||path.startsWith("content://")){

                    }else{
                        path="file://"+path;
                    }
                }
                ImageLoader.getInstance().displayImage(path, photoView,
                        new DisplayImageOptions.Builder()
                                .cacheInMemory(true).cacheOnDisk(true).build(), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                progressBar.setVisibility(View.GONE);
                            }

                        });

                //force to get focal point,to listen key listener
                photoView.setFocusableInTouchMode(true);
                photoView.requestFocus();
                photoView.setOnKeyListener(pressKeyListener);//add key listener to listen back press
                photoView.setOnClickListener(onClickListener);
                final String finalPath = path;
                photoView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        FragmentDiaChoose.create(0,getResources().getStringArray(R.array.lib_pic_save_cancel))
                                .setmChooseListener(new FragmentDiaChoose.ChooseClickListener() {
                                    @Override
                                    public void click(int index, int tag) {
                                        if(index==0){
                                        saveToSDcard(finalPath,pos);
                                        }
                                    }
                                })
                                .show(getActivity().getSupportFragmentManager(),"save");
                        return true;
                    }
                });
                photoView.setTag(pos);
                photoView.touchEnable(true);
                container.addView(view);
                return view;
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
                tips.setText((position + 1) + "/" + imgs.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //set current position
        viewPager.getOverscrollView().setCurrentItem(position);
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            exitFragment(v);
        }
    };

    private void exitFragment(View v) {

        //exit click position
        int position = (int) v.getTag();

        if(((FrameLayout)v.getParent()).getChildAt(1).getVisibility() == View.VISIBLE){
            popFragment();
        }else {
            runExitAnimation(v);
            ((PhotoView) v).animateTo(imageInfos.get(position), new Runnable() {
                @Override
                public void run() {
                    popFragment();
                }
            });
        }
    }

    private void popFragment() {
        if (!ViewPagerFragment.this.isResumed()) {
            return;
        }
        final FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.popBackStack();
        }
    }


    private View.OnKeyListener pressKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (event.getAction() != KeyEvent.ACTION_UP) {
                    return true;
                }
                exitFragment(v);
                return true;
            }
            return false;
        }
    };


    private void runEnterAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(300);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());
        mask.startAnimation(alphaAnimation);
        tips.startAnimation(alphaAnimation);
    }

    public void runExitAnimation(final View view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(300);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mask.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tips.startAnimation(alphaAnimation);
        mask.startAnimation(alphaAnimation);
    }



    public void saveToSDcard(String imageUrl,int position){
        ImageLoader.getInstance().loadImage(imageUrl, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                String fileName = new HashCodeFileNameGenerator().generate(imageUri) + ".jpeg";
                String path=new File(Environment.getExternalStorageDirectory(),CommonTag.TEMP_SAVE_PIC).getAbsolutePath();
                File saveImageFile = saveFile(loadedImage, fileName,path);
                if (saveImageFile!=null){
                    Toast.makeText(getActivity(),String.format("%s%s/%s", "图片保存到", path, fileName),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(saveImageFile);
                    intent.setData(uri);
                    getActivity().sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
                }
                else Toast.makeText(getActivity(),"图片保存失败",Toast.LENGTH_SHORT).show();

                //对bitmap进行垃圾回收
                loadedImage.recycle();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    private File saveFile(Bitmap bitmap, String fileName,String path){

        try {
            File file=new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            File save=new File(file,fileName);
            boolean result=bitmap.compress(Bitmap.CompressFormat.PNG,100,new FileOutputStream(save));
            if(result){
                return save;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
