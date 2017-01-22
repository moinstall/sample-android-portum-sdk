package com.portum.android.sdk.internal.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import com.portum.android.sdk.internal.model.AdResponse;

/**
 * Created by camobap on 1/22/17.
 */

public class AdResponseParcelableCreator implements Parcelable.Creator<AdResponse> {
    @Override
    public AdResponse createFromParcel(Parcel source) {
        return null;
    }

    @Override
    public AdResponse[] newArray(int size) {
        return new AdResponse[size];
    }
}
