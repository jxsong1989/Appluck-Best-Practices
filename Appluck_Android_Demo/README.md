# Appluck最佳实践-Android版本

本项目为Appluck集成的最佳实践，开发者可借鉴参考。
若有疑问请与对接人员联系。

<br/>

## 介绍
Appluck为开发者提供新的h5变现解决方案，在不影响游戏原有广告的基础上，做出增量收入。
增量收入的来源：

1. **新增** 非标广告位：如浮标/icon，推送等形式。
2. **替换** 变现效率低的标准广告位：如曝光次序靠后的激励视频等。
3. **兜底** 无填充/无法加载的标准广告位：如视频加载失败等。



最佳实践可解答开发者的疑惑：

**1. 新增广告位会影响用户注意力，如何能保证原有广告的曝光/收入等没有下降？**

- 在新增广告位时，原有的广告收入可能略微降低，但总收入会显著提升

**2. 替换标准广告展现机会，如何能保证替换后的收入高于替换之前？**
- 在替换广告位时，最佳实践建议的替换频次是对总收入有显著提升的频次。


下文我们将介绍已被验证过最有效的接入方式，每种方式都经历严格的AB测试。  





<br/>

## 接入流程

### 运营对接

1. 开发者提供需要接入的应用包名、广告位。

2. Appluck工作人员提供每个广告位对应的广告位链接。


### 开发接入

1. 开发按照己方要求进行新增/替换广告位，根据下文的  **WebView技术细节** 指导，打开对应的网页
   > Appluck Url 格式为 https://domain/scene?sk=xxxxxxxxxxxxxx&lzdid={gaid}
   >
   > + domain为Appluck为您分配的域名
   >
   > + xxxxxxxxxxxxxx为广告位ID
   >
   > + {gaid}是一个宏，实际使用时需将{gaid}整体替换为用户的Google Advertising ID
   >
   > 例：最终打开的url如： https://aios.soinluck.com/scene?sk=q842c2e079a1b32c8&lzdid=228b9b29-784f-4181-bbc9-28cd14f672f4

2. **使用集成测试链接验证各种跳转是否已完美兼容**




<br/>

## Appluck_Android_Demo说明
AndroidDemo工程，展示了Appluck建议的接入方式、时机和位置
![avatar](https://github.com/jxsong1989/Best-practices-for-Appluck-in-Unity/blob/main/doc/index.jpg)

这是一个典型的App场景，包含了浮标位入口、触发激励视频的按钮

#### 1. 浮标位

此处使用了Appluck提供的浮标素材获取API 来动态获取推荐的浮标素材，并保持30秒的替换频率。这样可以极致优化素材的点击率。如有特殊要求，您也可以使用固定的素材。

#### 2. 激励视频

demo中将第2次激励视频替换为Appluck活动。
第2次激励视频的eCPM是显著低于第1次的，这也是Appluck eCPM有优势的位置。
若您对收益有疑虑，可精细化运营，从后端控制替换频次，并做AB测试来对比收益。

demo中也将广告加载失败的处理改为打开Appluck活动。
这样的替换完全是增量收入，可放心修改。




<br/>

## 必看-WebView技术细节

Appluck是由h5实现的活动集合，在游戏中打开Appluck时需要使用WebView承载。由于Appluck上游广告预算的丰富多样，WebView本身需要对一些跳转的协议进行支持，否则会出现跳转广告失败的问题。除此之外，还需要对用户的后退行为等做一些响应，防止误操作退出WebView。

在实际集成过程中，您需要自行封装WebView，以完成Appluck必须的一些兼容。



#### 兼容要求及实现方式

+ WebView对Url协议头的支持
  + market链接
    ```java
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
    }
    ```
  + apk下载
    ```java
    webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    });
    ```
  + 其他
    ```java
    Intent intent = new Intent(Intent.ACTION_VIEW);
    ActivityInfo activityInfo = intent.resolveActivityInfo(context.getPackageManager(), 0);
    if (activityInfo.exported) {
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    ```
 + WebView对http链接的支持
    + AndroidManifest.xml中application节点添加配置
      ```java
      android:usesCleartextTraffic="true"
      ```
    + 如明确不支持http，请与Appluck运营说明
+ WebView网页后退的支持
  请支持网页的后退而不是直接关闭页面，参考代码
  
  ```java
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
  ```
  
+ WebView打开浏览器的支持
  一些兼容性较差的广告需要通过浏览器打开，Appluck约定了url参数中包含参数lz_open_browser=1时需跳出应用通过浏览器打开，参考代码
  
  ```java
   webView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              String url = request.getUrl().toString();
              try {
                  if (url.contains("lz_open_browser=1")) {
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
                      return true;
                  }
              } catch (Throwable e) {
                  return true;
              }
          }
          return super.shouldOverrideUrlLoading(view, request);
      }
  });
  
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
  ```



由于内容较多，Appluck提供了测试方案

#### 兼容测试

请在您已封装好的WebView中打开链接  https://aios.soinluck.com/scene?sk=q842925369bb6199f&lzdid={gaid}

链接中提供了

1. 传参测试 - 网页尝试读取您的传参，若传参格式有误会有提醒。

2. 跳转测试 - 提供了多种典型跳转方式的链接，逐一点击并确认得到正确的跳转结果。





