package com.adience.adboost.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.adience.adboost.AdBoost;
import com.adience.adboost.AdNet;
import com.adience.adboost.Interstitial;
import com.adience.adboost.Interstitial.SubType;
import com.chartboost.sdk.ChartboostDefaultDelegate;
import com.chartboost.sdk.Model.CBError.CBImpressionError;

public class ChartboostActivity extends Activity {
    private static final AdNet MY_AD_NETWORK = AdNet.Chartboost;
    private String MY_AD_NETWORK_APP_ID;
    private String MY_AD_NETWORK_APP_SIG;
    
    private Interstitial interstitial;
    private Button showInterstitialButton;
    private RadioGroup interstitialChoice;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AdBoost.appStarted(this, getString(R.string.adboostApiKey));
        
        MY_AD_NETWORK_APP_ID = getString(R.string.chartboostAppId);
        MY_AD_NETWORK_APP_SIG = getString(R.string.chartboostAppSignature);
        AdBoost.initAdNet(MY_AD_NETWORK, MY_AD_NETWORK_APP_ID, MY_AD_NETWORK_APP_SIG);
        setContentView(R.layout.activity_chartboost);
        interstitialChoice = (RadioGroup)findViewById(R.id.interstitialChoice);
        progress = (ProgressBar)findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);
        showInterstitialButton = (Button)findViewById(R.id.showInterstitialButton);
        showInterstitialButton.setEnabled(false);
        interstitial = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdBoost.appClosed(this);
    }
    
    @Override
    public void onBackPressed() {
        if(interstitial != null && interstitial.dismiss()) {
            return;
        }
        super.onBackPressed();
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
        interstitial.setListener(new ChartboostDefaultDelegate() {
            @Override
            public void didFailToLoadInterstitial(String location, final CBImpressionError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getString(R.string.interstitial_failed_msg_fmt, error.toString());
                        Toast.makeText(ChartboostActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.w(MainActivity.TAG, msg);
                        interstitialChoice.clearCheck();
                        progress.setVisibility(View.INVISIBLE);
                    }
                });
                if(interstitial != null && interstitial.isReady()) {
                    showInterstitialButton.setEnabled(true);
                } else {
                    interstitial = null;
                }
            }
            
            @Override
            public void didFailToLoadMoreApps(CBImpressionError error) {
                didFailToLoadInterstitial(null, error);
            }

            @Override
            public void didDismissInterstitial(String location) {
                interstitial = null;
                showInterstitialButton.setEnabled(false);
                interstitialChoice.clearCheck();
            }
            
            @Override
            public void didDismissMoreApps() {
                didDismissInterstitial(null);
            }
            
            @Override
            public void didCacheInterstitial(String location) {
                showInterstitialButton.setEnabled(true);
                progress.setVisibility(View.INVISIBLE);
            }
            
            @Override
            public void didCacheMoreApps() {
                didCacheInterstitial(null);
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
