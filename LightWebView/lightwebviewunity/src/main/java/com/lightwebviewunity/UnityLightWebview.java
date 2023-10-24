package com.lightwebviewunity;

import com.lightwebviewsdk.LigthWebview;
import com.unity3d.player.UnityPlayer;

public class UnityLightWebview {

    private UnityLightWebview() {

    }

    public static void open(String url) {
        open(url, 0);
    }

    public static void open(String url, int closeMode) {
        LigthWebview.open(UnityPlayer.currentActivity, url, closeMode);
    }
}
