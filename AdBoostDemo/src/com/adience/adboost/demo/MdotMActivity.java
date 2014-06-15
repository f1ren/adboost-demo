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
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.adience.adboost.AdBoost;
import com.adience.adboost.AdNet;
import com.adience.adboost.AdView;
import com.adience.adboost.Interstitial;
import com.adience.adboost.Interstitial.SubType;
import com.adience.adboost.demo.utils.LayoutUtils;
import com.mdotm.android.listener.MdotMAdEventListener;

public class MdotMActivity extends Activity {
    private static final AdNet MY_AD_NETWORK = AdNet.MdotM;
    private String MY_AD_NETWORK_ID;

    private AdView bannerFromXml;
    private AdView bannerFromCode;
    private Interstitial interstitial;
    private ViewGroup layout;
    private RadioGroup interstitialChoice;
    private ProgressBar progress;
    private Button showInterstitialButton;
    
    private boolean isTestMode = true;

    private final AdEventListener listener = new AdEventListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AdBoost.appStarted(this, getString(R.string.adboostApiKey));
        
        MY_AD_NETWORK_ID = getString(R.string.mdotmAppId);
        if(isTestMode) {
            AdBoost.enableTestMode(MY_AD_NETWORK);
        }

        setContentView(R.layout.activity_mdotm);
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
        bannerFromXml.destroy();
        bannerFromCode.destroy();
    }

    @Override
    public void onBackPressed() {
        if(interstitial != null && interstitial.dismiss()) {
            return;
        }
        super.onBackPressed();
    }
    
    private void showBannerFromXml() {
        bannerFromXml = (AdView)findViewById(R.id.adView);
        bannerFromXml.setListener(listener);
        bannerFromXml.loadAd();
    }

    private void createBannerProgrammatically() {
        bannerFromCode = new AdView(this);
        bannerFromCode.setAdNetwork(MY_AD_NETWORK, MY_AD_NETWORK_ID);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        bannerFromCode.setLayoutParams(params);
        layout.addView(bannerFromCode, 0);
        bannerFromCode.setListener(listener);
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

    private void loadInterstitial(SubType subtype) {
        interstitial = new Interstitial(this, MY_AD_NETWORK, MY_AD_NETWORK_ID);
        interstitial.setListener(listener);
        progress.setVisibility(View.VISIBLE);
        interstitial.loadAd(subtype);
    }

    public void loadFullpage(View view) {
        loadInterstitial(SubType.FULL_PAGE);
    }

    public void loadVideo(View view) {
        loadInterstitial(SubType.VIDEO);
    }

    public void loadAutomatic(View view) {
        loadInterstitial(SubType.AUTOMATIC);
    }

    private final class AdEventListener implements MdotMAdEventListener {

        @Override
        public void onReceiveBannerAd() {
            Log.d(MainActivity.TAG, "onReceiveBannerAd");
        }

        @Override
        public void onFailedToReceiveBannerAd() {
            Log.d(MainActivity.TAG, "onFailedToReceiveBannerAd");
        }

        @Override
        public void onFailedToReceiveInterstitialAd() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = getString(R.string.interstitial_failed);
                    Toast.makeText(MdotMActivity.this, msg, Toast.LENGTH_SHORT).show();
                    Log.w(MainActivity.TAG, msg);
                    interstitialChoice.clearCheck();
                    progress.setVisibility(View.INVISIBLE);
                    if(interstitial.isReady()) {
                        showInterstitialButton.setEnabled(true);
                    } else {
                        interstitial = null;
                    }
                }
            });
        }

        @Override
        public void onBannerAdClick() {
            Log.d(MainActivity.TAG, "onBannerAdClick");
        }

        @Override
        public void onInterstitialAdClick() {
            Log.d(MainActivity.TAG, "onInterstitialAdClick");
        }

        @Override
        public void onDismissScreen() {
            Log.d(MainActivity.TAG, "onDismissScreen");
        }

        @Override
        public void onInterstitialDismiss() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    interstitial = null;
                    showInterstitialButton.setEnabled(false);
                    interstitialChoice.clearCheck();
                }
            });
        }

        @Override
        public void onLeaveApplicationFromBanner() {
            Log.d(MainActivity.TAG, "onLeaveApplicationFromBanner");
        }

        @Override
        public void onLeaveApplicationFromInterstitial() {
            Log.d(MainActivity.TAG, "onLeaveApplicationFromInterstitial");
        }

        @Override
        public void onReceiveInterstitialAd() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showInterstitialButton.setEnabled(true);
                    progress.setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        public void willShowInterstitial() {
            Log.d(MainActivity.TAG, "willShowInterstitial");
        }

        @Override
        public void didShowInterstitial() {
            Log.d(MainActivity.TAG, "didShowInterstitial");
        }
    }

}
