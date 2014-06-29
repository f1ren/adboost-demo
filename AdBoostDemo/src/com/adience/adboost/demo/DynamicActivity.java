package com.adience.adboost.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
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
import com.adience.adboost.AdSize;
import com.adience.adboost.AdView;
import com.adience.adboost.IAdListener;
import com.adience.adboost.Interstitial;
import com.adience.adboost.Interstitial.SubType;
import com.adience.adboost.demo.mediation.Mediation;
import com.adience.adboost.demo.mediation.Mediation.AdParams;
import com.adience.adboost.demo.utils.LayoutUtils;

public class DynamicActivity extends Activity {
    private static final String TAG = "AdBoost Demo";
    private static final String ADNET_STATE_KEY = "AdNet";

    // Replace AdNet.values() with the ad networks you are engaged with.
    // Make sure to fill-in the respective values in: res/values/adnet_config.xml
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
        
        if(MY_AD_NETWORKS == null || MY_AD_NETWORKS.length == 0) {
            noAdNetworks();
            return;
        }

        AdBoost.appStarted(this, getString(R.string.adboostAppKey));
        MY_TEST_DEVICE_ID = getString(R.string.admobTestDeviceId);
        setContentView(R.layout.activity_dynamic);
        mediation = new Mediation(this, MY_AD_NETWORKS);
        if(savedInstanceState != null) {
            String adnetStr = savedInstanceState.getString(ADNET_STATE_KEY);
            if(adnetStr != null) {
                myAdNetwork = AdNet.valueOf(adnetStr);
            }
        }
        layout = LayoutUtils.getRootLayout(this);
        bannerFromXml = (AdView)findViewById(R.id.adView);
        interstitialChoice = (RadioGroup)findViewById(R.id.interstitialChoice);
        progress = (ProgressBar)findViewById(R.id.progress_bar);
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
            if(MY_AD_NETWORKS != null && MY_AD_NETWORKS.length > 0) {
                myAdNetwork = MY_AD_NETWORKS[0];
            }
        }
        if(myAdNetwork != null) {
            selectAdNetwork();
        }
        setSubtitleCompat();
    }

    private void noAdNetworks() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_ad_networks).setNegativeButton(R.string.exit, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();                
            }
        }).setOnCancelListener(new OnCancelListener() {
            
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setTitleCompat(AdNet adnet) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setTitle(adnet.name());
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setSubtitleCompat() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setSubtitle(R.string.change_in_menu);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(bannerFromXml != null) {
            bannerFromXml.pause();
        }
        if(bannerFromCode != null) {
            bannerFromCode.pause();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdBoost.appClosed(this);
        if(bannerFromXml != null) {
            bannerFromXml.destroy();
        }
        if(bannerFromCode != null) {
            bannerFromCode.destroy();
        }
        if(interstitial != null) {
            interstitial.destroy();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(MY_AD_NETWORKS == null) {
            return false;
        }
        for(AdNet adnet : MY_AD_NETWORKS) {
            menu.add(adnet.name());
        }
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
        bannerFromXml.setAdSize(AdSize.W320H50);
        bannerFromXml.setListener(new IAdListener() {
            
            @Override
            public void adShown() {
                Log.i(TAG, "XML AdView adShown");
            }
            
            @Override
            public void adReceived() {
                Log.i(TAG, "XML AdView adReceived");
            }
            
            @Override
            public void adFailed(Object error) {
                Log.i(TAG, "XML AdView adFailed: " + error);
            }
            
            @Override
            public void adDismissed() {
                Log.i(TAG, "XML AdView adDismissed");
            }
            
            @Override
            public void adClicked() {
                Log.i(TAG, "XML AdView adClicked");
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
        bannerFromCode.setAdSize(AdSize.DEFAULT);
        float density = getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, Math.round(50*density));
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        bannerFromCode.setLayoutParams(params);
        layout.addView(bannerFromCode, 0);
        bannerFromCode.setListener(new BannerFromCodeListener());
        bannerFromCode.loadAd();
    }

    private void createLeadBoltBanner(String adViewId) {
        leadBoltBanner = new Interstitial(this, AdNet.LeadBolt, adViewId);
        leadBoltBanner.setListener(new BannerFromCodeListener());
        leadBoltBanner.show(); // LeadBolt ad can show also without calling loadAd first.
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
                Log.i(TAG, "Interstitial adReceived");
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
                Log.i(TAG, "Interstitial adFailed");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = getString(R.string.interstitial_failed_msg_fmt, error);
                        Toast.makeText(DynamicActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.w(TAG, msg);
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
                Log.i(TAG, "Interstitial adShown");
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
                Log.i(TAG, "Interstitial adClicked");
            }

            @Override
            public void adDismissed() {
                Log.i(TAG, "Interstitial adDismissed");
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
        // Some ad networks don't support caching or caching notification, so isReady will always return true,
        if(!interstitial.isReady()) { // in which case don't show the progress indicator.
            progress.setVisibility(View.VISIBLE);
        }
        interstitial.loadAd(subtype);
    }
    
    private void selectAdNetwork() {
        setTitleCompat(myAdNetwork);
        if(isTestMode) {
            AdBoost.enableTestMode(myAdNetwork, MY_TEST_DEVICE_ID);
        }
        AdParams params = mediation.getParams(myAdNetwork);
        if(interstitial != null) {
            interstitial.destroy();
        }
        interstitial = new Interstitial(this, myAdNetwork, params.interstitialId);
        Set<SubType> supported = interstitial.getSupportedTypes();
        interstitialChoice.clearCheck();
        // Show only relevant interstitial subtypes:
        for(int i = 0; i < interstitialChoice.getChildCount(); i++) {
            View button = interstitialChoice.getChildAt(i);
            SubType type = SubType.valueOf((String)button.getTag());
            button.setVisibility(supported.contains(type) ? View.VISIBLE : View.GONE);
        }
        showInterstitialButton.setEnabled(interstitial.isReady()); // Is true if ad network doesn't support caching.
        if(leadBoltBanner != null) {
            leadBoltBanner.destroy();
        }
        showBannerFromXml(myAdNetwork, params.adViewId);
        createBannerProgrammatically(myAdNetwork, params.adViewId);
    }

    private static class BannerFromCodeListener implements IAdListener {
        @Override
        public void adShown() {
            Log.i(TAG, "code AdView adShown");
        }
    
        @Override
        public void adReceived() {
            Log.i(TAG, "code AdView adReceived");
        }
    
        @Override
        public void adFailed(Object error) {
            Log.i(TAG, "code AdView adFailed: "+error);
        }
    
        @Override
        public void adDismissed() {
            Log.i(TAG, "code AdView adDismissed");
        }
    
        @Override
        public void adClicked() {
            Log.i(TAG, "code AdView adClicked");
        }
    }
}
