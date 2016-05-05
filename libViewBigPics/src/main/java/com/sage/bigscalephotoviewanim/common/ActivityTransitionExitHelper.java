package com.sage.bigscalephotoviewanim.common;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bruce Too
 * On 9/26/15.
 * At 15:13
 * ActivityTransitionExit
 * Don`t forget add transparent theme in target activity
 */
public class ActivityTransitionExitHelper {

    private static final TimeInterpolator decelerator = new DecelerateInterpolator();
    private static final TimeInterpolator accelerator = new AccelerateInterpolator();
    private static final int ANIM_DURATION = 5000;
    private Intent fromIntent;//intent from pre activity
    private View toView;//target view show in this activity
    private View background; //root view of this activity
    private ColorDrawable bgDrawable; //background color
    private int leftDelta;
    private int topDelta;
    private float widthDelta;
    private float heightDelta;

    public ActivityTransitionExitHelper(Intent fromIntent) {
        this.fromIntent = fromIntent;
    }

    public static ActivityTransitionExitHelper with(Intent intent) {
        return new ActivityTransitionExitHelper(intent);
    }

    /**
     * add target view
     * @param toView to which view
     * @return this
     */
    public ActivityTransitionExitHelper toView(View toView) {
        this.toView = toView;
        return this;
    }

    /**
     * add root view of this layout
     * @param background the background view
     * @return this
     */
    public ActivityTransitionExitHelper background(View background) {
        this.background = background;
        return this;
    }

    /**
     * @param savedInstanceState if savedInstanceState != null
     *                           we don`t have to perform the transition animation
     */
    public ActivityTransitionExitHelper start(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            final int thumbnailTop = fromIntent.getIntExtra(ActivityTransitionEnterHelper.PRE_NAME + ".top", 0);
            final int thumbnailLeft = fromIntent.getIntExtra(ActivityTransitionEnterHelper.PRE_NAME + ".left", 0);
            final int thumbnailWidth = fromIntent.getIntExtra(ActivityTransitionEnterHelper.PRE_NAME + ".width", 0);
            final int thumbnailHeight = fromIntent.getIntExtra(ActivityTransitionEnterHelper.PRE_NAME + ".height", 0);
            String imgUrl = fromIntent.getStringExtra(ActivityTransitionEnterHelper.PRE_NAME + ".imageUrl");
            ImageLoader.getInstance().displayImage(imgUrl, (ImageView) toView, new DisplayImageOptions.Builder()
//                    .showImageOnLoading(R.drawable.ic_launcher)
//                    .showImageForEmptyUri(R.drawable.ic_launcher)
//                    .showImageOnFail(R.drawable.ic_launcher)
                    .cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(DefaultConfigurationFactory.createBitmapDisplayer())
                    .build());

                bgDrawable = new ColorDrawable(Color.BLACK);
                background.setBackgroundDrawable(bgDrawable);


            toView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    //remove default
                    toView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int viewLocation[] = new int[2];
                    toView.getLocationOnScreen(viewLocation);
                    leftDelta = thumbnailLeft - viewLocation[0];
                    topDelta = thumbnailTop - viewLocation[1];
                    //Note: widthDelta must be float
                    widthDelta =(float) thumbnailWidth / toView.getWidth();
                    heightDelta =(float)  thumbnailHeight / toView.getHeight();

                    runEnterAnimation();
                    return true;
                }
            });
        }
        return this;
    }

    private void runEnterAnimation() {
        //resize/relocation toView
        toView.setPivotX(0);
        toView.setPivotY(0); //axis
        toView.setScaleX(widthDelta);
        toView.setScaleY(heightDelta);
        toView.setTranslationX(leftDelta);
        toView.setTranslationY(topDelta);

        toView.animate().translationX(0).translationY(0)
                .scaleX(1).scaleY(1).setDuration(ANIM_DURATION)
                .setInterpolator(accelerator)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animationEnd != null) {
                            animationEnd.end();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })

                .start();

        ObjectAnimator bgAnim = ObjectAnimator.ofInt(bgDrawable, "alpha",0,255);
        //ObjectAnimator bgAnim = ObjectAnimator.ofFloat(bgDrawable, "alpha",0,1);
        bgAnim.setInterpolator(accelerator);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();

    }

    public void runExitAnimation(final Runnable exit){

        //targetApi 16
        toView.animate().translationX(leftDelta).translationY(topDelta)
                .scaleX(widthDelta).scaleY(heightDelta)
                .setInterpolator(decelerator)
                .setDuration(ANIM_DURATION)
//                .withEndAction(new Runnable() {//api16
//                    @Override
//                    public void run() {
//                        background.setVisibility(View.GONE);
//                        toView.setVisibility(View.GONE); //let background and target view invisible
//                        exit.run();
//                    }
//                }) //endAction Callback
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        background.setVisibility(View.GONE);
                        toView.setVisibility(View.GONE); //let background and target view invisible
                        exit.run();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();

        //animate color drawable of background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(bgDrawable, "alpha",0);
        bgAnim.setInterpolator(decelerator);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();
    }

    public interface  AnimationEnd{
         void end();
    }
    public AnimationEnd animationEnd;

    public ActivityTransitionExitHelper setAnimaEnd(AnimationEnd animationEnd) {
        this.animationEnd = animationEnd;
        return this;
    }
}
