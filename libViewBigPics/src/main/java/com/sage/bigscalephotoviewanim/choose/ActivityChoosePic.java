package com.sage.bigscalephotoviewanim.choose;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sage.bigscalephotoviewanim.R;
import com.sage.bigscalephotoviewanim.common.CommonUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;


/**
 * Created by Sage on 2015/11/13.
 */
public class ActivityChoosePic extends AppCompatActivity implements View.OnClickListener{

    public static Intent createIntent(Context context,ArrayList<String> mSelectedImage){
        Intent intent=new Intent(context,ActivityChoosePic.class);
        intent.putExtra("data",mSelectedImage);
        return intent;
    }
    public static Intent createIntent(Context context,ArrayList<String> mSelectedImage,int maxChoose){
        Intent intent=new Intent(context,ActivityChoosePic.class);
        intent.putExtra("data",mSelectedImage);
        if(maxChoose>0)
            intent.putExtra("max",maxChoose);
        return intent;
    }

    TextView includeChoose;
    GridView idGridView;
    TextView tvChooseDir;
    TextView tvTotalCount;
    RelativeLayout layoutBottomLy;
    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    public static ArrayList<String> mSelectedImage = new ArrayList<>();
    private int mScreenHeight;
    private ProgressDialog mProgressDialog;

    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    /**
     * 所有的图片
     */
    private List<String> mImgs;

    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<>();

