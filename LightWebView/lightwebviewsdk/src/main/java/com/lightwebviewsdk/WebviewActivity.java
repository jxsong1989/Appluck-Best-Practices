package com.lightwebviewsdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.lightwebviewsdk.util.WebViewUtil;

public class WebviewActivity extends Activity {

    private WebView webView;

    private Button close;

    private Button back;

    private View closeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String url = request.getUrl().toString();
                    try {
                        if (WebViewUtil.openIntent(WebviewActivity.this, url)) {
                            return true;
                        }
                    } catch (Throwable e) {
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        //下载处理
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        Intent intent = getIntent();
        closeView = findViewById(R.id.close_view);
        final GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(R.dimen.close_view_corner_radius);
        gradientDrawable.setColor(0x99000000);
        closeView.setBackground(gradientDrawable);
        close = findViewById(R.id.close_view_btn);
        back = findViewById(R.id.back_btn);
        int closeMode = intent.getIntExtra("closeMode", 0);
        if (closeMode == 1) {
            back.setOnClickListener((v) -> {
                onBackPressed();
            });
            back.setVisibility(View.VISIBLE);
            close.setVisibility(View.GONE);
        } else {
            close.setOnClickListener((v) -> {
                super.onBackPressed();
            });
            back.setVisibility(View.GONE);
            close.setVisibility(View.VISIBLE);
        }
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 允许javascript执行
        webSettings.setDomStorageEnabled(true);// 打开本地缓存提供JS调用,至关重要，开启DOM缓存，开启LocalStorage存储
        String url = intent.getStringExtra("url");
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView == null) {
            super.onBackPressed();
            return;
        }
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}