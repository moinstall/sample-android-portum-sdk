package com.portum.android.sdk.sample;

import com.portum.android.sdk.PortumListener;

import android.content.Context;
import android.widget.Toast;

/**
 * Sample of listener implementation
 */
public final class TestListener implements PortumListener {

    private Context mContext;

    public TestListener(Context context) {
        mContext = context;
    }

    @Override
    public void onAdDownloading() {
        Toast.makeText(mContext, "Ad downloading...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdDownloaded() {
        Toast.makeText(mContext, "Ad downloaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdShow() {
        Toast.makeText(mContext, "Ad show", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdClick() {
        Toast.makeText(mContext, "Ad click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdClose() {
        Toast.makeText(mContext, "Ad close", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Exception reason) {
        Toast.makeText(mContext, "Error: " + reason.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
