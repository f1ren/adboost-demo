package com.adience.adboost.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adience.adboost.Interstitial;
import com.adience.adboost.AdNet;
import com.vhypskbuxnxbupnm.AdListener;

public class LeadBoltActivity extends Activity {
    private static final AdNet MY_AD_NETWORK = AdNet.LeadBolt;
    private static final String MY_BANNER_ID = "<YOUR LEADBOLT BANNER ID>";
    private static final String MY_INTERSTITIAL_ID = "<YOUR LEADBOLT INTERSTITIAL ID>";

    private Interstitial interstitial;
    private Interstitial banner;
    private boolean interstitialFailed;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // NOTE: if you are using this code for your main activity, make sure to add the following line:
        // AdBoost.appStarted(this, MainActivity.MY_ADBOOST_ID);
        setContentView(R.layout.activity_leadbolt);
        createBannerProgrammatically();
        loadInterstitial();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // NOTE: if you are using this code for your main activity, make sure to add the following line:
        // AdBoost.appClosed(this);
        interstitial.destroy();
        banner.destroy();
    }

    private void createBannerProgrammatically() {
        banner = new Interstitial(this, MY_AD_NETWORK, MY_BANNER_ID);
        banner.setListener(new AdListener() {
            @Override
            public void onAdCached() {
                banner.show();
            }

            @Override
            public void onAdAlreadyCompleted() {
            }

            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdCompleted() {
            }

            @Override
            public void onAdFailed() {
            }

            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdPaused() {
            }

            @Override
            public void onAdProgress() {
            }

            @Override
            public void onAdResumed() {
            }
        });
        banner.loadAd();
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
        interstitial.setListener(new AdListener() {
            
            @Override
            public void onAdFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getString(R.string.interstitial_failed);
                        Toast.makeText(LeadBoltActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.w(MainActivity.TAG, msg);
                    }
                });
                interstitialFailed = true;
            }
            
            @Override
            public void onAdClosed() {
                // load the next ad so it is ready when we click the button.
                interstitialFailed = false;
                interstitial.loadAd();
            }
            
            @Override
            public void onAdResumed() {
            }
            
            @Override
            public void onAdProgress() {
            }
            
            @Override
            public void onAdPaused() {
            }
            
            @Override
            public void onAdLoaded() {
            }
            
            @Override
            public void onAdCompleted() {
            }
            
            @Override
            public void onAdClicked() {
            }
            
            @Override
            public void onAdCached() {
            }
            
            @Override
            public void onAdAlreadyCompleted() {
            }
        });
        interstitialFailed = false;
        interstitial.loadAd();
    }

}
