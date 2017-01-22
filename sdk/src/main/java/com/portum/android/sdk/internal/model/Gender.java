package com.portum.android.sdk.internal.model;

public enum Gender {
	MALE("male"),
	FEMALE("female"),
	UNKNOWN("unknown");
	
	private String mValue;

	Gender(String value) {
        mValue = value;
    }

    public String value() {
        return mValue;
    }
}
