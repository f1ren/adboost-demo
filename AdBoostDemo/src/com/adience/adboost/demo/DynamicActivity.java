package com.adience.adboost.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Set;

import com.adience.adboost.AdBoost;
import com.adience.adboost.AdNet;
import com.adience.adboost.AdView;
import com.adience.adboost.DefaultAdListener;
import com.adience.adboost.IAdListener;
import com.adience.adboost.Interstitial;
import com.adience.adboost.Interstitial.SubType;
import com.adience.adboost.demo.Mediation.AdParams;

public class DynamicActivity extends Activity {
    private static final String ADNET_STATE_KEY = "AdNet";

    // Replace AdNet.values() with the ad networks you are engaged with:
    private static final AdNet[] MY_AD_NETWORKS = AdNet.values();

    private Mediation mediation;
    
    private String MY_TEST_DEVICE_ID;
    private AdView bannerFromXml;
    private AdView bannerFromCode;
    private Interstitial interstitial;
    private ViewGroup layout;
    private RadioGroup interstitialChoice;
    private ProgressBar progress;
    private Button showInterstitialButton;
    
    private boolean isTestMode = true;

    private AdNet myAdNetwork;

    private Interstitial leadBoltBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdBoost.appStarted(this, getString(R.string.adboostApiKey));
        MY_TEST_DEVICE_ID = getString(R.string.admobTestDeviceId);
        setContentView(R.layout.activity_dynamic);
        mediation = new Mediation(this, MY_AD_NETWORKS);
        if(savedInstanceState != null) {
            String adnetStr = savedInstanceState.getString(ADNET_STATE_KEY);
            if(adnetStr != null) {
                myAdNetwork = AdNet.valueOf(adnetStr);
            }
        }
        layout = (ViewGroup)findViewById(R.id.layout);
        bannerFromXml = (AdView)findViewById(R.id.adView);
        interstitialChoice = (RadioGroup)findViewById(R.id.interstitialChoice);
        progress = (ProgressBar)findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);
        showInterstitialButton = (Button)findViewById(R.id.showInterstitialButton);
        showInterstitialButton.setEnabled(false);
        interstitial = null;
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(myAdNetwork != null) {
            outState.putString(ADNET_STATE_KEY, myAdNetwork.name());
        }
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(myAdNetwork == null) {
            openOptionsMenu();
        } else {
            selectAdNetwork();
        }
        setSubtitleCompat();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setTitleCompat(AdNet adnet) {
        getActionBar().setTitle(adnet.name());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setSubtitleCompat() {
        getActionBar().setSubtitle(R.string.change_in_menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdBoost.appClosed(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dynamic_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        myAdNetwork = AdNet.valueOf(item.getTitle().toString());
        selectAdNetwork();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(interstitial != null && interstitial.dismiss()) {
            return;
        }
        super.onBackPressed();
    }
    
    private void showBannerFromXml(AdNet adnet, String adViewId) {
        if(AdView.supports(adnet)) {
            bannerFromXml.setVisibility(View.VISIBLE);
        } else {
            bannerFromXml.setVisibility(View.INVISIBLE);
            return;
        }
        bannerFromXml.setAdNetwork(adnet, adViewId);
        bannerFromXml.setListener(new IAdListener() {
            
            @Override
            public void adShown() {
                Log.i(MainActivity.TAG, "XML AdView adShown");
            }
            
            @Override
            public void adReceived() {
                Log.i(MainActivity.TAG, "XML AdView adReceived");
            }
            
            @Override
            public void adFailed(Object error) {
                Log.i(MainActivity.TAG, "XML AdView adFailed: " + error);
            }
            
            @Override
            public void adDismissed() {
                Log.i(MainActivity.TAG, "XML AdView adDismissed");
            }
            
            @Override
            public void adClicked() {
                Log.i(MainActivity.TAG, "XML AdView adClicked");
            }
        });
        bannerFromXml.loadAd();
    }

    private void createBannerProgrammatically(AdNet adnet, String adViewId) {
        if(bannerFromCode != null) {
            layout.removeView(bannerFromCode);
        }
        if(!AdView.supports(adnet)) {
            if(adnet == AdNet.LeadBolt) {
                // LeadBolt has banners but they are not compatible with AdView.
                createLeadBoltBanner(adViewId);
            }
            return;
        }
        bannerFromCode = new AdView(this);
        bannerFromCode.setAdNetwork(adnet, adViewId);
        float density = getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, Math.round(50*density));
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        bannerFromCode.setLayoutParams(params);
        layout.addView(bannerFromCode, 0);
        bannerFromCode.setListener(new IAdListener() {
            
            @Override
            public void adShown() {
                Log.i(MainActivity.TAG, "code AdView adShown");
            }
            
            @Override
            public void adReceived() {
                Log.i(MainActivity.TAG, "code AdView adReceived");
            }
            
            @Override
            public void adFailed(Object error) {
                Log.i(MainActivity.TAG, "code AdView adFailed: "+error);
            }
            
            @Override
            public void adDismissed() {
                Log.i(MainActivity.TAG, "code AdView adDismissed");
            }
            
            @Override
            public void adClicked() {
                Log.i(MainActivity.TAG, "code AdView adClicked");
            }
        });
        bannerFromCode.loadAd();
    }

    private void createLeadBoltBanner(String adViewId) {
        leadBoltBanner = new Interstitial(this, AdNet.LeadBolt, adViewId);
        leadBoltBanner.setListener(new DefaultAdListener() {
            @Override
            public void adReceived() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        leadBoltBanner.show();
                    }
                });
            }
        });
        leadBoltBanner.loadAd();
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
    
    public void loadOverlay(View view) {
        loadInterstitial(SubType.OVERLAY);
    }
    
    public void loadApps(View view) {
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
        interstitial.setListener(new IAdListener() {

            @Override
            public void adReceived() {
                Log.i(MainActivity.TAG, "Interstitial adReceived");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInterstitialButton.setEnabled(true);
                        progress.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void adFailed(final Object error) {
                Log.i(MainActivity.TAG, "Interstitial adFailed");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getString(R.string.interstitial_failed_msg_fmt, error);
                        Toast.makeText(DynamicActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.w(MainActivity.TAG, msg);
                        interstitialChoice.clearCheck();
                        progress.setVisibility(View.INVISIBLE);
                        if(interstitial != null && interstitial.isReady()) {
                            showInterstitialButton.setEnabled(true);
                        }
                    }
                });
            }

            @Override
            public void adShown() {
                Log.i(MainActivity.TAG, "Interstitial adShown");
                // For AdNets that don't support adDismissed:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInterstitialButton.setEnabled(false);
                        interstitialChoice.clearCheck();
                    }
                });
            }

            @Override
            public void adClicked() {
                Log.i(MainActivity.TAG, "Interstitial adClicked");
            }

            @Override
            public void adDismissed() {
                Log.i(MainActivity.TAG, "Interstitial adDismissed");
                // For AdNets that don't support adShown:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInterstitialButton.setEnabled(false);
                        interstitialChoice.clearCheck();
                    }
                });
            }
        });
        progress.setVisibility(View.VISIBLE);
        interstitial.loadAd(subtype);
    }
    
    private void selectAdNetwork() {
        setTitleCompat(myAdNetwork);
        if(isTestMode) {
            AdBoost.enableTestMode(myAdNetwork, MY_TEST_DEVICE_ID);
        }
        AdParams params = mediation.getParams(myAdNetwork);
        interstitial = new Interstitial(this, myAdNetwork, params.interstitialId);
        Set<SubType> supported = interstitial.getSupportedTypes();
        interstitialChoice.clearCheck();
        // Show only relevant interstitial subtypes:
        for(int i = 0; i < interstitialChoice.getChildCount(); i++) {
            View button = interstitialChoice.getChildAt(i);
            SubType type = SubType.valueOf((String)button.getTag());
            button.setVisibility(supported.contains(type) ? View.VISIBLE : View.GONE);
        }
        showInterstitialButton.setEnabled(false);
        if(leadBoltBanner != null) {
            leadBoltBanner.destroy();
        }
        showBannerFromXml(myAdNetwork, params.adViewId);
        createBannerProgrammatically(myAdNetwork, params.adViewId);
    }
}
