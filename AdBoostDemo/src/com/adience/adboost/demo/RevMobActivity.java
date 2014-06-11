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
import com.revmob.RevMobAdsListener;

public class RevMobActivity extends Activity {
    private static final AdNet MY_AD_NETWORK = AdNet.RevMob;
    
    private AdView bannerFromXml;
    private AdView bannerFromCode;
    private Interstitial interstitial;
    private ViewGroup layout;
    private Button showInterstitialButton;
    private RadioGroup interstitialChoice;
    private ProgressBar progress;

    private boolean isTestMode = true;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdBoost.appStarted(this, getString(R.string.adboostApiKey));
        AdBoost.initAdNet(MY_AD_NETWORK, this);
        if(isTestMode) {
            AdBoost.enableTestMode(MY_AD_NETWORK);
        }
        setContentView(R.layout.activity_revmob);
        layout = (ViewGroup)findViewById(R.id.layout);
        interstitialChoice = (RadioGroup)findViewById(R.id.interstitialChoice);
        progress = (ProgressBar)findViewById(R.id.progress);
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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

    private void loadInterstitial(SubType subtype) {
        interstitial = new Interstitial(this, MY_AD_NETWORK);
        interstitial.setListener(new RevMobAdsListener() {
            @Override
            public void onRevMobAdNotReceived(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getString(R.string.interstitial_failed_msg_fmt, error);
                        Toast.makeText(RevMobActivity.this, msg, Toast.LENGTH_LONG).show();
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
            public void onRevMobAdDisplayed() {
                interstitial = null;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInterstitialButton.setEnabled(false);
                        interstitialChoice.clearCheck();
                    }
                });
            }

            @Override
            public void onRevMobAdClicked() {
            }

            @Override
            public void onRevMobAdDismiss() {
            }

            @Override
            public void onRevMobAdReceived() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInterstitialButton.setEnabled(true);
                        progress.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onRevMobSessionIsStarted() {
            }

            @Override
            public void onRevMobSessionNotStarted(String arg0) {
            }

        });
        progress.setVisibility(View.VISIBLE);
        interstitial.loadAd(subtype);
    }

    public void loadFullPage(View view) {
        loadInterstitial(SubType.FULL_PAGE);
    }
    
    public void loadOverlay(View view) {
        loadInterstitial(SubType.OVERLAY);
    }
    
}
