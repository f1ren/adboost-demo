package com.adience.adboost.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.adience.adboost.AdBoost;
import com.adience.adboost.AdNet;
import com.adience.adboost.AdView;
import com.adience.adboost.Interstitial;
import com.adience.adboost.demo.utils.LayoutUtils;
import com.inneractive.api.ads.InneractiveInterstitialAdListener;

public class InneractiveActivity extends Activity {
    private static final AdNet MY_AD_NETWORK = AdNet.Inneractive;
    private String MY_AD_NETWORK_ID;

    private AdView bannerFromXml;
    private AdView bannerFromCode;
    private Interstitial interstitial;
    private ViewGroup layout;
    private boolean interstitialFailed;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AdBoost.appStarted(this, getString(R.string.adboostAppKey));
        
        MY_AD_NETWORK_ID = getString(R.string.inneractiveAppId);
        setContentView(R.layout.activity_inneractive);
        layout = LayoutUtils.getRootLayout(this);
        showBannerFromXml();
        createBannerProgrammatically();
        loadInterstitial();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdBoost.appClosed(this);
        bannerFromXml.destroy();
        bannerFromCode.destroy();
    }

    private void showBannerFromXml() {
        bannerFromXml = (AdView)findViewById(R.id.adView);
        bannerFromXml.loadAd();
    }

    private void createBannerProgrammatically() {
        bannerFromCode = new AdView(this);
        bannerFromCode.setAdNetwork(MY_AD_NETWORK, MY_AD_NETWORK_ID);
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
        interstitial = new Interstitial(this, MY_AD_NETWORK, MY_AD_NETWORK_ID);
        interstitial.setListener(new InneractiveInterstitialAdListener() {
            @Override
            public void onIaFailedToLoadInterstitialAd() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getString(R.string.interstitial_failed);
                        Toast.makeText(InneractiveActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.w(MainActivity.TAG, msg);
                    }
                });
                interstitialFailed = true;
            }
            
            @Override
            public void onIaDismissScreen() {
                // load the next ad so it is ready when we click the button.
                interstitialFailed = false;
                interstitial.loadAd();
            }

            @Override
            public void onIaDefaultInterstitialAdLoaded() {
            }

            @Override
            public void onIaInterstitialAdClicked() {
            }

            @Override
            public void onIaInterstitialAdLoaded() {
            }

        });
        interstitialFailed = false;
        interstitial.loadAd();
    }

}
