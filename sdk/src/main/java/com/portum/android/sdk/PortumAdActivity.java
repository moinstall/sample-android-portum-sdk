package com.portum.android.sdk;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.portum.android.sdk.internal.Logger;
import com.portum.android.sdk.internal.network.PrivateImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Interstial activity which can play audio or video
 */
public final class PortumAdActivity extends AppCompatActivity {

    public static final String BANNER_URL = "bannerUrl";
    public static final String VIDEO_URL = "videoUrl";
    public static final String IMPRESSION_URLS = "impUrls";
    public static final String CLICK_URL = "clickUrl";

    private static int SHOW_IMMEDIATELY = 0;

    private String mBannerUrl;
    private String mVideoUrl;
    private String mClickUrl;
    private List<String> mImpressionUrls;

    private ImageView mCloseButton;
    private VideoView mVideoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mBannerUrl = intent.getStringExtra(BANNER_URL);
        mVideoUrl = intent.getStringExtra(VIDEO_URL);
        mImpressionUrls = intent.getStringArrayListExtra(IMPRESSION_URLS);
        mClickUrl = intent.getStringExtra(CLICK_URL);

        setContentView(R.layout.portum_ad_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        final PortumListener listener = PortumFacade.listener();

        mCloseButton = (ImageView) findViewById(R.id.interstitial_close);
        mCloseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.onAdClose();
                finish();
            }
        });
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int closeSize = (size.y < size.x ? size.y : size.x) / 20;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                closeSize,
                closeSize
        );
        params.setMargins(closeSize / 2, closeSize / 2, 0, 0);
        mCloseButton.setLayoutParams(params);
        mCloseButton.requestLayout();

        final ImageView adImage = (ImageView) findViewById(R.id.interstitial_banner);
        PrivateImageLoader.getInstance().displayImage(mBannerUrl, adImage, new SimpleImageLoadingListener() {

            private boolean failed = false;

            @Override
            public void onLoadingComplete(String paramString, View paramView,
                                          Bitmap paramBitmap) {

                if (failed || paramBitmap == null) {
                    Logger.w("Popup will not be showed because previous error");
                } else {
                    listener.onAdShow();
                    showCloseWithDelay(SHOW_IMMEDIATELY);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                failed = true;
                listener.onError(new Exception("banner loading error", failReason.getCause()));
            }
        });

        adImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickAd();
            }
        });

        if (!TextUtils.isEmpty(mVideoUrl)) {
            adImage.setAlpha(0.99f); // hack for some samsung device
            mVideoView = (VideoView) findViewById(R.id.video_view);

            // http://stackoverflow.com/a/16899473/902217
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(mVideoView);

            mVideoView.requestFocus();
            mVideoView.setMediaController(mediaController);
            mediaController.hide();

            mVideoView.setVideoURI(Uri.parse(mVideoUrl));
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    adImage.setVisibility(View.INVISIBLE);

                    if (TextUtils.isEmpty(mBannerUrl)) {
                        showCloseWithDelay(SHOW_IMMEDIATELY);
                    }
                }
            });
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.seekTo(0);
                    mp.start();
                }
            });
            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    String message = "MediaPlayer error w=" + what + "e=" + extra;

                    Logger.w(message);
                    listener.onError(new Exception(message));

                    return true;
                }
            });
            mVideoView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        clickAd();
                        return true;
                    }

                    return mVideoView.onTouchEvent(event);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!TextUtils.isEmpty(mVideoUrl)) {
            mVideoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!TextUtils.isEmpty(mVideoUrl)) {
            mVideoView.pause();
        }
    }

    private void showCloseWithDelay(int delay) {
        mCloseButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCloseButton.setVisibility(View.VISIBLE);
            }
        }, delay);

        PortumFacade.processImpressions(mImpressionUrls);
    }

    private void clickAd() {
        if (TextUtils.isEmpty(mClickUrl)) {
            Logger.e("Promoted URL is empty");
        } else {
            try {
                Uri uri = Uri.parse(mClickUrl);
                boolean isMobileApp = "play.google.com".equals(uri.getHost());

                if (isMobileApp) {    // fix url to open market
                    String packageName = uri.getQueryParameter("id");
                    uri = Uri.parse("market://details?id=" + packageName);
                }

                PortumFacade.listener().onAdClick();

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            } catch (ActivityNotFoundException e) {
                Logger.w("Unable show url = " + mClickUrl, e);
                PortumFacade.listener().onError(e);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // prevent go back
    }
}
