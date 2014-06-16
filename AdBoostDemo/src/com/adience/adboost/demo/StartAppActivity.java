package com.adience.adboost.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.adience.adboost.AdBoost;
import com.adience.adboost.AdNet;
import com.adience.adboost.AdView;
import com.adience.adboost.Interstitial;
import com.adience.adboost.Interstitial.SubType;
import com.adience.adboost.demo.utils.LayoutUtils;
import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdDisplayListener;
import com.startapp.android.publish.AdEventListener;

public class StartAppActivity extends Activity {
    private static final AdNet MY_AD_NETWORK = AdNet.StartApp;
    private String MY_AD_NETWORK_DEVID;
    private String MY_AD_NETWORK_APPID;
    
    private AdView bannerFromXml;
    private AdView bannerFromCode;
    private Interstitial interstitial;
    private ViewGroup layout;
    private RadioGroup interstitialChoice;
    private ProgressBar progress;
    private Button showInterstitialButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AdBoost.appStarted(this, getString(R.string.adboostApiKey));
        
        MY_AD_NETWORK_DEVID = getString(R.string.startAppDevId);
        MY_AD_NETWORK_APPID = getString(R.string.startAppAppId);
        AdBoost.initAdNet(MY_AD_NETWORK, this, MY_AD_NETWORK_DEVID, MY_AD_NETWORK_APPID);
        setContentView(R.layout.activity_startapp);
        layout = LayoutUtils.getRootLayout(this);
        interstitialChoice = (RadioGroup)findViewById(R.id.interstitialChoice);
        progress = (ProgressBar)findViewById(R.id.progress_bar);
        progress.setVisibility(View.INVISIBLE);
        showInterstitialButton = (Button)findViewById(R.id.showInterstitialButton);
        showInterstitialButton.setEnabled(false);
        showBannerFromXml();
        createBannerProgrammatically();
        interstitial = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdBoost.appClosed(this);
    }

    private void showBannerFromXml() {
        bannerFromXml = (AdView)findViewById(R.id.adView);
        bannerFromXml.loadAd();
    }

    private void createBannerProgrammatically() {
        bannerFromCode = new AdView(this);
        bannerFromCode.setAdNetwork(MY_AD_NETWORK);
        float density = getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(320 * density), (int)(50 * density));
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        bannerFromCode.setLayoutParams(params);
        layout.addView(bannerFromCode, 0);
        bannerFromCode.loadAd();
    }

    public void showInterstitial(View view) {
        if(interstitial == null) {
            Toast.makeText(this, getText(R.string.first_load_interstitial), Toast.LENGTH_SHORT).show();
        } else if(interstitial.isReady()) {
            interstitial.show();
        } else {
            Toast.makeText(this, getText(R.string.interstitial_loading), Toast.LENGTH_SHORT).show();
        }
    }
    
    public void loadAutomatic(View view) {
        loadInterstitial(SubType.AUTOMATIC);
    }

    public void loadFullpage(View view) {
        loadInterstitial(SubType.FULL_PAGE);
    }

    public void loadApps(View view) {
        loadInterstitial(SubType.APPS);
    }

    public void loadOverlay(View view) {
        loadInterstitial(SubType.OVERLAY);
    }

    private void loadInterstitial(SubType subtype) {
        interstitial = new Interstitial(this, MY_AD_NETWORK);
        interstitial.setListener(new StartAppListener());
        progress.setVisibility(View.VISIBLE);
        interstitial.loadAd(subtype);
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
                    interstitialChoice.clearCheck();
                    progress.setVisibility(View.INVISIBLE);
                }
            });
            if(interstitial.isReady()) {
                showInterstitialButton.setEnabled(true);
            } else {
                interstitial = null;
            }
        }
        
        @Override
        public void adDisplayed(Ad arg0) {
        }

        @Override
        public void adHidden(Ad arg0) {
            interstitial = null;
            showInterstitialButton.setEnabled(false);
            interstitialChoice.clearCheck();
        }

        @Override
        public void onReceiveAd(Ad arg0) {
            showInterstitialButton.setEnabled(true);
            progress.setVisibility(View.INVISIBLE);
        }

    }
}