    /**
     * 扫描拿到所有的图片文件夹
     */
    public List<ImageFolder> mImageFloders = new ArrayList<>();
    private int totalCount;
    private int maxChoose=9;
    private int currentIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStateBar();
        setContentView(R.layout.activity_choose_pic);
        init();
    }
    public View statusBarView;
    private void initStateBar(){
        final View decorView = getWindow().getDecorView();
         statusBarView = new View(this);
        final int statusBarHeight = getStatusBarHeight();
        statusBarView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
        statusBarView.setBackgroundResource(R.color.bg_title);
        ((ViewGroup)decorView).addView(statusBarView);
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public void change(){
        if(mSelectedImage.size()==0){
            includeChoose.setEnabled(false);
            tvTotalCount.setEnabled(false);
            includeChoose.setText("选择");
            tvTotalCount.setText("预览");
        }else{
            includeChoose.setEnabled(true);
            tvTotalCount.setEnabled(true);
            includeChoose.setText("选择"+mSelectedImage.size()+"/"+maxChoose);
            tvTotalCount.setText("预览("+mSelectedImage.size()+")");
        }
    }

    public boolean canSelect(){
        if(mSelectedImage.size()>=maxChoose){
            showToast("最多只能选择"+maxChoose+"张图");
            return false;
        }
        return true;
    }
    private void init(){
        includeChoose= (TextView) findViewById(R.id.include_choose);
         idGridView= (GridView) findViewById(R.id.id_gridView);
        tvChooseDir= (TextView) findViewById(R.id.id_choose_dir);
        tvTotalCount= (TextView) findViewById(R.id.id_total_count);
        findViewById(R.id.include_back).setOnClickListener(this);
        tvTotalCount.setOnClickListener(this);
        tvChooseDir.setOnClickListener(this);
        includeChoose.setOnClickListener(this);
        layoutBottomLy= (RelativeLayout) findViewById(R.id.id_bottom_ly);
        if(!ImageLoader.getInstance().isInited()){
            CommonUtils.initImageloader(this);
        }
        if(getIntent().hasExtra("max")){
            maxChoose=getIntent().getIntExtra("max",9);
        }
        mSelectedImage= (ArrayList<String>) getIntent().getSerializableExtra("data");
        if(mSelectedImage==null){
            mSelectedImage=new ArrayList<>();
        }
        change();
        mScreenHeight=getResources().getDisplayMetrics().heightPixels;
        getImages();
    }


    private PopupWindow pw;
    private ListView lv;
    private void showPw(){
        if(pw==null){
            View view= LayoutInflater.from(this).inflate(R.layout.pw_choose_pic_dir,null);
            lv= (ListView) view.findViewById(R.id.lv);
            lv.setAdapter(new AdapterChooseDir(mImageFloders));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pw.dismiss();
                    if (position == currentIndex) {
                        return;
                    }
                    currentIndex = position;
                    mImgDir = new File(mImageFloders.get(position).getDir());
                    updateGridView();
                }
            });
            pw=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, (int) (mScreenHeight*0.6),true);
            pw.setBackgroundDrawable(new ColorDrawable());
            pw.setOutsideTouchable(true);
            pw.setTouchable(true);
            pw.setAnimationStyle(R.style.PopupAnimation);
            pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    // 设置背景颜色恢复正常
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    getWindow().setAttributes(lp);
                }
            });
        }
        lv.setItemChecked(currentIndex, true);
        pw.showAsDropDown(layoutBottomLy,0,0);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = .3f;
        getWindow().setAttributes(lp);
    }
    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages()
    {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {
            showToast("暂无外部存储");
            return;
        }
        // 显示进度条
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                String firstImage = null;

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                // 只查询jpeg和png的图片
                Cursor mCursor = getContentResolver().query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED+" desc ");

                while (mCursor.moveToNext())
                {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    if(parentFile.getName().equals(ImageChooseUtils.temp_file_choose)){
                        continue;
                    }
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFolder imageFloder;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath))
                    {
                        continue;
                    } else
                    {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFolder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }

                    int picSize = 0;
                    try {
                        picSize = parentFile.list(filter).length;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    totalCount += picSize;

                    imageFloder.setCount(picSize);
                    mImageFloders.add(imageFloder);

                    if (picSize > mPicsSize)
                    {
                        mPicsSize = picSize;
                        mImgDir = parentFile;
                        currentIndex=mImageFloders.size()-1;
                    }
                }
                mCursor.close();

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;
                if (mImgDir == null)
                {
                    mHandler.sendEmptyMessage(0x120);
                    return;
                }
                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);

            }
        }).start();

    }

    private FilenameFilter filter=new FilenameFilter()
    {
        @Override
        public boolean accept(File dir, String filename)
        {
            if ((filename.endsWith(".jpg")
                    || filename.endsWith(".png")
                    || filename.endsWith(".jpeg"))&&new File(dir,filename).length()>0)
                return true;
            return false;
        }
    };
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            mProgressDialog.dismiss();
            switch (msg.what){
                case 0x110:
                    tvChooseDir.setEnabled(true);
                    // 为View绑定数据
                    updateGridView();
                    break;
                case 0x120:
                    showToast("相册是空的额~~");
                    break;
            }

        }
    };

    private AdapterChoosePic adapter;
    private void updateGridView(){
        List<File> files=Arrays.asList(mImgDir.listFiles(filter));
        Collections.sort(files,new ModifiedTimeSort());
        adapter=new AdapterChoosePic(files,this);
        idGridView.setAdapter(adapter);
    }

    class ModifiedTimeSort implements Comparator<File> {


        @Override
        public int compare(File lhs, File rhs) {
            if(lhs.lastModified()>rhs.lastModified()){
                return -1;
            }else if(lhs.lastModified()<rhs.lastModified()){
                return 1;
            }
            return 0;
        }

        @Override
        public boolean equals(Object object) {
            return false;
        }
    }
    private Toast toast;
    public void showToast(String msg){
        if(toast!=null){
            toast.cancel();
            toast=null;
        }
        toast=Toast.makeText(this,msg+"",Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.include_back){
            finish();
        }else if(view.getId()==R.id.include_choose){
            setResult(RESULT_OK,new Intent().putStringArrayListExtra("data",mSelectedImage));
            finish();
        }else if(view.getId()==R.id.id_choose_dir){
            showPw();
        }else if(view.getId()==R.id.id_total_count){
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_root,
                    FragmentPreviewBigPic.getInstance(mSelectedImage))
                    .addToBackStack("preview").commit();
        }
    }
}
