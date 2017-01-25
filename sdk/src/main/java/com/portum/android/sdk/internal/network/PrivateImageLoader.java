package com.portum.android.sdk.internal.network;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * In case if end user will ue AUIL library also private loader to avoid missconfiguration
 * @see <a href="https://github.com/nostra13/Android-Universal-Image-Loader/issues/92">https://github.com/nostra13/Android-Universal-Image-Loader/issues/92</a>
 */
public final class PrivateImageLoader extends ImageLoader {
    private volatile static PrivateImageLoader instance;

    // https://en.wikipedia.org/wiki/Singleton_pattern#Initialization-on-demand_holder_idiom
    private static class Holder {
        private static final PrivateImageLoader INSTANCE = new PrivateImageLoader();
    }

    /**
     * @return singleton class instance
     */
    public static PrivateImageLoader getInstance() {
        return Holder.INSTANCE;
    }
}
