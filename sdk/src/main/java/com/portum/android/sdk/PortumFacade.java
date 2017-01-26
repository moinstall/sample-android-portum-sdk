package com.portum.android.sdk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.portum.android.sdk.internal.listener.DefaultListener;
import com.portum.android.sdk.internal.listener.UIThreadListenerWrapper;
import com.portum.android.sdk.internal.model.AdFormat;
import com.portum.android.sdk.internal.model.AdResponseStatus;
import com.portum.android.sdk.internal.model.UserInfo;
import com.portum.android.sdk.internal.network.AdNetworkServiceProvider;
import com.portum.android.sdk.internal.Logger;
import com.portum.android.sdk.internal.model.AdResponse;
import com.portum.android.sdk.internal.model.Gender;
import com.portum.android.sdk.internal.network.PortumAdServerAPI20;
import com.portum.android.sdk.internal.network.PrivateImageLoader;
import com.portum.android.sdk.internal.network.RequestBuilder;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Facade for Portum ad platform.
 * Single point which provide control on ads
 */
public final class PortumFacade {

    /**
     * Internal state
     */
    private enum State {
        NotReady,
        Initializing,
        Ready,
        NetworkIssues,
        Error,
    }

    /**
     * Any context
     */
    private static Context mContext;

    /**
     * Handler for UI tasks
     */
    private static Handler mHandler;

    /**
     * AdResponse info form server side
     */
    private static AdResponse mAdResponse;

    /**
     * Server bridge, single point for communication with backend server
     */
    private static AdNetworkServiceProvider mServer = null;

    /**
     * Request to server builder
     */
    private static RequestBuilder mRequestBuilder = new RequestBuilder();

    /**
     * PortumListener for different events that may happens during using
     */
    private static PortumListener mListener = new UIThreadListenerWrapper(new DefaultListener());

    /**
     * Last setted adUnitId
     */
    private static String madUnitId;

    /**
     * Last setted Ad Format
     */
    private static AdFormat mAdFormat;

    /**
     * User info, need for more accurate ads
     */
    private static UserInfo mUserInfo;

    /**
     * Internal state of SDK
     */
    private static State mState = State.NotReady;

    /**
     * Executor service for network tasks
     */
    private static ExecutorService mExecutorService;

    /**
     * SDK version
     * @return return string which represent SDK version
     */
    public static String version() {
        return BuildConfig.VERSION_NAME + (BuildConfig.DEBUG ? " - Debug" : " - Release");
    }

    /**
     * Thread-safe Entry point this method should be called at first to initialize internals of PortumFacade SDK
     *
     * @param context - android context
     */
    public static synchronized void prepare(final Context context) {
        prepare(context, null);
    }

