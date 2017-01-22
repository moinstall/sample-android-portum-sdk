package com.portum.android.sdk.internal.adapter;

import com.portum.android.sdk.internal.model.AdFormat;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

public class AdFormatJsonAdapter {

    @ToJson
    String toJson(AdFormat card) {
        return card.value();
    }

    @FromJson
    AdFormat fromJson(String card) {
        if (AdFormat.IMAGE.value().equals(card)) {
            return AdFormat.IMAGE;
        } else if (AdFormat.VIDEO.value().equals(card)) {
            return AdFormat.VIDEO;
        } else {
            return AdFormat.ANY;
        }
    }
}
