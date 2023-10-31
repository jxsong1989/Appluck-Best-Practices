package com.appluck.appluck_android_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.bumptech.glide.Glide;
import com.lightwebviewsdk.LigthWebview;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String rv_placement_id = "014a35e960c054f0";
    private static MaxRewardedAd rewardedAd;

    private final static String appluck_placement_id = "q842c2e079a1b32c8";

    //google广告id,请通过google sdk动态获取
    private final static String gaid = "228b9b29-784f-4181-bbc9-28cd14f672f4";

    private final static String appluck_placement_url = "https://aios.soinluck.com/scene?sk=" + appluck_placement_id + "&lzdid=" + gaid;

    private final static String appluck_placement_material_api = "https://aios.soinluck.com/api/v1/placement/get/v2?sk=" + appluck_placement_id + "&gaid=" + gaid + "&type=icon";

    private static PlacementDtoV2 placementDtoV2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThreadPool.execute(() -> {
            try {
                //获取Appluck广告位素材
                final Connection.Response execute = Jsoup.connect(appluck_placement_material_api)
                        .ignoreContentType(true)
                        .ignoreContentType(true)
                        .execute();
                final String body = execute.body();
                JSONObject jsonObject = JSON.parseObject(body);
                placementDtoV2 = jsonObject.getObject("data", PlacementDtoV2.class);
                final PlacementDtoV2.Creative pop = placementDtoV2.pop();
                if (pop != null) {
                    runOnUiThread(() -> {
                        //设置浮标位
                        final ImageView icon = findViewById(R.id.icon);
                        Glide.with(this).load(pop.getSrc()).into(icon);
                        icon.setVisibility(View.VISIBLE);
                        icon.setOnClickListener((v) -> {
                            //closeMode 0:关闭; 1:网页返回;
                            LigthWebview.open(this, appluck_placement_url, 1);
                        });
                        //间隔10s切换一次浮标位素材
                        showIconDelayed(icon, 10000L);
                    });
                }
            } catch (IOException e) {
                //do nothing
            }

        });

        this.runOnUiThread(() -> {
            final AppLovinSdk appLovinSdk = AppLovinSdk.getInstance(this);
            appLovinSdk.setMediationProvider("max");
            AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
                @Override
                public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                    //是否开启Max Debugger 页面
                    //appLovinSdk.showMediationDebugger();
                    rewardedAd = MaxRewardedAd.getInstance(rv_placement_id, MainActivity.this);
                    rewardedAd.setListener(new MaxRewardedAdListener(rewardedAd));
                    rewardedAd.loadAd();
                    Toast.makeText(MainActivity.this, "Max init.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        final View rv = findViewById(R.id.btn_rv);
        rv.setOnClickListener((v) -> {
            if (rewardedAd != null && rewardedAd.isReady()) {
                //展示视频广告
                rewardedAd.showAd();
            } else {
                //当视频广告加载失败时展示Appluck互动广告
                //closeMode 0:关闭; 1:网页返回;
                LigthWebview.open(this, appluck_placement_url, 0);
                //继续加载视频广告
                if(rewardedAd != null){
                    rewardedAd.loadAd();
                }
            }
        });
    }

    /**
     * 定时切换浮标位素材
     * @param icon 浮标位
     * @param mills 切换间隔时间,毫秒
     */
    public void showIconDelayed(ImageView icon, long mills) {
        new Handler().postDelayed(() -> {
            Glide.with(this).load(placementDtoV2.pop().getSrc()).into(icon);
            showIconDelayed(icon, mills);
        }, mills);
    }
}