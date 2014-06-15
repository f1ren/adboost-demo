package com.adience.adboost.demo.mediation;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

import com.adience.adboost.AdBoost;
import com.adience.adboost.AdNet;
import com.adience.adboost.demo.R;

public class Mediation {
    
    public static class AdParams {
        public final String adViewId;
        public final String interstitialId;
        
        public AdParams(String adViewId, String interstitialId) {
            this.adViewId = adViewId;
            this.interstitialId = interstitialId;
        }
    }
    
    private Map<AdNet, Mediation.AdParams> adnetMediation;
    
    public Mediation(Activity activity, AdNet[] networks) {
        adnetMediation = new HashMap<AdNet, Mediation.AdParams>();
        Mediation.AdParams adnetParams;
        String appId;
        
        for(AdNet adnet : networks) {
            switch(adnet) {
            case AdMob:
                adnetParams = new AdParams(activity.getString(R.string.admobBannerId), activity.getString(R.string.admobInterstitialId));
                adnetMediation.put(AdNet.AdMob, adnetParams);
                break;

            case AirPush:
                adnetMediation.put(AdNet.AirPush, new AdParams(null, null));
                break;

            case Chartboost:
                AdBoost.initAdNet(AdNet.Chartboost, activity.getString(R.string.chartboostAppId), activity.getString(R.string.chartboostAppSignature));
                adnetParams = new AdParams(null, null);
                adnetMediation.put(AdNet.Chartboost, adnetParams);
                break;

            case InMobi:
                appId = activity.getString(R.string.inmobiAppId);
                AdBoost.initAdNet(AdNet.InMobi, activity, appId);
                adnetParams = new AdParams(appId, appId);
                adnetMediation.put(AdNet.InMobi, adnetParams);
                break;

            case Inneractive:
                appId = activity.getString(R.string.inneractiveAppId);
                adnetParams = new AdParams(appId, appId);
                adnetMediation.put(AdNet.Inneractive, adnetParams);
                break;

            case LeadBolt:
                adnetParams = new AdParams(activity.getString(R.string.leadboltBannerId), activity.getString(R.string.leadboltInterstitialId));
                adnetMediation.put(AdNet.LeadBolt, adnetParams);
                break;

            case MMedia:
                AdBoost.initAdNet(AdNet.MMedia, activity);
                adnetParams = new AdParams(activity.getString(R.string.mmediaBannerId), activity.getString(R.string.mmediaInterstitialId));
                adnetMediation.put(AdNet.MMedia, adnetParams);
                break;

            case MoPub:
                adnetParams = new AdParams(activity.getString(R.string.mopubBannerId), activity.getString(R.string.mopubInterstitialId));
                adnetMediation.put(AdNet.MoPub, adnetParams);
                break;

            case RevMob:
                AdBoost.initAdNet(AdNet.RevMob, activity);
                adnetMediation.put(AdNet.RevMob, new AdParams(null, null));
                break;

            case StartApp:
                AdBoost.initAdNet(AdNet.StartApp, activity, activity.getString(R.string.startAppDevId), activity.getString(R.string.startAppAppId));
                adnetParams = new AdParams(null, null);
                adnetMediation.put(AdNet.StartApp, adnetParams);
                break;
                
            case MdotM:
                adnetParams = new AdParams(activity.getString(R.string.mdotmAppId), activity.getString(R.string.mdotmAppId));
                adnetMediation.put(AdNet.MdotM, adnetParams);
                break;
            }
        }
    }
    
    public Mediation.AdParams getParams(AdNet adnet) {
        return adnetMediation.get(adnet);
    }
}