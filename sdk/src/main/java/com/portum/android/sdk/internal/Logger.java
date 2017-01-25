package com.portum.android.sdk.internal;

import com.portum.android.sdk.BuildConfig;

import android.util.Log;


/**
 * Single logger for all library
 */
public final class Logger {
    private static final String TAG = "PortumFacade";

    /**
     * http://stackoverflow.com/questions/20176284/buildconfig-debug-always-false-when-building-library-projects-with-gradle
     * @param message is log entry
     */
    public static void d(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void w(String message) {
        Log.w(TAG, message);
    }

    public static void w(String message, Throwable e) {
        Log.w(TAG, message, e);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }

    public static void e(String message, Throwable e) {
        Log.e(TAG, message, e);
    }

    public static void bug(String message) {
        Log.w(TAG, "Unreachable state please report this bug to developer '" + message + "'");
    }
}
