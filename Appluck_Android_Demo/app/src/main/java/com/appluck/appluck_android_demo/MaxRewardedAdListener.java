package com.appluck.appluck_android_demo;

import android.os.Handler;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.ads.MaxRewardedAd;

import java.util.concurrent.TimeUnit;

public class MaxRewardedAdListener implements com.applovin.mediation.MaxRewardedAdListener {


    private MaxRewardedAd rewardedAd;

    private int retryAttempt;

    public MaxRewardedAdListener(MaxRewardedAd rewardedAd) {
        this.rewardedAd = rewardedAd;
    }

    @Override
    public void onUserRewarded(MaxAd ad, MaxReward reward) {

    }

    @Override
    public void onRewardedVideoStarted(MaxAd ad) {

    }

    @Override
    public void onRewardedVideoCompleted(MaxAd ad) {
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        retryAttempt = 0;
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {
        rewardedAd.loadAd();
    }

    @Override
    public void onAdClicked(MaxAd ad) {
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rewardedAd.loadAd();
            }
        }, delayMillis);

    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        rewardedAd.loadAd();
    }
}
