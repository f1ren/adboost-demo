package com.adience.adboost.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.adience.adboost.AdBoost;

public class MainActivity extends Activity {

    protected static final String MY_ADBOOST_ID = "<YOUR ADBOOST ID HERE>";
    protected static final String TAG = "AdBoost Demo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdBoost.appStarted(this, MY_ADBOOST_ID);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdBoost.appClosed(this);
    }

    private void startDemoActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
    
    public void loadAdMob(View b) {
        startDemoActivity(AdMobActivity.class);
    }

    public void loadInMobi(View b) {
        startDemoActivity(InMobiActivity.class);
    }

    public void loadMMedia(View b) {
        startDemoActivity(MMediaActivity.class);
    }

    public void loadMoPub(View b) {
        startDemoActivity(MoPubActivity.class);
    }

    public void loadInneractive(View b) {
        startDemoActivity(InneractiveActivity.class);
    }

    public void loadRevMob(View b) {
        startDemoActivity(RevMobActivity.class);
    }

    public void loadStartApp(View b) {
        startDemoActivity(StartAppActivity.class);
    }

    public void loadAirPush(View b) {
        startDemoActivity(AirPushActivity.class);
    }

    public void loadLeadBolt(View b) {
        startDemoActivity(LeadBoltActivity.class);
    }
}
