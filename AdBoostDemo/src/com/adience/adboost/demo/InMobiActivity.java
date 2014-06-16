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

import java.util.Map;

import com.adience.adboost.AdBoost;
import com.adience.adboost.AdNet;
import com.adience.adboost.AdSize;
import com.adience.adboost.AdView;
import com.adience.adboost.Interstitial;
import com.adience.adboost.Interstitial.SubType;
import com.adience.adboost.demo.utils.LayoutUtils;
import com.inmobi.monetization.IMErrorCode;
import com.inmobi.monetization.IMInterstitial;
import com.inmobi.monetization.IMInterstitialListener;

public class InMobiActivity extends Activity {
    private static final AdNet MY_AD_NETWORK = AdNet.InMobi;
    private String MY_AD_NETWORK_ID;

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
        
        MY_AD_NETWORK_ID = getString(R.string.inmobiAppId);
        AdBoost.initAdNet(MY_AD_NETWORK, this, MY_AD_NETWORK_ID);
        setContentView(R.layout.activity_inmobi);
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
        bannerFromXml.loadAd();
    }

    private void createBannerProgrammatically() {
        bannerFromCode = new AdView(this);
        bannerFromCode.setAdNetwork(MY_AD_NETWORK, MY_AD_NETWORK_ID);
        bannerFromCode.setAdSize(AdSize.W320H50);
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
        interstitial = new Interstitial(this, MY_AD_NETWORK, MY_AD_NETWORK_ID);
        interstitial.setListener(new IMInterstitialListener() {
            @Override
            public void onInterstitialFailed(IMInterstitial arg0, final IMErrorCode errorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getString(R.string.interstitial_failed_msg_fmt, errorCode.toString());
                        Toast.makeText(InMobiActivity.this, msg, Toast.LENGTH_SHORT).show();
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
            public void onShowInterstitialScreen(IMInterstitial arg0) {
            }

            @Override
            public void onDismissInterstitialScreen(IMInterstitial arg0) {
                interstitial = null;
                showInterstitialButton.setEnabled(false);
                interstitialChoice.clearCheck();
            }

            @Override
            public void onInterstitialInteraction(IMInterstitial arg0, Map<String, String> arg1) {
            }

            @Override
            public void onInterstitialLoaded(IMInterstitial arg0) {
                showInterstitialButton.setEnabled(true);
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLeaveApplication(IMInterstitial arg0) {
            }
        });
        progress.setVisibility(View.VISIBLE);
        interstitial.loadAd(subtype);
    }

    public void loadFullpage(View view) {
        loadInterstitial(SubType.FULL_PAGE);
    }

    public void loadApps(View view) {
        loadInterstitial(SubType.APPS);
    }

}
