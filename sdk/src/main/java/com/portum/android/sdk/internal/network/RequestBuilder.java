package com.portum.android.sdk.internal.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.portum.android.sdk.internal.Logger;
import com.portum.android.sdk.internal.model.AdFormat;
import com.portum.android.sdk.internal.model.AdRequest;
import com.portum.android.sdk.internal.model.ConnectionType;
import com.portum.android.sdk.internal.model.Gender;
import com.portum.android.sdk.internal.model.UserInfo;

/**
 * Ad Server request builder
 */
public final class RequestBuilder {

    private static AdRequest sCache = null;

    /**
     * Build Ad Server request model
     *
     * @param context android context
     * @param adUnitId ad unit id in portum system
     * @param userInfo user information
     * @param adFormat see {@link AdFormat}
     *
     * @return request model
     */
    public AdRequest buildAdRequest(Context context, String adUnitId,
                                    UserInfo userInfo, AdFormat adFormat) {
        synchronized (this) {
            if (sCache == null) {
                sCache = new AdRequest(context);
            }
        }

        AdRequest result = new AdRequest(sCache);

        result.setAdUnitId(adUnitId);
        result.setFormat(adFormat);
        result.setConnectionType(getNetworkClass(context));

        if (userInfo != null) {
            if (userInfo.getGender() != Gender.UNKNOWN) {
                result.setUserGender(userInfo.getGender() == Gender.FEMALE ? "f" : "m");
            }

            result.setUserAge(userInfo.getAge());
        }

        return result;
    }

    /**
     * http://stackoverflow.com/a/25912464/902217
     *
     * @param context android context
     *
     * @return network connection type {@link ConnectionType}
     */
    private ConnectionType getNetworkClass(Context context) {
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
}
