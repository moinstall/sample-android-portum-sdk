package com.portum.android.sdk.internal.listener;

import android.os.Handler;
import android.os.Looper;

import com.portum.android.sdk.PortumListener;

public final class UIThreadListenerWrapper implements PortumListener {
    private static final String TAG = "DefaultListener";

    private final PortumListener mListener;
    private final Handler mHandler;

    public UIThreadListenerWrapper(PortumListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("wrapped listener cannot be null");
        }

        mListener = listener;
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onAdDownloading() {
        if (isUIThread()) {
            mListener.onAdDownloading();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onAdDownloading();
                }
            });
        }
    }

    @Override
    public void onAdDownloaded() {
        if (isUIThread()) {
            mListener.onAdDownloaded();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onAdDownloaded();
                }
            });
        }
    }

    @Override
    public void onAdShow() {
        if (isUIThread()) {
            mListener.onAdShow();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onAdShow();
                }
            });
        }
    }

    @Override
    public void onAdClick() {
        if (isUIThread()) {
            mListener.onAdClick();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onAdClick();
                }
            });
        }
    }

    @Override
    public void onAdClose() {
        if (isUIThread()) {
            mListener.onAdClose();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onAdClose();
                }
            });
        }
    }

    @Override
    public void onError(final Exception reason) {
        if (isUIThread()) {
            mListener.onError(reason);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onError(reason);
                }
            });
        }
    }

    // http://stackoverflow.com/a/11411087/902217
    private boolean isUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
