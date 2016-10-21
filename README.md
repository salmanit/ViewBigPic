# ViewBigPics<br />
点击小图看大图，并可以放大缩小<br />
<br />
<br />
原地址是这里的https://github.com/brucetoo/ActivityAnimation&nbsp;<br />
因为里边的代码有些地方不符合我的需求，所以就修改了点东西。简单的说就是&nbsp;<br />
原来的，如果图片很小比如100*100的，我们点击后看大图他也显示的100*100，<br />
而我们需求看大图的时候要铺满屏幕，就是以屏幕宽为比列缩放。<br />
<br />
<br />
这个库里默认使用了compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.4'<br />
所以如果你工程也用到的话可以不用再引用这个库拉<br />
<br />
使用 compile 'com.sage.bigscalephotoviewanim:libViewBigPics:1.0.7'<br />
<br />
<br />
<br />
最新版本1.0.4增加了一个库，库的地址我找不到了，网上的，就是选择图片的。image-chooser库，github上的地址找不到拉。<br />
还有用洪洋写的一个仿微信多图选择的，也整合到里边了。<br />
反正是和图片有关的，我打算都整合到一起，方便调用，免得添加一堆库。<br />
使用看demo就行，再不行看源码。<br />
<br />
默认viewpager的背景颜色是黑色的，索引文字是白色的，如要修改，可以重写以下2个字段<br />
&lt;color name=&quot;library_big_scale_bg_color&quot;&gt;@android:color/black&lt;/color&gt;<br />
&nbsp; &nbsp; &lt;color name=&quot;library_big_scale_text_color&quot;&gt;@android:color/white&lt;/color&gt;<br />
<br />
<br />
<br />
&nbsp;点击事件,如果只有一张图，可以只传2个参数， KEY_IMAGE_LIST和KEY_IMAGE_INFO<br />
&nbsp;@Override<br />
&nbsp; &nbsp; public void onItemClick(AdapterView&lt;?&gt; parent, View view, int position, long id) {<br />
&nbsp; &nbsp; &nbsp; &nbsp; if(view.isEnabled()) {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; try { &nbsp;Bundle bundle = new Bundle();<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; bundle.putStringArrayList(CommonTag.KEY_IMAGE_LIST, imgList);//all PhotoView url(remote)<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; bundle.putParcelable(CommonTag.KEY_IMAGE_INFO, ((PhotoView) view.findViewById(R.id.pv)).getInfo());//click PhotoView ImageInfo<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; bundle.putInt(CommonTag.KEY_CLICK_POSITION, position);//click position<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; imgImageInfos.clear();<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; //NOTE:if imgList.size &gt;= the visible count in single screen,i will cause NullPointException<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; //because item out of screen have been replaced/reused<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; //中文说明下：我们利用的是getchildat（position）获取到listview或者gridview里的item。。<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; //如果第一个可见的不是0，后边获取会出错，所以得减掉不可见的<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; for (int i = 0; i &lt; imgList.size(); i++) {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; if(i&lt;parent.getFirstVisiblePosition()||i&gt;parent.getLastVisiblePosition()){<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; imgImageInfos.add(new ImageInfo());<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; }else {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; //imgImageInfos.add(((PhotoView) parent.getChildAt(i-parent.getFirstVisiblePosition())).getInfo());//remember all PhotoView ImageInfo<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; imgImageInfos.add(((PhotoView) (parent.getChildAt(i-parent.getFirstVisiblePosition()).findViewById(R.id.pv))).getInfo());//remember all PhotoView ImageInfo<br />
<br />
<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; }<br />
<br />
<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; }<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; bundle.putParcelableArrayList(CommonTag.KEY_ALL_IMAGE_INFO, imgImageInfos);<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; //attach fragment to Window<br />
<br />
<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; getActivity().getSupportFragmentManager().beginTransaction().replace(Window.ID_ANDROID_CONTENT,<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ViewPagerFragment.getInstance(bundle), &quot;ViewPagerFragment&quot;)<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; .addToBackStack(null).commit();<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; } catch (Exception e) {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; e.printStackTrace();<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; }<br />
&nbsp; &nbsp; &nbsp; &nbsp; }else{<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Toast.makeText(getActivity(),&quot;not enable&quot;,Toast.LENGTH_SHORT).show();<br />
&nbsp; &nbsp; &nbsp; &nbsp; }<br />
&nbsp; &nbsp; }<br />
<br />
<br />

<p>
	简单的adapter，其中注释掉的为item就是张图，后边那个为item控件比较多的使用方法
</p>
<p>
	<br />
	
