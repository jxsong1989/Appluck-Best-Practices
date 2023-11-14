# Appluck Best Practices - Android Version

[中文](https://github.com/jxsong1989/Appluck-Best-Practices/blob/main/Appluck_Android_Demo/README-CN.md) 
<br/>

[Github Repository](https://github.com/jxsong1989/Appluck-Best-Practices/edit/main/Appluck_Android_Demo)
<br/>
This project represents the best practices for integrating Appluck, providing developers with a reference implementation. For any questions, please contact the designated personnel.

<br/>

## Introduction
Appluck offers developers a new H5 monetization solution that generates incremental revenue without affecting the original in-game ads. The sources of incremental revenue include:

1. **New** non-standard ad placements, such as floating buttons/icons, push notifications, etc.
2. **Replacement** of standard ad placements with low monetization efficiency, such as late-exposure rewarded videos.
3. **Fallback** for standard ad placements with no fill/cannot load, such as video loading failures.

Best practices address developers' concerns:

**1. How to ensure that the exposure/income of existing ads does not decrease when adding new ad placements?**

- When adding new ad placements, the income from existing ads may slightly decrease, but the total revenue will significantly increase.

**2. How to ensure that the income after replacing standard ad placements is higher than before replacement?**

- When replacing ad placements, best practices recommend a replacement frequency that significantly increases total revenue.

The following sections introduce the most effective integration methods that have been verified through rigorous A/B testing.

<br/>

## Integration Process

### Operational Integration

1. Developers provide the application package name and ad placements to be integrated.
2. Appluck staff provide the corresponding links for each ad placement.

### Development Integration

1. Developers add or replace ad placements according to their requirements, opening the corresponding webpage using the guidance in the **WebView Technical Details** section.
   > The Appluck URL format is https://domain/scene?sk=xxxxxxxxxxxxxx&lzdid={gaid}
   >
   > + domain is the domain assigned to you by Appluck.
   >
   > + xxxxxxxxxxxxxx is the ad placement ID.
   >
   > + {gaid} is a macro that should be replaced with the user's Google Advertising ID when used.
   >
   > Example: The final URL to open will be like: https://aios.soinluck.com/scene?sk=q842c2e079a1b32c8&lzdid=228b9b29-784f-4181-bbc9-28cd14f672f4

2. **Use integration test links to verify the compatibility of various redirects.**

<br/>

## Appluck_Android_Demo Explanation
The AndroidDemo project demonstrates the recommended integration methods, timing, and positions for Appluck.
![avatar](https://github.com/jxsong1989/Best-practices-for-Appluck-in-Unity/blob/main/doc/index_android.jpg)

This is a typical app scenario that includes a floating button entry and a button triggering a rewarded video.

#### 1. Floating Button Placement

Here, the demo uses Appluck's API to dynamically fetch recommended floating button materials, keeping a replacement frequency of 30 seconds. This optimizes the click-through rate of materials. If you have specific requirements, you can also use fixed materials.

#### 2. Rewarded Video

In the demo, the second rewarded video is replaced with an Appluck activity. The eCPM of the second rewarded video is significantly lower than the first, showcasing Appluck's advantage in eCPM. If you have concerns about revenue, you can fine-tune operations, control replacement frequency from the backend, and conduct A/B testing to compare revenue.

The demo also changes the handling of ad loading failures to open an Appluck activity. This replacement is entirely incremental income and can be modified with confidence.

<br/>

## Must-Read - WebView Technical Details

Appluck activities are implemented using H5 and need to be loaded in a WebView when opened in a game. Because of the rich and diverse upstream ad budget on Appluck, the WebView itself needs to support certain protocols for redirects; otherwise, redirecting to ads may fail. Additionally, some responses need to be implemented for user backward behavior to prevent accidental exits from the WebView.

In the actual integration process, you need to encapsulate the WebView yourself to accomplish the necessary compatibility for Appluck.

#### Compatibility Requirements and Implementation

+ WebView support for URL protocols
  + Market link
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
  + APK download
    ```java
    webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    });
    ```
  + Others
    ```java
    Intent intent = new Intent(Intent.ACTION_VIEW);
    ActivityInfo activityInfo = intent.resolveActivityInfo(context.getPackageManager(), 0);
    if (activityInfo.exported) {
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    ```
 + WebView support for HTTP links
    + Add configuration to the application node in AndroidManifest.xml
      ```java
      android:usesCleartextTraffic="true"
      ```
    + If HTTP is explicitly not supported, consult Appluck's operations.
+ WebView support for webpage backward navigation
  Support webpage navigation backward instead of directly closing the page; refer to the code:
  
  ```java
  @Override
  public void onBackPressed() {
      if (webView == null) {
          super.onBackPressed();
          return;
      }
      if (webView.canGoBack()) {
          webView.goBack();
      else {
          super.onBackPressed();
      }
  }
  ```

+ WebView Support for Opening the Browser.
  Some Appluck ads require opening in an external browser, and these ads' URLs will include the parameter lz_open_browser=1. Developers should detect this parameter in the URL and, when present, launch the browser to handle the link. Reference code:
  
  ```java
   webView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              String url = request.getUrl().toString();
              try {
                  if (url.contains("lz_open_browser=1")) {
                       if (isAppInstalled(context, "com.android.chrome")) {
                          // Create an Intent specifying the ACTION_VIEW action and URL
                          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                          // Specify the package name of the browser to use
                          intent.setPackage("com.android.chrome"); // Package name for Chrome browser
                          // Start Chrome browser to handle the link
                          context.startActivity(intent);
                      } else {
                          // If Chrome browser is not installed, use the default system browser to open the link
                          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                          // Start the default browser to handle the link
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


Due to the extensive content, Appluck provides a testing solution.

#### Compatibility Testing

Open the link https://aios.soinluck.com/scene?sk=q842925369bb6199f&lzdid={gaid} in your pre-packaged WebView.

The link provides:

1. Parameter testing - The webpage attempts to read your parameters, and if the parameter format is incorrect, a prompt will be displayed.

2. Redirect testing - Links for various typical redirect methods are provided. Click on each one and confirm correct redirect results.
