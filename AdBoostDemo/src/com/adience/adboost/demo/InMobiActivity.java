package com.adience.adboost.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import java.util.Map;

import com.adience.adboost.AdNet;
import com.adience.adboost.AdSize;
import com.adience.adboost.AdView;
import com.adience.adboost.Interstitial;
import com.inmobi.monetization.IMErrorCode;
import com.inmobi.monetization.IMInterstitial;
import com.inmobi.monetization.IMInterstitialListener;

public class InMobiActivity extends Activity {
    private static final AdNet MY_AD_NETWORK = AdNet.InMobi;
    private static final String MY_AD_NETWORK_ID = "<YOUR INMOBI APP ID>";

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
        setContentView(R.layout.activity_inmobi);
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
    }

    private void showBannerFromXml() {
        bannerFromXml = (AdView)findViewById(R.id.adView);
        bannerFromXml.loadAd();
    }

    private void createBannerProgrammatically() {
        bannerFromCode = new AdView(this);
        bannerFromCode.setAdNetwork(MY_AD_NETWORK, MY_AD_NETWORK_ID);
        bannerFromCode.setAdSize(AdSize.W320H50);
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
        interstitial.setListener(new IMInterstitialListener() {
            @Override
            public void onInterstitialFailed(IMInterstitial arg0, final IMErrorCode errorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getString(R.string.interstitial_failed_msg_fmt, errorCode.toString());
                        Toast.makeText(InMobiActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.w(MainActivity.TAG, msg);
                    }
                });
                interstitialFailed = true;
            }
            
            @Override
            public void onShowInterstitialScreen(IMInterstitial arg0) {
                // load the next ad so it is ready when we click the button.
                interstitialFailed = false;
                interstitial.loadAd();
            }

            @Override
            public void onDismissInterstitialScreen(IMInterstitial arg0) {
            }

            @Override
            public void onInterstitialInteraction(IMInterstitial arg0, Map<String, String> arg1) {
            }

            @Override
            public void onInterstitialLoaded(IMInterstitial arg0) {
            }

            @Override
            public void onLeaveApplication(IMInterstitial arg0) {
            }
        });
        interstitialFailed = false;
        interstitial.loadAd();
    }

}
