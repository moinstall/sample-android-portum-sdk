package com.portum.android.sdk.internal.model;

/**
 * Created by camobap on 1/22/17.
 */
public enum AdResponseStatus {
    SUCCESS("ok"),
    NOAD("noad"),
    BAD_REQUEST("bad_request"),
    UNKNOWN("unknown");

    private String mValue;

    AdResponseStatus(String value) {
        mValue = value;
    }

    public String value() {
        return mValue;
    }
}
