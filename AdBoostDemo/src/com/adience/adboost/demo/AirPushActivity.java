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
import com.rfjpqmxwto.ymboaqjqbz187028.AdListener;

public class AirPushActivity extends Activity {
    private static final AdNet MY_AD_NETWORK = AdNet.AirPush;
    
    private AdView bannerFromXml;
    private AdView bannerFromCode;
    private Interstitial interstitial;
    private ViewGroup layout;
    private RadioGroup interstitialChoice;
    private ProgressBar progress;
    private Button showInterstitialButton;
    
    private boolean isTestMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdBoost.appStarted(this, getString(R.string.adboostApiKey));
        if(isTestMode) {
            AdBoost.enableTestMode(MY_AD_NETWORK);
        }
        setContentView(R.layout.activity_airpush);
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

    public void loadSmartWall(View view) {
        loadInterstitial(SubType.AUTOMATIC);
    }
    
    public void loadOverlay(View view) {
        loadInterstitial(SubType.OVERLAY);
    }
    
    public void loadAppWall(View view) {
        loadInterstitial(SubType.APPS);
    }
    
    public void loadLandingPage(View view) {
        loadInterstitial(SubType.LANDING_PAGE);
    }
    
    public void loadFullPage(View view) {
        loadInterstitial(SubType.FULL_PAGE);
    }
    
    public void loadVideo(View view) {
        loadInterstitial(SubType.VIDEO);
    }
    
    private void loadInterstitial(SubType subtype) {
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
            public void noAdAvailableListener() {
                onAdError("No ad available");
            }

            @Override
            public void onSDKIntegrationError(String msg) {
                onAdError(msg);
            }

            @Override
            public void onAdCached(AdType arg0) {
                showInterstitialButton.setEnabled(true);
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSmartWallAdClosed() {
                interstitial = null;
                showInterstitialButton.setEnabled(false);
                interstitialChoice.clearCheck();
            }

            @Override
            public void onSmartWallAdShowing() {
            }

        });
        progress.setVisibility(View.VISIBLE);
        interstitial.loadAd(subtype);
    }

}
