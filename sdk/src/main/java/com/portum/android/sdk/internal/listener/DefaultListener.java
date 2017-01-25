package com.portum.android.sdk.internal.listener;

import com.portum.android.sdk.PortumListener;
import com.portum.android.sdk.internal.Logger;

/**
 * Default listener implementation to avoid NPE
 */
public class DefaultListener implements PortumListener {
    private static final String TAG = "DefaultListener";

    @Override
    public void onAdDownloading() {
        Logger.d("onAdDownloading");
    }

    @Override
    public void onAdDownloaded() {
        Logger.d("onAdDownloaded");
    }

    @Override
    public void onAdShow() {
        Logger.d("onAdShow");
    }

    @Override
    public void onAdClick() {
        Logger.d("onAdClick");
    }

    @Override
    public void onAdClose() {
        Logger.d("onAdClose");
    }

    @Override
    public void onError(Exception reason) {
        Logger.d("onError " + reason.getLocalizedMessage());
    }
}