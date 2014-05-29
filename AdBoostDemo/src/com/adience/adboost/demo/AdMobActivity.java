package com.adience.adboost.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.adience.adboost.AdNet;
import com.adience.adboost.AdSize;
import com.adience.adboost.AdView;
import com.adience.adboost.Interstitial;
import com.google.android.gms.ads.AdListener;

public class AdMobActivity extends Activity {
    private static final AdNet MY_AD_NETWORK = AdNet.AdMob;
    private static final String MY_TEST_DEVICE_ID = "<YOUR TEST DEVICE ID>";
    private static final String MY_BANNER_ID = "<YOUR BANNER ID>";
    private static final String MY_INTERSTITIAL_ID = "<YOUR INTERSTITIAL ID>";
    
    private AdView bannerFromXml;
    private AdView bannerFromCode;
    private Interstitial interstitial;
    private ViewGroup layout;
    private boolean interstitialFailed;
    
    private boolean isTestMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // NOTE: if you are using this code for your main activity, make sure to add the following line:
        // AdBoost.appStarted(this, MainActivity.MY_ADBOOST_ID);
        setContentView(R.layout.activity_admob);
        layout = (ViewGroup)findViewById(R.id.layout);
        showBannerFromXml();
        createBannerProgrammatically();
        loadInterstitial();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // NOTE: if you are using this code for your main activity, make sure to add the following line:
        // AdBoost.appClosed(this);
        bannerFromXml.destroy();
        bannerFromCode.destroy();
        interstitial.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bannerFromXml.resume();
        bannerFromCode.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bannerFromXml.pause();
        bannerFromCode.pause();
    }

    private void showBannerFromXml() {
        bannerFromXml = (AdView)findViewById(R.id.adView);
        if(isTestMode) {
            bannerFromXml.enableTestMode(MY_TEST_DEVICE_ID);
        }
        bannerFromXml.loadAd();
    }

    private void createBannerProgrammatically() {
        bannerFromCode = new AdView(this);
        bannerFromCode.setAdNetwork(MY_AD_NETWORK, MY_BANNER_ID);
        bannerFromCode.setAdSize(AdSize.W320H50);
        if(isTestMode) {
            bannerFromCode.enableTestMode(MY_TEST_DEVICE_ID);
        }
        LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        bannerFromCode.setLayoutParams(params);
        layout.addView(bannerFromCode);
        bannerFromCode.loadAd();
    }

    public void showInterstitial(View view) {
        if(interstitial.isReady()) {
            interstitial.show();
        } else {
            if(interstitialFailed) { 
                Toast.makeText(this, getText(R.string.reloading_interstitial), Toast.LENGTH_SHORT).show();
                interstitialFailed = false;
                interstitial.loadAd();
            } else {
                Toast.makeText(this, getText(R.string.interstitial_loading), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadInterstitial() {
        interstitial = new Interstitial(this, MY_AD_NETWORK, MY_INTERSTITIAL_ID);
        if(isTestMode) {
            interstitial.enableTestMode(MY_TEST_DEVICE_ID);
        }
        interstitial.setListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(final int errorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getString(R.string.interstitial_failed_code_fmt, errorCode);
                        Toast.makeText(AdMobActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.w(MainActivity.TAG, msg);
                    }
                });
                interstitialFailed = true;
            }
            
            @Override
            public void onAdOpened() {
                // load the next ad so it is ready when we click the button.
                interstitialFailed = false;
                interstitial.loadAd();
            }
        });
        interstitialFailed = false;
        interstitial.loadAd();
    }

}
