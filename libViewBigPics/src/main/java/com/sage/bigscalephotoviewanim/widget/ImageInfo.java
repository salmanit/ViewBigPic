package com.sage.bigscalephotoviewanim.widget;

import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

/**
 * Created by liuheng on 2015/8/19.
 */
public class ImageInfo implements Parcelable{

    RectF mRect = new RectF();

    RectF mImgRect = new RectF();

    RectF mWidgetRect = new RectF();

    RectF mBaseRect = new RectF();

    PointF mScreenCenter = new PointF();

    float mScale;

    float mDegrees;

    ImageView.ScaleType mScaleType= ImageView.ScaleType.FIT_CENTER;

    public ImageInfo() {
    }

    public ImageInfo(RectF rect, RectF img, RectF widget, RectF base, PointF screenCenter, float scale, float degrees, ImageView.ScaleType scaleType) {
        mRect.set(rect);
        mImgRect.set(img);
        mWidgetRect.set(widget);
        mScale = scale;
        mScaleType = scaleType;
        mDegrees = degrees;
        mBaseRect.set(base);
        mScreenCenter.set(screenCenter);
    }

    protected ImageInfo(Parcel in) {
        mRect = in.readParcelable(RectF.class.getClassLoader());
        mImgRect = in.readParcelable(RectF.class.getClassLoader());
        mWidgetRect = in.readParcelable(RectF.class.getClassLoader());
        mBaseRect = in.readParcelable(RectF.class.getClassLoader());
        mScreenCenter = in.readParcelable(PointF.class.getClassLoader());
        mScale = in.readFloat();
        mDegrees = in.readFloat();
        mScaleType =ImageView.ScaleType.values()[in.readInt()];
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel in) {
            return new ImageInfo(in);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mRect, flags);
        dest.writeParcelable(mImgRect, flags);
        dest.writeParcelable(mWidgetRect, flags);
        dest.writeParcelable(mBaseRect, flags);
        dest.writeParcelable(mScreenCenter, flags);
        dest.writeFloat(mScale);
        dest.writeFloat(mDegrees);
        dest.writeInt(mScaleType.ordinal());
    }
}
