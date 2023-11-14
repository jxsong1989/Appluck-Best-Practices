# Appluck Best Practices - Unity Version

[中文](https://github.com/jxsong1989/Appluck-Best-Practices/blob/main/Appluck_Android_Demo/README-CN.md) 
<br/>
<br/>
[Github Link](https://github.com/jxsong1989/Appluck-Best-Practices/edit/main/Appluck_Unity_Demo)
<br/>
This project represents the best practices for integrating Appluck, providing developers with a reference guide. If you have any questions, please contact the integration personnel.

<br/>

## Introduction
Appluck offers developers a new solution for H5 monetization, allowing for incremental revenue without affecting the original in-game advertisements. The sources of incremental revenue include:

1. **Addition** of non-standard ad spaces, such as floating banners/icons, push notifications, etc.
2. **Replacement** of standard ad spaces with low monetization efficiency, such as late-exposure rewarded videos.
3. **Fallback** for standard ad spaces with no fill or loading issues, such as video loading failures.

Best practices address common developer concerns:

**1. How to ensure that the exposure/revenue of existing ads is not diminished when adding new ad spaces that may affect user attention?**
   - When adding new ad spaces, the revenue from existing ads may slightly decrease, but the overall revenue will significantly increase.

**2. How to ensure that the revenue after replacing standard ad opportunities is higher than before the replacement?**
   - When replacing ad spaces, the best practice suggests a replacement frequency that significantly increases the overall revenue.

Below, we will introduce the most effective integration methods that have been rigorously tested through AB testing.

<br/>

## Integration Process

### Operational Integration

1. Developers provide the application package name and ad space for integration.
2. Appluck personnel provide the ad space links corresponding to each ad space.

### Development Integration

1. Developers add or replace ad spaces according to their requirements. Follow the guidance in the **WebView Technical Details** section to open the corresponding webpage.
   > Appluck URL format: https://domain/scene?sk=xxxxxxxxxxxxxx&lzdid={gaid}
   >
   > + domain is the domain assigned to you by Appluck.
   > + xxxxxxxxxxxxxx is the ad space ID.
   > + {gaid} is a macro that needs to be replaced with the user's Google Advertising ID when used.
   >
   > Example: The final URL opened will be like this: https://aios.soinluck.com/scene?sk=q842c2e079a1b32c8&lzdid=228b9b29-784f-4181-bbc9-28cd14f672f4

2. **Use the integration test link to verify the compatibility of various redirects.**

<br/>

## Appluck_Unity_Demo Explanation
UnityDemo project demonstrates the recommended integration methods, timing, and locations suggested by Appluck.
![avatar](https://github.com/jxsong1989/Best-practices-for-Appluck-in-Unity/blob/main/doc/index.jpg)

This is a typical game scene, including the game's score (Gold section), a button to add points (Add Gold), and several entrances to trigger ads.

#### 1. Dynamic Floating Banner
A typical scenario where developers want to display attention-grabbing ads to users after completing certain key steps. In the demo, this position is shown to the user when their gold coins reach 10. Clicking opens the Appluck activity page. Developers can control the visibility of the entrance based on actual conditions and can also make the floating banner move, such as flying a treasure chest over the screen after completing certain steps to attract user clicks.

#### 2. Rewarded Video
In the demo, the second rewarded video is replaced with an Appluck activity. The eCPM of the second rewarded video is significantly lower than the first, which is also a position where Appluck eCPM has an advantage. If you have concerns about revenue, you can fine-tune operations, control replacement frequency from the backend, and conduct AB tests to compare revenue.

The demo also changes the handling of failed ad loading to open the Appluck activity. This replacement is entirely incremental revenue and can be modified with confidence.

#### 3. Static Floating Banner
If there are no other considerations, the floating banner can be fixed in a specific location.

<br/>

## Must-See - WebView Technical Details

Appluck's activities are implemented in HTML5, and a WebView is required to open Appluck in the game. Due to the diverse budgets of upstream ads on Appluck, the WebView itself needs to support some protocols for redirection; otherwise, ad redirection may fail. In addition, some user behaviors, such as going back, need to be responded to in order to prevent accidental exits from the WebView.

During the actual integration process, you have two options:

### 1. Use an Already-Packaged WebView Plugin

It is recommended to use the Unity plugin LightWebView. [Link to Asset Store](https://assetstore.unity.com/packages/slug/264898)

This plugin is compatible with all Appluck redirection protocols. The source code of the plugin is available in the LightWebView folder of this project.

Example:

```c#
// Get the user's GAID first, replace the {gaid} macro in the ad space link with the actual GAID, and get the URL.
// Open the URL using the method provided by LightWebviewAndroid.
// Note: The close mode is set to CloseMode.back, meaning that when users perform operations such as swiping left on the screen or using the keyboard to go back, the WebView's back action is prioritized. It only closes the WebView when back is at the top level.
LightWebviewAndroid.instance.open(url, CloseMode.back);
```
### 2. Self-Packaging WebView or Using Other Third-Party WebView Plugins

In this case, you need to achieve some compatibility required by Appluck.

#### Compatibility Requirements and Implementation

+ WebView support for URL protocols
  + Market links
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
  + APK downloads
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
 + WebView support for http links
    + Add configuration to the application node in AndroidManifest.xml
      ```java
      android:usesCleartextTraffic="true"
      ```
    + If http is explicitly not supported, please clarify with Appluck operations.
+ WebView support for webpage back navigation
  Please support the webpage's back navigation instead of directly closing the page. Refer to the code:
  
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

