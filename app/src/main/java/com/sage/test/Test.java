package com.sage.test;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

/**
 * Created by Sage on 2017/8/25.
 * Description:
 */

public class Test implements Parcelable{

    ImageView.ScaleType mScaleType;
    private int number;

    protected Test(Parcel in) {
        number = in.readInt();
    }

    public static final Creator<Test> CREATOR = new Creator<Test>() {
        @Override
        public Test createFromParcel(Parcel in) {
            return new Test(in);
        }

        @Override
        public Test[] newArray(int size) {
            return new Test[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
    }
}
