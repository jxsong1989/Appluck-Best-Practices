using System;
using System.Collections;
using System.Collections.Generic;
using TMPro;
using UnityEngine;
using UnityEngine.UI;
using static LightWebviewAndroid;
using static MainCtrl;

public class MainCtrl : MonoBehaviour
{

    public Canvas canvas;

    //固定浮标
    public Button icon_fixed;

    //动态浮标
    public Button icon_floating;

    //max激励视频
    public Button max_reward;

    //增加金币
    public Button add_gold;

    //用户金币数，每次激励视频 +5
    private int gold = 0;

    public Text gold_text;

    //用户激励视频次数
    private int reward_times = 0;

    //appluck 提供的链接，这里是测试链接，请替换为分配的链接
    private const string appluck_url = "https://aios.soinluck.com/scene?sk=q842c2e079a1b32c8&lzdid=228b9b29-784f-4181-bbc9-28cd14f672f4";

    //Max配置
    private MaxConfig maxConfig;

    private void Awake()
    {
        maxConfig = new MaxConfig();
        MaxSdkCallbacks.OnSdkInitializedEvent += (MaxSdkBase.SdkConfiguration sdkConfiguration) =>
        {
            maxConfig.InitializeRewardedAds();
            //MaxSdk.ShowMediationDebugger();
        };

        MaxSdk.SetSdkKey("1n01ilZ9JQclLQLMrrk5sQUcTPJoSbgCFKaQzvT4RqEQv88-UNmW31T_TPxm4wuBpCqLtQ66b5vc5ltMZcWGFp");
        MaxSdk.InitializeSdk();
      
        canvas.gameObject.SetActive(true);

        icon_fixed.onClick.AddListener(() => {
            LightWebviewAndroid.instance.open(appluck_url, CloseMode.close);
        });

        icon_floating.onClick.AddListener(() => {
            LightWebviewAndroid.instance.open(appluck_url, CloseMode.back);
        });

        max_reward.onClick.AddListener(() => {
            reward_times += 1;
            if (reward_times == 2) {
                LightWebviewAndroid.instance.open(appluck_url, CloseMode.back);
                toast("Show Appluck instead of the 2nd rewarded video");
            }
            else
            {
                if (MaxSdk.IsRewardedAdReady(MaxConfig.reward_unit_id))
                {
                    MaxSdk.ShowRewardedAd(MaxConfig.reward_unit_id);
                }
                else {
                    LightWebviewAndroid.instance.open(appluck_url, CloseMode.back);
                    toast("Ad load failed , showing Appluck");
                }
            }
            gold += 5;
            gold_text.text = gold.ToString();
        });

        add_gold.onClick.AddListener(() => {
            gold += 5;
            gold_text.text = gold.ToString();
        });
    }

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        if (gold >= AppluckConfig.icon_floating_show_min)
        {
            icon_floating.gameObject.SetActive(true);
            icon_floating.enabled = true;
        }
        else
        {
            icon_floating.gameObject.SetActive(false);
            icon_floating.enabled = false;
        }
    }

    //推荐通过服务器远程配置，可动态修改
    public class AppluckConfig {
        //动态浮标（icon_floating）显示条件，用户金币最小值
        public const int icon_floating_show_min = 10;

        //替换激励的次数
        public const int reward_replace_times = 2;
    }

    public class MaxConfig : MonoBehaviour{
        public const string reward_unit_id = "014a35e960c054f0";
        public const string inter_unit_id = "63bcc5da9ed405bf";
        int retryAttempt;
        public void InitializeRewardedAds()
        {
            // Attach callback
            MaxSdkCallbacks.Rewarded.OnAdLoadedEvent += OnRewardedAdLoadedEvent;
            MaxSdkCallbacks.Rewarded.OnAdLoadFailedEvent += OnRewardedAdLoadFailedEvent;
            MaxSdkCallbacks.Rewarded.OnAdDisplayedEvent += OnRewardedAdDisplayedEvent;
            MaxSdkCallbacks.Rewarded.OnAdClickedEvent += OnRewardedAdClickedEvent;
            MaxSdkCallbacks.Rewarded.OnAdRevenuePaidEvent += OnRewardedAdRevenuePaidEvent;
            MaxSdkCallbacks.Rewarded.OnAdHiddenEvent += OnRewardedAdHiddenEvent;
            MaxSdkCallbacks.Rewarded.OnAdDisplayFailedEvent += OnRewardedAdFailedToDisplayEvent;
            MaxSdkCallbacks.Rewarded.OnAdReceivedRewardEvent += OnRewardedAdReceivedRewardEvent;

            // Load the first rewarded ad
            LoadRewardedAd();

        }
        private void LoadRewardedAd()
        {
            MaxSdk.LoadRewardedAd(reward_unit_id);
        }

        private void OnRewardedAdLoadedEvent(string adUnitId, MaxSdkBase.AdInfo adInfo)
        {
            // Rewarded ad is ready for you to show. MaxSdk.IsRewardedAdReady(adUnitId) now returns 'true'.

            // Reset retry attempt
            retryAttempt = 0;
        }

        private void OnRewardedAdLoadFailedEvent(string adUnitId, MaxSdkBase.ErrorInfo errorInfo)
        {
            // Rewarded ad failed to load 
            // AppLovin recommends that you retry with exponentially higher delays, up to a maximum delay (in this case 64 seconds).

            retryAttempt++;
            double retryDelay = Math.Pow(2, Math.Min(6, retryAttempt));

            Invoke("LoadRewardedAd", (float)retryDelay);
        }

        private void OnRewardedAdDisplayedEvent(string adUnitId, MaxSdkBase.AdInfo adInfo) { }

        private void OnRewardedAdFailedToDisplayEvent(string adUnitId, MaxSdkBase.ErrorInfo errorInfo, MaxSdkBase.AdInfo adInfo)
        {
            // Rewarded ad failed to display. AppLovin recommends that you load the next ad.
            LoadRewardedAd();
        }

        private void OnRewardedAdClickedEvent(string adUnitId, MaxSdkBase.AdInfo adInfo) { }

        private void OnRewardedAdHiddenEvent(string adUnitId, MaxSdkBase.AdInfo adInfo)
        {
            // Rewarded ad is hidden. Pre-load the next ad
            LoadRewardedAd();
        }

        private void OnRewardedAdReceivedRewardEvent(string adUnitId, MaxSdk.Reward reward, MaxSdkBase.AdInfo adInfo)
        {
            // The rewarded ad displayed and the user should receive the reward.
        }

        private void OnRewardedAdRevenuePaidEvent(string adUnitId, MaxSdkBase.AdInfo adInfo)
        {
            // Ad revenue paid. Use this callback to track user revenue.
        }
    }

    void toast(string text)
    {
        AndroidJavaClass UnityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject currentActivity = UnityPlayer.GetStatic<AndroidJavaObject>("currentActivity"); ;
        AndroidJavaClass Toast = new AndroidJavaClass("android.widget.Toast");
        AndroidJavaObject context = currentActivity.Call<AndroidJavaObject>("getApplicationContext");
        currentActivity.Call("runOnUiThread", new AndroidJavaRunnable(() =>
        {
            Toast.CallStatic<AndroidJavaObject>("makeText", context, text, Toast.GetStatic<int>("LENGTH_SHORT")).Call("show");
        }
        ));
    }
}
