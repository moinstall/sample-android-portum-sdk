package com.portum.android.sdk.internal.model;

public enum ConnectionType {
    WIFI("wifi"),
    TWO_G("2g"),
    THREE_G("3g"),
    FOUR_G("4g"),
    UNKNOWN("unknown");

    private String mValue;

    ConnectionType(String value) {
        mValue = value;
    }

    public String value() {
        return mValue;
    }
}
