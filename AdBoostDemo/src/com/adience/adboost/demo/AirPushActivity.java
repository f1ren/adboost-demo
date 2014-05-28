package com.adience.adboost.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.adience.adboost.AdView;
import com.adience.adboost.Interstitial;
import com.adience.adboost.AdNet;
import com.rfjpqmxwto.ymboaqjqbz187028.AdListener;

public class AirPushActivity extends Activity {
    private static final AdNet MY_AD_NETWORK = AdNet.AirPush;
    
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
        setContentView(R.layout.activity_airpush);
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
    }

    private void showBannerFromXml() {
        bannerFromXml = (AdView)findViewById(R.id.adView);
        if(isTestMode) {
            bannerFromXml.enableTestMode(null);
        }
        bannerFromXml.loadAd();
    }

    private void createBannerProgrammatically() {
        bannerFromCode = new AdView(this);
        bannerFromCode.setAdNetwork(MY_AD_NETWORK);
        if(isTestMode) {
            bannerFromCode.enableTestMode(null);
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
        interstitial = new Interstitial(this, MY_AD_NETWORK);
        interstitial.setListener(new AdListener() {
            @Override
            public void onAdError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getString(R.string.interstitial_failed_msg_fmt, error);
                        Toast.makeText(AirPushActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.w(MainActivity.TAG, msg);
                    }
                });
                interstitialFailed = true;
            }
            
            @Override
            public void noAdAvailableListener() {
                onAdError("No ad available");
            }

            @Override
            public void onSDKIntegrationError(String msg) {
                onAdError(msg);
            }

            @Override
            public void onAdCached(AdType arg0) {
            }

            @Override
            public void onSmartWallAdClosed() {
            }

            @Override
            public void onSmartWallAdShowing() {
            }

        });
        interstitialFailed = false;
        interstitial.loadAd();
    }

}
