# ViewBigPics
点击小图看大图，并可以放大缩小＜/br＞

原地址是这里的https://github.com/brucetoo/ActivityAnimation ＜/br＞ 
因为里边的代码有些地方不符合我的需求，所以就修改了点东西。简单的说就是 ＜/br＞ 
原来的，如果图片很小比如100*100的，我们点击后看大图他也显示的100*100，＜/br＞
而我们需求看大图的时候要铺满屏幕，就是以屏幕宽为比列缩放。＜/br＞

这个库里默认使用了compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.4'＜/br＞
所以如果你工程也用到的话可以不用再引用这个库拉＜/br＞
＜/br＞
使用 compile 'com.sage.bigscalephotoviewanim:libViewBigPics:1.0.7'＜/br＞
＜/br＞

最新版本1.0.4增加了一个库，库的地址我找不到了，网上的，就是选择图片的。image-chooser库，github上的地址找不到拉。＜/br＞
还有用洪洋写的一个仿微信多图选择的，也整合到里边了。＜/br＞
反正是和图片有关的，我打算都整合到一起，方便调用，免得添加一堆库。＜/br＞
使用看demo就行，再不行看源码。＜/br＞
＜/br＞
默认viewpager的背景颜色是黑色的，索引文字是白色的，如要修改，可以重写以下2个字段＜/br＞
<color name="library_big_scale_bg_color">@android:color/black</color>＜/br＞
    <color name="library_big_scale_text_color">@android:color/white</color>＜/br＞
＜/br＞＜/br＞＜/br＞

 点击事件,如果只有一张图，可以只传2个参数， KEY_IMAGE_LIST和KEY_IMAGE_INFO＜/br＞
 @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(view.isEnabled()) {
            try {  Bundle bundle = new Bundle();
            bundle.putStringArrayList(CommonTag.KEY_IMAGE_LIST, imgList);//all PhotoView url(remote)
            bundle.putParcelable(CommonTag.KEY_IMAGE_INFO, ((PhotoView) view.findViewById(R.id.pv)).getInfo());//click PhotoView ImageInfo
            bundle.putInt(CommonTag.KEY_CLICK_POSITION, position);//click position
            imgImageInfos.clear();
            //NOTE:if imgList.size >= the visible count in single screen,i will cause NullPointException
            //because item out of screen have been replaced/reused
            //中文说明下：我们利用的是getchildat（position）获取到listview或者gridview里的item。。
            //如果第一个可见的不是0，后边获取会出错，所以得减掉不可见的
            for (int i = 0; i < imgList.size(); i++) {
                if(i<parent.getFirstVisiblePosition()||i>parent.getLastVisiblePosition()){
                    imgImageInfos.add(new ImageInfo());
                }else {
                    //imgImageInfos.add(((PhotoView) parent.getChildAt(i-parent.getFirstVisiblePosition())).getInfo());//remember all PhotoView ImageInfo
                    imgImageInfos.add(((PhotoView) (parent.getChildAt(i-parent.getFirstVisiblePosition()).findViewById(R.id.pv))).getInfo());//remember all PhotoView ImageInfo

                }

            }
            bundle.putParcelableArrayList(CommonTag.KEY_ALL_IMAGE_INFO, imgImageInfos);
            //attach fragment to Window

                getActivity().getSupportFragmentManager().beginTransaction().replace(Window.ID_ANDROID_CONTENT,
                        ViewPagerFragment.getInstance(bundle), "ViewPagerFragment")
                        .addToBackStack(null).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getActivity(),"not enable",Toast.LENGTH_SHORT).show();
        }
    }

简单的adapter，其中注释掉的为item就是张图，后边那个为item控件比较多的使用方法＜/br＞
    public class AAadapter extends BaseAdapter{

        @Override
        public int getCount() {
            return imgList.size();
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
//            PhotoView p = new PhotoView(getActivity());
//            p.setLayoutParams(new AbsListView.LayoutParams((int) (getResources().getDisplayMetrics().density * 100),
//                  (int) (getResources().getDisplayMetrics().density * 100)));
//            p.setScaleType(ImageView.ScaleType.CENTER_CROP);
           // p.setEnabled(false);//u can't click view until image load completed
           //load image and put it into PhotoView

            if(convertView==null){
                convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pic_text,parent,false);
            }
            PhotoView p= (PhotoView) convertView.findViewById(R.id.pv);
            p.setLayoutParams(new LinearLayout.LayoutParams((int) (getResources().getDisplayMetrics().density * 100),
                    (int) (getResources().getDisplayMetrics().density * 100)));
            TextView tv= (TextView) convertView.findViewById(R.id.tv_position);
            tv.setText("position~"+position);
            ImageLoader.getInstance().displayImage(imgList.get(position),p);
            p.touchEnable(false);
            return convertView;
        }
    }





    上边是Fragment的使用。下边是 activity的使用，简单的一张图＜/br＞
    需要说明的是activity里的控件长宽比列要和点击的原始控件长宽比例一样，否则，两者图片拉伸不一样，看起来就奇怪了，不推荐使用这个玩意＜/br＞
＜/br＞
＜/br＞
    new  ActivityTransitionEnterHelper(MainActivity.this).fromView(view).
                          imageUrl(imgList.get(position)).start(PictureDetailsActivity.class);
＜/br＞
＜/br＞＜/br＞

    对应的activity的使用＜/br＞
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.picture_info);
            mImageView = (ImageView) findViewById(R.id.imageView);
            mBackgroudnView = (RelativeLayout) findViewById(R.id.topLevelLayout);

            transitionExitHelper = ActivityTransitionExitHelper.with(getIntent())
                    .toView(mImageView).background(mBackgroudnView)
                    .setAnimaEnd(new ActivityTransitionExitHelper.AnimationEnd() {
                        @Override
                        public void end() {
                            findViewById(R.id.tv).setVisibility(View.VISIBLE);
                        }
                    })
                    .start(savedInstanceState);

        }


        @Override
        public void onBackPressed() {
            findViewById(R.id.tv).setVisibility(View.GONE);
            transitionExitHelper.runExitAnimation(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }

        @Override
        public void finish() {
            super.finish();
            // override transitions to skip the standard window animations
            overridePendingTransition(0, 0);
        }