</p>
&nbsp; &nbsp; public class AAadapter extends BaseAdapter{<br />
<br />
<br />
&nbsp; &nbsp; &nbsp; &nbsp; @Override<br />
&nbsp; &nbsp; &nbsp; &nbsp; public int getCount() {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; return imgList.size();<br />
&nbsp; &nbsp; &nbsp; &nbsp; }<br />
<br />
<br />
&nbsp; &nbsp; &nbsp; &nbsp; @Override<br />
&nbsp; &nbsp; &nbsp; &nbsp; public Object getItem(int position) {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; return null;<br />
&nbsp; &nbsp; &nbsp; &nbsp; }<br />
<br />
<br />
&nbsp; &nbsp; &nbsp; &nbsp; @Override<br />
&nbsp; &nbsp; &nbsp; &nbsp; public long getItemId(int position) {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; return 0;<br />
&nbsp; &nbsp; &nbsp; &nbsp; }<br />
<br />
<br />
&nbsp; &nbsp; &nbsp; &nbsp; @Override<br />
&nbsp; &nbsp; &nbsp; &nbsp; public View getView(int position, View convertView, ViewGroup parent) {<br />
// &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;PhotoView p = new PhotoView(getActivity());<br />
// &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;p.setLayoutParams(new AbsListView.LayoutParams((int) (getResources().getDisplayMetrics().density * 100),<br />
// &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;(int) (getResources().getDisplayMetrics().density * 100)));<br />
// &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;p.setScaleType(ImageView.ScaleType.CENTER_CROP);<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;// p.setEnabled(false);//u can't click view until image load completed<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;//load image and put it into PhotoView<br />
<br />
<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; if(convertView==null){<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pic_text,parent,false);<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; }<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; PhotoView p= (PhotoView) convertView.findViewById(R.id.pv);<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; p.setLayoutParams(new LinearLayout.LayoutParams((int) (getResources().getDisplayMetrics().density * 100),<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; (int) (getResources().getDisplayMetrics().density * 100)));<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; TextView tv= (TextView) convertView.findViewById(R.id.tv_position);<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; tv.setText(&quot;position~&quot;+position);<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ImageLoader.getInstance().displayImage(imgList.get(position),p);<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; p.touchEnable(false);<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; return convertView;<br />
&nbsp; &nbsp; &nbsp; &nbsp; }<br />
&nbsp; &nbsp; }<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
<br />
&nbsp; &nbsp; 上边是Fragment的使用。下边是 activity的使用，简单的一张图<br />
&nbsp; &nbsp; 需要说明的是activity里的控件长宽比列要和点击的原始控件长宽比例一样，否则，两者图片拉伸不一样，看起来就奇怪了，不推荐使用这个玩意<br />
<br />
&nbsp; &nbsp; new &nbsp;ActivityTransitionEnterHelper(MainActivity.this).fromView(view).<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; imageUrl(imgList.get(position)).start(PictureDetailsActivity.class);<br />
<br />
<br />
&nbsp; &nbsp; 对应的activity的使用<br />
&nbsp; &nbsp; &nbsp; &nbsp; @Override<br />
&nbsp; &nbsp; &nbsp; &nbsp; public void onCreate(Bundle savedInstanceState) {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; super.onCreate(savedInstanceState);<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; setContentView(R.layout.picture_info);<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; mImageView = (ImageView) findViewById(R.id.imageView);<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; mBackgroudnView = (RelativeLayout) findViewById(R.id.topLevelLayout);<br />
<br />
<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; transitionExitHelper = ActivityTransitionExitHelper.with(getIntent())<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; .toView(mImageView).background(mBackgroudnView)<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; .setAnimaEnd(new ActivityTransitionExitHelper.AnimationEnd() {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; @Override<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; public void end() {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; findViewById(R.id.tv).setVisibility(View.VISIBLE);<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; }<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; })<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; .start(savedInstanceState);<br />
<br />
<br />
&nbsp; &nbsp; &nbsp; &nbsp; }<br />
<br />
<br />
<br />
<br />
&nbsp; &nbsp; &nbsp; &nbsp; @Override<br />
&nbsp; &nbsp; &nbsp; &nbsp; public void onBackPressed() {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; findViewById(R.id.tv).setVisibility(View.GONE);<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; transitionExitHelper.runExitAnimation(new Runnable() {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; @Override<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; public void run() {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; finish();<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; }<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; });<br />
&nbsp; &nbsp; &nbsp; &nbsp; }<br />
<br />
<br />
&nbsp; &nbsp; &nbsp; &nbsp; @Override<br />
&nbsp; &nbsp; &nbsp; &nbsp; public void finish() {<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; super.finish();<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; // override transitions to skip the standard window animations<br />
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; overridePendingTransition(0, 0);<br />
&nbsp; &nbsp; &nbsp; &nbsp; }
