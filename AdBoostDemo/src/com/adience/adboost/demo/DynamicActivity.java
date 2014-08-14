package com.adience.adboost.demo;

import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.adience.adboost.AdBoost;
import com.adience.adboost.AdNet;
import com.adience.adboost.AdSize;
import com.adience.adboost.AdView;
import com.adience.adboost.IAdListener;
import com.adience.adboost.Interstitial;
import com.adience.adboost.Interstitial.SubType;

public class DynamicActivity extends Activity {
    private static final String TAG = "AdBoost Demo";

    private AdView bannerFromXml;
    private AdView bannerFromCode;
    private Interstitial interstitial;
    private ViewGroup layout;
    private RadioGroup interstitialChoice;
    private ProgressBar progress;
    private Button showInterstitialButton;

    private void initializeAdUnits() {
	    interstitial = new Interstitial(this);
        interstitial.setListener(new InterstitialListener());
	    Set<SubType> supported = interstitial.getSupportedTypes();
	    interstitialChoice.clearCheck();
	    // Show only relevant interstitial subtypes:
	    for(int i = 0; i < interstitialChoice.getChildCount(); i++) {
	        View button = interstitialChoice.getChildAt(i);
	        SubType type = SubType.valueOf((String)button.getTag());
	        button.setVisibility(supported.contains(type) ? View.VISIBLE : View.GONE);
	    }
	    showInterstitialButton.setEnabled(interstitial.isReady()); // Is true if ad network doesn't support caching.
	    showBannerFromXml();
	    createBannerProgrammatically();
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AdBoost.appStarted(this, getString(R.string.adboostAppKey));
        AdBoost.enableTestMode(AdNet.DYNAMIC);
        setContentView(R.layout.activity_dynamic);
        layout = (ViewGroup)findViewById(R.id.layout);
        bannerFromXml = (AdView)findViewById(R.id.adView);
        interstitialChoice = (RadioGroup)findViewById(R.id.interstitialChoice);
        progress = (ProgressBar)findViewById(R.id.progress_bar);
        progress.setVisibility(View.INVISIBLE);
        showInterstitialButton = (Button)findViewById(R.id.showInterstitialButton);
        showInterstitialButton.setEnabled(false);
        initializeAdUnits();
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
    protected void onResume() {
        super.onResume();
        
        if(bannerFromXml != null) {
            bannerFromXml.resume();
        }
        if(bannerFromCode != null) {
            bannerFromCode.resume();
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
    public void onBackPressed() {
        if(interstitial != null && interstitial.dismiss()) {
            return;
        }
        
        super.onBackPressed();
    }
    
    private void showBannerFromXml() {
        bannerFromXml.setListener(new BannerListener("Bottom"));
        bannerFromXml.loadAd();
    }

    private void createBannerProgrammatically() {
        bannerFromCode = new AdView(this);
        bannerFromCode.setAdSize(AdSize.DEFAULT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        bannerFromCode.setLayoutParams(params);
        layout.addView(bannerFromCode, 0);
        bannerFromCode.setListener(new BannerListener("Top"));
        bannerFromCode.loadAd();
    }

    public void loadInterstitial(View view) {
		SubType subtype = SubType.valueOf((String)view.getTag());
		if(!interstitial.isReady()) {
			// Some ad networks don't support caching or caching notification, so isReady will always return true,
			// in which case don't show the progress indicator.
		    progress.setVisibility(View.VISIBLE);
		}
		interstitial.loadAd(subtype);
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

    
    //////////////// Ad Unit Listeners

	private class BannerListener implements IAdListener {
    	private String prefix;
    	
    	public BannerListener(String prefix) {
    		this.prefix = prefix;
		}
    	
		@Override
		public void adShown() {
		    Log.i(TAG, prefix + " AdView adShown");
		}

		@Override
		public void adReceived() {
		    Log.i(TAG, prefix + " AdView adReceived");
		}

		@Override
		public void adFailed(Object error) {
		    Log.i(TAG, prefix + " AdView adFailed: " + error);
		}

		@Override
		public void adDismissed() {
		    Log.i(TAG, prefix + " AdView adDismissed");
		}

		@Override
		public void adClicked() {
		    Log.i(TAG, prefix + " AdView adClicked");
		}
	}

	private class InterstitialListener implements IAdListener {
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
		            // Just in case
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
		            showInterstitialButton.setEnabled(interstitial.isReady());
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
		            showInterstitialButton.setEnabled(interstitial.isReady());
		            interstitialChoice.clearCheck();
		        }
		    });
		}
	}
}
