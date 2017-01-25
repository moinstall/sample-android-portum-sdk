package com.portum.android.sdk.internal.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Ad response model
 */
public final class AdResponse {

    public static class Creative {
        @Json(name = "url") String mUrl;

        public String getUrl() {
            return mUrl;
        }
    }

    public AdResponse(AdResponseStatus status) {
        mStatus = status;
    }

    public AdResponseStatus getStatus() {
        return mStatus;
    }

    public String getBannerUrl() {
        return mBannerCreative != null ? mBannerCreative.getUrl() : null;
    }

    public String getVideoUrl() {
        return mVideoCreative != null ? mVideoCreative.getUrl() : null;
    }

    public String getClickUrl() {
        return mClickUrl;
    }

    public AdFormat getAdFormat() { return mAdFormat; }

    public List<String> getImpressionUrls() {
        return mImpressionUrls;
    }

    @Json(name = "status") AdResponseStatus mStatus;
    @Json(name = "ad_type") AdFormat mAdFormat;
    @Json(name = "video") Creative mVideoCreative;
    @Json(name = "banner") Creative mBannerCreative;
    @Json(name = "click_url") String mClickUrl;
    @Json(name = "impressions") List<String> mImpressionUrls;
}
