package com.portum.android.sdk.internal.network;

import com.portum.android.sdk.internal.model.AdRequest;
import com.portum.android.sdk.internal.model.AdResponse;

import java.util.List;

/**
 * Abstract
 */
public interface AdNetworkServiceProvider {
    AdResponse requestAd(AdRequest adRequest);
    void processImpression(List<String> imps);
}
