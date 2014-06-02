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
import com.adience.adboost.AdView;
import com.adience.adboost.Interstitial;
import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdDisplayListener;
import com.startapp.android.publish.AdEventListener;

public class StartAppActivity extends Activity {
    private static final AdNet MY_AD_NETWORK = AdNet.StartApp;
    
    private AdView bannerFromXml;
    private AdView bannerFromCode;
    private Interstitial interstitial;
    private ViewGroup layout;
    private boolean interstitialFailed;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // NOTE: if you are using this code for your main activity, make sure to add the following line:
        // AdBoost.appStarted(this, MainActivity.MY_ADBOOST_ID);
        
        setContentView(R.layout.activity_startapp);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showBannerFromXml() {
        bannerFromXml = (AdView)findViewById(R.id.adView);
        bannerFromXml.loadAd();
    }

    private void createBannerProgrammatically() {
        bannerFromCode = new AdView(this);
        bannerFromCode.setAdNetwork(MY_AD_NETWORK);
        float density = getResources().getDisplayMetrics().density;
        LayoutParams params = new RelativeLayout.LayoutParams((int)(320 * density), (int)(50 * density));
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
        interstitial.setListener(new StartAppListener());
        interstitialFailed = false;
        interstitial.loadAd();
    }

    private class StartAppListener implements AdEventListener, AdDisplayListener {
        @Override
        public void onFailedToReceiveAd(Ad arg0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = getString(R.string.interstitial_failed);
                    Toast.makeText(StartAppActivity.this, msg, Toast.LENGTH_LONG).show();
                    Log.w(MainActivity.TAG, msg);
                }
            });
            interstitialFailed = true;
        }
        
        @Override
        public void adDisplayed(Ad arg0) {
            // load the next ad so it is ready when we click the button.
            interstitial.loadAd();
        }

        @Override
        public void adHidden(Ad arg0) {
        }

        @Override
        public void onReceiveAd(Ad arg0) {
        }

    }
}
