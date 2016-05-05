package com.sage.imagechooser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sage.imagechooser.api.BChooserPreferences;
import com.sage.imagechooser.api.ChooserType;
import com.sage.imagechooser.api.ChosenImage;
import com.sage.imagechooser.api.ChosenVideo;
import com.sage.imagechooser.api.ImageChooserListener;
import com.sage.imagechooser.api.ImageChooserManager;
import com.sage.imagechooser.api.VideoChooserListener;
import com.sage.imagechooser.api.VideoChooserManager;

import java.io.File;


public class BaseChoosePicVideoActivity extends AppCompatActivity implements ImageChooserListener,VideoChooserListener {

	
	private int chooserType;
	private ImageChooserManager imageChooserManager;
	private String filePath;
	private VideoChooserManager videoChooserManager;

    public Bundle getVideo_bundle() {
        return null;
    }

    public void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.clearOldFiles();
        try {
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void captureVideo() {
        chooserType = ChooserType.REQUEST_CAPTURE_VIDEO;
        videoChooserManager = new VideoChooserManager(this,
                ChooserType.REQUEST_CAPTURE_VIDEO);
        if(getVideo_bundle()!=null){
            videoChooserManager.setExtras(getVideo_bundle());
        }
        videoChooserManager.setVideoChooserListener(this);
        try {
            filePath = videoChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pickVideo() {
        chooserType = ChooserType.REQUEST_PICK_VIDEO;
        videoChooserManager = new VideoChooserManager(this,
                ChooserType.REQUEST_PICK_VIDEO);
        videoChooserManager.setVideoChooserListener(this);
        try {
            videoChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       Log.i("base choose activity", "OnActivityResult" + " Chooser Type: " + chooserType + "  File Path : " + filePath);
        if (resultCode == RESULT_OK) {
        	if(requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE){
        		 if (imageChooserManager == null) {
                     reinitializeImageChooser();
                 }
                 imageChooserManager.submit(requestCode, data);
        	}else if(requestCode == ChooserType.REQUEST_CAPTURE_VIDEO || requestCode == ChooserType.REQUEST_PICK_VIDEO){
        		if (videoChooserManager == null) {
                    reinitializeVideoChooser();
                }
                videoChooserManager.submit(requestCode, data);
        	}
        } 
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType, true);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(filePath);
    }
    
    // Should be called if for some reason the VideoChooserManager is null (Due
    // to destroying of activity for low memory situations)
    private void reinitializeVideoChooser() {
        videoChooserManager = new VideoChooserManager(this, chooserType, true);
        videoChooserManager.setVideoChooserListener(this);
        videoChooserManager.reinitialize(filePath);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        System.out.println("Saving Stuff" + " Chooser Type: " + chooserType + "  File Path: " + filePath);
        outState.putInt("chooser_type", chooserType);
        outState.putString("media_path", filePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState( Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("chooser_type")) {
                chooserType = savedInstanceState.getInt("chooser_type");
            }
            if (savedInstanceState.containsKey("media_path")) {
                filePath = savedInstanceState.getString("media_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

	@Override
	public void onImageChosen(ChosenImage image) {
		
	}



	@Override
	public void onError(String reason) {
		
	}



	@Override
	public void onVideoChosen(ChosenVideo video) {
		
	}


    public void clearCache(){
        File dir=new File(Environment.getExternalStorageDirectory(),new BChooserPreferences(this).getFolderName());
        if(dir.exists()&&dir.isDirectory()){
            File[] files=dir.listFiles();
            if(files!=null){
                for(File file:files){
                    file.delete();
                }
            }
        }
    }
}
