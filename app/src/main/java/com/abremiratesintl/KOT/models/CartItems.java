package com.abremiratesintl.KOT.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CartItems implements Parcelable {
    List<Items> mCartItems;
    int mCount;

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public float getTotalPrice() {
        return mTotalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        mTotalPrice = totalPrice;
    }

    float mTotalPrice;

    public List<Items> getCartItems() {
        return mCartItems;
    }

    public CartItems() {
    }

    public void setCartItems(List<Items> cartItems) {
        mCartItems = cartItems;
    }

    public CartItems(Parcel in) {
        if (in.readByte() == 0x01) {
            mCartItems = new ArrayList<Items>();
            in.readList(mCartItems, Items.class.getClassLoader());
        } else {
            mCartItems = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mCartItems == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mCartItems);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CartItems> CREATOR = new Parcelable.Creator<CartItems>() {
        @Override
        public CartItems createFromParcel(Parcel in) {
            return new CartItems(in);
        }

        @Override
        public CartItems[] newArray(int size) {
            return new CartItems[size];
        }
    };
}