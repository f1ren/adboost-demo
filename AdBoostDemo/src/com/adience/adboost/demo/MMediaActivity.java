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
import com.millennialmedia.android.MMAd;
import com.millennialmedia.android.MMException;
import com.millennialmedia.android.RequestListener;

public class MMediaActivity extends Activity {
    private static final AdNet MY_AD_NETWORK = AdNet.MMedia;
    private String MY_BANNER_ID;
    private String MY_INTERSTITIAL_ID;
    
    private AdView bannerFromXml;
    private AdView bannerFromCode;
    private Interstitial interstitial;
    private ViewGroup layout;
    private boolean interstitialFailed;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AdBoost.appStarted(this, getString(R.string.adboostApiKey));
        
        MY_BANNER_ID = getString(R.string.mmediaBannerId);
        MY_INTERSTITIAL_ID = getString(R.string.mmediaInterstitialId);
        AdBoost.initAdNet(MY_AD_NETWORK, this);
        setContentView(R.layout.activity_mmedia);
        layout = (ViewGroup)findViewById(R.id.layout);
        showBannerFromXml();
        createBannerProgrammatically();
        loadInterstitial();
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
        bannerFromCode.setAdNetwork(MY_AD_NETWORK, MY_BANNER_ID);
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
        interstitial.setListener(new RequestListener() {
            @Override
            public void requestFailed(MMAd arg0, final MMException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getString(R.string.interstitial_failed_msg_fmt, e.getMessage());
                        Toast.makeText(MMediaActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.w(MainActivity.TAG, msg);
                    }
                });
                interstitialFailed = true;
            }
            
            @Override
            public void MMAdOverlayLaunched(MMAd arg0) {
            }

            @Override
            public void MMAdOverlayClosed(MMAd arg0) {
                // load the next ad so it is ready when we click the button.
                interstitialFailed = false;
                interstitial.loadAd();
            }

            @Override
            public void MMAdRequestIsCaching(MMAd arg0) {
            }

            @Override
            public void onSingleTap(MMAd arg0) {
            }

            @Override
            public void requestCompleted(MMAd arg0) {
            }

        });
        interstitialFailed = false;
        if(!interstitial.isReady()) {
            interstitial.loadAd();
        }
    }

}
