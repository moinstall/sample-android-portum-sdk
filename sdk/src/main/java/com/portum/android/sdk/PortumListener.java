package com.portum.android.sdk;

/**
 * If you wanna listen different event during ad processing implement this
 * listener and setup it in {@link PortumFacade}
 *
 * NOTE: all events will be delivered on UI thread
 */
public interface PortumListener {
    void onAdDownloading();
    void onAdDownloaded();
    void onAdShow();
    void onAdClick();
    void onAdClose();
    void onError(Exception reason);
}
