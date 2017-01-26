package com.portum.android.sdk.internal.network;

import com.portum.android.sdk.BuildConfig;
import com.portum.android.sdk.internal.Logger;
import com.portum.android.sdk.internal.adapter.AdFormatJsonAdapter;
import com.portum.android.sdk.internal.adapter.AdResponseStatusJsonAdapter;
import com.portum.android.sdk.internal.model.AdRequest;
import com.portum.android.sdk.internal.model.AdResponse;
import com.portum.android.sdk.internal.model.AdResponseStatus;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Portum Ad Server client
 */
public final class PortumAdServerAPI20 implements AdNetworkServiceProvider {
    private static final Moshi MOSHI = new Moshi.Builder()
            .add(new AdFormatJsonAdapter())
            .add(new AdResponseStatusJsonAdapter())
            .build();

    private static final JsonAdapter<AdResponse> AD_RESPONSE_JSON_ADAPTER
            = MOSHI.adapter(AdResponse.class);

    private OkHttpClient mHttpClient;

    public PortumAdServerAPI20() {
        mHttpClient = new OkHttpClient();
    }

    @Override
    public AdResponse requestAd(AdRequest adRequest) {
        try {
            HttpUrl.Builder builder = new HttpUrl.Builder()
                    .scheme(BuildConfig.AD_SERVER_SCHEME)
                    .host(BuildConfig.AD_SERVER_HOST)
                    .addPathSegment("adreq")
                    .addQueryParameter("p", adRequest.getAdUnitId())
                    .addQueryParameter("api_ver", adRequest.getApiVersion())
                    .addQueryParameter("ad_format", adRequest.getFormat().value())
                    .addQueryParameter("w", String.valueOf(adRequest.getScreenWidth()))
                    .addQueryParameter("h", String.valueOf(adRequest.getScreenHeight()))
                    .addQueryParameter("android_ifa", adRequest.getGoogleAdId())
                    .addQueryParameter("dnt", "0")
                    .addQueryParameter("android_id", adRequest.getPlatformId())
                    .addQueryParameter("dev_imei", adRequest.getmHardwareId())
                    .addQueryParameter("dev_mac", adRequest.getMAC())
                    .addQueryParameter("coppa", adRequest.isCoppa() ? "1" : "0")
                    .addQueryParameter("bundle", adRequest.getAppId())
                    .addQueryParameter("os", adRequest.getDeviceOS())
                    .addQueryParameter("lng", adRequest.getLanguage())
                    .addQueryParameter("mnc", String.valueOf(adRequest.getMNC()))
                    .addQueryParameter("mcc", String.valueOf(adRequest.getMCC()))
                    .addQueryParameter("ct", adRequest.getConnectionType().value())
                    .addQueryParameter("dt", adRequest.getScreenInch() > 7.0f ? "tablet" : "phone")
                    .addQueryParameter("dm", adRequest.getDeviceModel())
                    .addQueryParameter("lat", adRequest.getLatitude())
                    .addQueryParameter("lon", adRequest.getLongitude())
                    .addQueryParameter("age", String.valueOf(adRequest.getUserAge()))
                    .addQueryParameter("gender", adRequest.getUserGender())
                    .addQueryParameter("response_type", "json");

            HttpUrl url = builder.build();

            // Create request for remote resource.
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            // Execute the request and retrieve the response.
            Response response = mHttpClient.newCall(request).execute();

            // Deserialize HTTP response to concrete type.
            ResponseBody body = response.body();
            AdResponse adResponse = AD_RESPONSE_JSON_ADAPTER.fromJson(body.source());
            body.close();

            return adResponse;
        } catch (Exception e) {
            Logger.w("cannot get response from server", e);

            return new AdResponse(AdResponseStatus.BAD_REQUEST);
        }
    }

    @Override
    public void processImpression(List<String> imps) {
        for (String url : imps) {
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                mHttpClient.newCall(request).execute();

            } catch (Exception e) {
                Logger.w("cannot get response from server during impression", e);
            }
        }
    }
}