    /**
     * The same as previous just for convenience you can setup placement Id once and then only call
     * show method without params
     *
     * @param context android context
     * @param adUnitId ad unit id
     */
    public static synchronized void prepare(final Context context, final String adUnitId) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }

        mState = State.Initializing;
        mContext = context;
        mState = State.Ready;
        mAdFormat = AdFormat.ANY;
        madUnitId = adUnitId;
        mHandler = new Handler(Looper.getMainLooper());
        mServer = new PortumAdServerAPI20();
        mExecutorService = Executors.newFixedThreadPool(5);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
        PrivateImageLoader.getInstance().init(config);

        if (mState == State.Ready) {
            Logger.d("PortumFacade ready to use");
        } else {
            Logger.d("PortumFacade reinitialized");
        }

        Logger.i("PortumFacade [" + version() + "] prepared successfully and ready to use");
    }

    /**
     * Set listener to monitor different SDK events
     * @param listener see {@link PortumListener}
     */
    public static void setListener(PortumListener listener) {
        if (mListener != listener && listener != null) {
            mListener = new UIThreadListenerWrapper(listener);
        }
    }
    /**
     * For internal usage only
     * @return listener {@link PortumListener}
     */
    static PortumListener listener() {
        return mListener;
    }

    /**
     * Optional step, if SDK user can provde more detailed info about user, this will help
     * to show more relevant ads, this info has ben taken from social networks as example
     *
     * @param userInfo basic User info
     */
    public static void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    /**
     * Thread-safe way to show ad. This method query service about latest creatives.
     * Also it carrying about current device orientation and work asynchronously.
     *
     * @param adUnitId ad unit id in portum system
     * @param format {@link AdFormat}
     */
    public static void showAd(String adUnitId, AdFormat format) {
        madUnitId = adUnitId;
        mAdFormat = format;
        show();
    }

    /**
     * Thread-safe way to show ad. This method query service about latest creatives.
     * Also it carrying about current device orientation and work asynchronously.
     */
    public static void show() {
        if (mContext == null) {
            throw new IllegalStateException("context cannot be null, please call 'prepare' before");
        }

        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    cacheAd();

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mState == State.Ready) {
                                if (mAdResponse != null && mAdResponse.getStatus() == AdResponseStatus.SUCCESS) {
                                    String bannerUrl = getAdUrl();
                                    String videoUrl = getAdVideoUrl();

                                    Intent intent = new Intent(mContext, PortumAdActivity.class);
                                    intent.putExtra(PortumAdActivity.BANNER_URL, bannerUrl);
                                    intent.putExtra(PortumAdActivity.VIDEO_URL, videoUrl);
                                    intent.putExtra(PortumAdActivity.CLICK_URL, mAdResponse.getClickUrl());
                                    intent.putStringArrayListExtra(PortumAdActivity.IMPRESSION_URLS,
                                            new ArrayList<>(mAdResponse.getImpressionUrls()));
                                    mContext.startActivity(intent);
                                } else {
                                    Logger.w("No ads to show");

                                    if (mAdResponse != null) {
                                        String msg = mAdResponse.getReason() + ": " + mAdResponse.getMessage();
                                        mListener.onError(new Exception(msg));
                                    } else {
                                        mListener.onError(new Exception("No ads to show, look at previous error in log"));
                                    }
                                }
                            } else {
                                Logger.w("PortumFacade still not ready to use state=" + mState);

                                mListener.onError(new Exception("PortumFacade status is "
                                        + mState + "but it should be " + State.Ready));
                            }
                        }
                    });

                } catch (Exception e) {
                    Logger.e(e.getMessage());
                    mListener.onError(e);
                }
            }
        });
    }

    static void processImpressions(final List<String> imps) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                mServer.processImpression(imps);
            }
        });
    }

    /**
     * Retrieve ad assets info and cache them
     */
    private static void cacheAd() {
        mListener.onAdDownloading();

        mAdResponse = mServer.requestAd(
                mRequestBuilder.buildAdRequest(
                        mContext,
                        madUnitId,
                        getUserInfo(),
                        mAdFormat));

        String bannerUrl = getAdUrl();

        PrivateImageLoader.getInstance().loadImage(bannerUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mListener.onAdDownloaded();
            }
        });
    }

    /**
     * This implementation as example, providing user specific info up to end sdk users (developers)
     * @return user info
     */
    private static UserInfo getUserInfo() {
        if (mUserInfo == null) {
            Gender gender = (Build.MANUFACTURER.equalsIgnoreCase("samsung")) ? Gender.FEMALE : Gender.MALE;

            mUserInfo = new UserInfo(gender, 25);
        }

        return mUserInfo;
    }

    /**
     * For now we have single ad
     * maybe in future this method will have more complicated logic
     *
     * @return image URL to show
     */
    private static String getAdUrl() {
    	if (mAdResponse == null || TextUtils.isEmpty(mAdResponse.getBannerUrl())) {
    		return null;
    	} else {
	        return mAdResponse.getBannerUrl();
    	}
    }
    private static String getAdVideoUrl() {
        if (mAdResponse == null || TextUtils.isEmpty(mAdResponse.getVideoUrl())) {
            return null;
        } else {
            return mAdResponse.getVideoUrl();
        }
    }
}
