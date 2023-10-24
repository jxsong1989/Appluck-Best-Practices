package com.lightwebviewsdk.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;


public class WebViewUtil {

    private WebViewUtil() {

    }

    public static void openUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        ActivityInfo activityInfo = intent.resolveActivityInfo(context.getPackageManager(), 0);
        if (activityInfo.exported) {
            intent.setData(Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void openBrowser(Context context, String url) {
        if (isAppInstalled(context, "com.android.chrome")) {
            // 创建一个 Intent，指定 ACTION_VIEW 动作和 URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            // 指定要使用的浏览器的包名
            intent.setPackage("com.android.chrome"); // Chrome 浏览器的包名
            // 启动 Chrome 浏览器来处理链接
            context.startActivity(intent);
        } else {
            // 如果没有安装 Chrome 浏览器，使用系统默认浏览器打开链接
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            // 启动默认浏览器来处理链接
            context.startActivity(intent);
        }
    }

    public static boolean openIntent(Context context, String url) {
        if (url.startsWith("market:")
                || url.startsWith("https://play.google.com/store/")
                || url.startsWith("http://play.google.com/store/")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.startsWith("market://details?id=")) {
                final String replace = url.replace("market://details", "https://play.google.com/store/apps/details");
                Log.d("LightWebview", "marketUrl replace: \n" + url + "\n" + replace);
                intent.setData(Uri.parse(replace));
            } else {
                intent.setData(Uri.parse(url));
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } else if (!url.startsWith("http://")
                && !url.startsWith("https://")) {
            openUrl(context, url);
            return true;
        } else if (url.contains("lz_open_browser=1")) {
            openBrowser(context, url);
            return true;
        }
        return false;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        if (packageName == null || packageName.length() <= 0) {
            return false;
        }
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
