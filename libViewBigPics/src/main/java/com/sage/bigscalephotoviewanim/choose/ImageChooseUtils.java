package com.sage.bigscalephotoviewanim.choose;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Sage on 2015/11/15.
 */
public class ImageChooseUtils {

    public static final String temp_file_choose="temp_small_pic";//大图生成小图的保存文件夹,过滤文件夹的名字，不扫描
    public static void saveSmallPic(final String picPath){
        File small=getSaveName(picPath);
        if(small.exists()){
            return;
        }
        HandlerThread thread=new HandlerThread("save");
        thread.start();
        Handler handler=new Handler(thread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    compressAndSaveImage(picPath,1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public static File getSaveName(String picPath){
        File original = new File(picPath);
        try {
            File dir=new File(Environment.getExternalStorageDirectory(), temp_file_choose);
            if(!dir.exists()){
                dir.mkdirs();
            }
            return  new File(dir,  original.getName().replace(".", "_size_" + original.length()/1024+"."));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return original;
    }

    /**传递图片路径过来，判断下我们的小图文件夹下是否有，有的话返回小图路径，没有的话返回原图路径*/
    public static String getResultPath(String picPath){
        File file=getSaveName(picPath);
        if(file.exists()){
            return file.getAbsolutePath();
        }else{
            return picPath;
        }
    }

    public static  String compressAndSaveImage(String fileImage, int scale) throws Exception {

        FileOutputStream stream = null;
        Bitmap bitmap;
        try {
            BitmapFactory.Options optionsForGettingDimensions = new BitmapFactory.Options();
            optionsForGettingDimensions.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(fileImage, optionsForGettingDimensions);
            if (bitmap != null) {
                bitmap.recycle();
            }
            int w, l;
            w = optionsForGettingDimensions.outWidth;
            l = optionsForGettingDimensions.outHeight;

            ExifInterface exif = new ExifInterface(fileImage);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int rotate = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = -90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            int what = w > l ? w : l;

            BitmapFactory.Options options = new BitmapFactory.Options();
            if (what > 3000) {
                options.inSampleSize = scale * 6;
            } else if (what > 2000 && what <= 3000) {
                options.inSampleSize = scale * 5;
            } else if (what > 1500 && what <= 2000) {
                options.inSampleSize = scale * 4;
            } else if (what > 1000 && what <= 1500) {
                options.inSampleSize = scale * 3;
            } else if (what > 400 && what <= 1000) {
                options.inSampleSize = scale * 2;
            } else {
                options.inSampleSize = scale;
            }

            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(fileImage, options);
            File file=getSaveName(fileImage);
            stream = new FileOutputStream(file);
            if (rotate != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, false);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            return file.getAbsolutePath();

        } catch (IOException e) {
            return fileImage;
        } catch (Exception e) {
            return fileImage;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {

                }
            }
        }
    }


    public static void deleteSmallPic(){
        File dir=new File(Environment.getExternalStorageDirectory(), temp_file_choose);
        if(dir.exists()&&dir.isDirectory()&&dir.listFiles()!=null){
            for(File file2:dir.listFiles()){
                file2.delete();
            }
        }
    }
}
