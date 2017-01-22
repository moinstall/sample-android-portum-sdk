package com.portum.android.sdk.internal.model;

public final class UserInfo {
    private Gender gender;
    private int age;

    public UserInfo(Gender gender, int age) {
        this.gender = gender;
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }
}
