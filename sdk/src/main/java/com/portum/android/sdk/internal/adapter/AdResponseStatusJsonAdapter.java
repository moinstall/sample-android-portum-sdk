package com.portum.android.sdk.internal.adapter;

import com.portum.android.sdk.internal.model.AdResponseStatus;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

public final class AdResponseStatusJsonAdapter {

    @ToJson
    String toJson(AdResponseStatus card) {
        return card.value();
    }

    @FromJson
    AdResponseStatus fromJson(String status) {
        if (AdResponseStatus.SUCCESS.value().equals(status)) {
            return AdResponseStatus.SUCCESS;
        } else if (AdResponseStatus.NOAD.value().equals(status)) {
            return AdResponseStatus.NOAD;
        } else if (AdResponseStatus.BAD_REQUEST.value().equals(status)){
            return AdResponseStatus.BAD_REQUEST;
        } else {
            return AdResponseStatus.UNKNOWN;
        }
    }
}
