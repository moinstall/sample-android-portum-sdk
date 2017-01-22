package com.portum.android.sdk.internal.model;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.portum.android.sdk.internal.Logger;
import com.portum.android.sdk.internal.helper.AdvertisingIdClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Class which contains all fields for build HTTP request
 */
public class AdRequest {

    private String mAdUnitId;
    private String mApiVersion;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDpi;
    private String mGoogleAdId;
    private AdFormat mFormat;
    private String mDeviceModel;
    private String mDeviceOS;

    /**
     * The mLanguage (2-character ISO_639-1 codes) The Device Language
     */
    private String mLanguage;

    /**
     * Current carrier MCC.
     */
    private int mMCC;

    /**
     * MNC code of the current carrier
     */
    private int mMNC;

    /**
     * Users params (optional)
     */
    private String mUserGender;
    private int mUserAge;

    private ConnectionType mConnectionType;
    private String mPlatformId;
    private String mHardwareId;
    private String mMAC;
    private boolean mCoppa;
    private String mAppId;
    private float screenInch;
    private String latitude;
    private String longitude;

    public AdRequest(Context context) {
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Point size = getDisplaySize(wm.getDefaultDisplay());
            mScreenWidth = size.x;
            mScreenHeight = size.y;

            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            mScreenDpi = dm.densityDpi;
        } catch (Exception e) {
            Logger.w("Cannot retrieve screen size info");
            throw new IllegalStateException("Cannot retrieve screen size info", e);
        }
//
//        try {
//            Map<Integer, Integer> ads = new HashMap<>();
//
//            // list of supported resolutions
//            if (getScreenWidth() < getScreenHeight()) { // portrait
//                ads.put(320, 480);
//                ads.put(640, 960);
//                ads.put(768, 1024);
//            } else {                                                    // landscape
//                ads.put(480, 320);
//                ads.put(960, 640);
//                ads.put(1024, 768);
//            }
//
//            Integer closestWidth = closest(getScreenWidth(), ads.keySet());
//            Integer closestHeight = ads.get(closestWidth);
//
//            setAdWidth(closestWidth);
//            setAdHeight(closestHeight);
//        } catch (Exception e) {
//            Logger.w("Cannot calculate closest ads size");
//
//            setAdWidth(320);
//            setAdHeight(480);
//        }

        try {
            mGoogleAdId = AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
        } catch (Exception e) {
            Logger.w("Cannot retrieve google advertising info");
        }

        mApiVersion = "20";

        // REVIEW http://stackoverflow.com/a/27836910/902217
        mDeviceModel = android.os.Build.MODEL;
        mDeviceOS = android.os.Build.VERSION.RELEASE;
        mLanguage = Locale.getDefault().getLanguage();

        try {
            TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String networkOperator = tel.getNetworkOperator();

            if (!TextUtils.isEmpty(networkOperator)) {
                int mcc = Integer.parseInt(networkOperator.substring(0, 3));
                int mnc = Integer.parseInt(networkOperator.substring(3));

                mMCC = mcc;
                mMNC = mnc;
            }
        } catch (Exception e) {
            Logger.w("Cannot retrieve carrier info");
        }

        mAppId = context.getPackageName();

        mPlatformId = Settings.Secure.ANDROID_ID;
    }

    public void setAdUnitId(String adUnitId) {
        mAdUnitId = adUnitId;
    }

    public String getAdUnitId() {
        return mAdUnitId;
    }

    public String getApiVersion() {
        return mApiVersion;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public int getScreenDpi() {
        return mScreenDpi;
    }

    public String getGoogleAdId() {
        return mGoogleAdId;
    }

    public AdFormat getFormat() {
        return mFormat;
    }

    public void setFormat(AdFormat format) {
        mFormat = format;
    }

    public String getDeviceModel() {
        return mDeviceModel;
    }

    public String getDeviceOS() {
        return mDeviceOS;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public int getMNC() {
        return mMNC;
    }

    public int getMCC() {
        return mMCC;
    }

    public String getUserGender() {
        return mUserGender;
    }

    public int getUserAge() {
        return mUserAge;
    }

    public void setUserGender(String gender) {
        mUserGender = gender;
    }

    public void setUserAge(int age) {
        mUserAge = age;
    }

    public ConnectionType getConnectionType() {
        return mConnectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        mConnectionType = connectionType;
    }

    public String getPlatformId() {
        return mPlatformId;
    }

    /**
     * http://stackoverflow.com/a/13515612/902217
     * @param display
     * @return
     */
    private static Point getDisplaySize(final Display display) {
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {   //API LEVEL 13
            display.getSize(point);
        } else {
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        return point;
    }

    /**
     * closest lowest )
     */
    private static Integer closest(Integer value, Collection<Integer> values) {
        Integer dist = Integer.MAX_VALUE;
        Integer closest = 0;

        List<Integer> list = new ArrayList<>(values);
        Collections.sort(list, Collections.reverseOrder());

        for (Integer v : list) {
            int newDist = Math.abs(v - value);

            if (newDist < dist) {
                closest = v;
                dist = newDist;
            }
        }

        return closest;
    }


    public String getmHardwareId() {
        return mHardwareId;
    }

    public String getMAC() {
        return mMAC;
    }

    public boolean isCoppa() {
        return mCoppa;
    }

    public String getAppId() {
        return mAppId;
    }

    public float getScreenInch() {
        return screenInch;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
