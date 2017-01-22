package com.portum.android.sdk.internal.model;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.Json;
import com.squareup.moshi.ToJson;

public enum AdFormat {
    ANY("any"),
    IMAGE("image"),
    VIDEO("video");

    private String mValue;

    AdFormat(String value) {
        mValue = value;
    }

    public String value() {
        return mValue;
    }
}
