package com.portum.android.sdk.internal.network;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.portum.android.sdk.internal.Logger;
import com.portum.android.sdk.internal.helper.AdvertisingIdClient;
import com.portum.android.sdk.internal.model.AdFormat;
import com.portum.android.sdk.internal.model.AdRequest;
import com.portum.android.sdk.internal.model.ConnectionType;
import com.portum.android.sdk.internal.model.Gender;
import com.portum.android.sdk.internal.model.UserInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by camobap on 8/17/15.
 */
public final class RequestBuilder {

    private static AdRequest sCache = null;

    public AdRequest buildAdRequest(Context context, String adUnitId,
                                    UserInfo userInfo, AdFormat adFormat) {

        if (sCache == null) {
            sCache = new AdRequest(context);
        }

        sCache.setAdUnitId(adUnitId);
        sCache.setFormat(adFormat);
        sCache.setConnectionType(getNetworkClass(context));

        if (userInfo != null) {
            if (userInfo.getGender() != Gender.UNKNOWN) {
                sCache.setUserGender(userInfo.getGender() == Gender.FEMALE ? "f" : "m");
            }

            sCache.setUserAge(userInfo.getAge());
        }

        return sCache;
    }

    /**
     * http://stackoverflow.com/a/25912464/902217
     */
    public static ConnectionType getNetworkClass(Context context) {
        ConnectionType type = ConnectionType.UNKNOWN;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();

            if (info != null && info.isConnected()) {

                switch (info.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        type = ConnectionType.WIFI;
                        break;

                    case ConnectivityManager.TYPE_MOBILE:
                        switch (info.getSubtype()) {
                            case TelephonyManager.NETWORK_TYPE_GPRS:
                            case TelephonyManager.NETWORK_TYPE_EDGE:
                            case TelephonyManager.NETWORK_TYPE_CDMA:
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                type = ConnectionType.TWO_G;
                                break;
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            case TelephonyManager.NETWORK_TYPE_EHRPD:
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                type = ConnectionType.THREE_G;
                                break;
                            case TelephonyManager.NETWORK_TYPE_LTE:
                                type = ConnectionType.FOUR_G;
                                break;
                        }
                        break;
                }
            }
        } catch (Exception e) {
            Logger.e("Cannot detect connection type");
        }

        return type;
    }

    /**
     * http://stackoverflow.com/a/63274/902217
     * @param list
     * @param delim
     * @return
     */
    private static String join(List<String> list, String delim) {
        if (list == null || list.size() == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        String loopDelim = "";

        for(String s : list) {

            sb.append(loopDelim);
            sb.append(s);

            loopDelim = delim;
        }

        return sb.toString();
    }
}
