package com.lightwebviewsdk;

import android.content.Context;
import android.content.Intent;

public class LigthWebview {

    private LigthWebview() {
    }

    public static void open(Context context, String url) {
        open(context, url, 0);
    }

    public static void open(Context context, String url, int closeMode) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("closeMode", closeMode);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
