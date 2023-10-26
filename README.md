# Best-practices-for-Appluck-in-Unity
Best practices for Appluck in Unity games.

## 为什么接
+ ### 在不影响体验、原有数据的前提下，通过新增广告位（如下）增量游戏收入
  + 浮标位（纯增量）
  + 激励视频替换位
  + 广告加载失败兜底（提高流量利用率）

## 接什么
+ ### Appluck_Unity_Demo
  UnityDemo工程，展示了Appluck的接入方式、时机和位置
  ![avatar](https://github.com/jxsong1989/Best-practices-for-Appluck-in-Unity/blob/main/doc/index.jpg)
  + 1：动态浮标位，点击通过LightWebView打开Appluck页面，可以根据用户属性或游戏进度来控制显示Appluck入口素材
  + 2：用户金币数，本demo中用户金币达到10时会显示1（动态浮标位）
  + 3：激励视频按钮，点击触发激励视频广告并且金币数+5，本demo中将第2次激励视频替换为Appluck页面
  + 4：增加金币按钮，点击只触发金币数+5，方便展示1（动态浮标位）
  + 5：固定浮标位，点击通过LightWebView打开Appluck页面，与1（动态浮标位）的区别是始终展示
  + 注：
    + ##### Appluck通常提供的是一条https链接，本demo推荐使用LightWebView来打开，开发者也可使用其他方案但WebView相关设置需要参考LightWebView
  
+ ### LightWebView
  轻量AndroidWebView工程，通过Android原生WebView打开网页，也可以直接使用Unity插件: https://assetstore.unity.com/packages/slug/264898
  ![avatar](https://github.com/jxsong1989/Best-practices-for-Appluck-in-Unity/blob/main/doc/back.jpg)
  + 1：返回按钮，点击触发网页后退，无法后退时触发页面关闭
    
  ```c#
  LightWebviewAndroid.instance.open(appluck_url, CloseMode.back);
  ```

  ![avatar](https://github.com/jxsong1989/Best-practices-for-Appluck-in-Unity/blob/main/doc/close.jpg)
  + 1：关闭按钮，点击触发页面关闭，无论网页是否可后退

  ```c#
  LightWebviewAndroid.instance.open(appluck_url, CloseMode.close);
  ```

## 怎么接
+  从Appluck运营处获取对应的广告位链接
+  接入LightWebView（使用其他方案则跳过该步骤）
  + 使用unity应用商店插件： https://assetstore.unity.com/packages/slug/264898 ，下载unitypackage文件并导入工程即可通过以下代码打开网页
    ```c#
    LightWebviewAndroid.instance.open(appluck_url, CloseMode.back);
    LightWebviewAndroid.instance.open(appluck_url, CloseMode.close);
    ```
+  在应用内合适的位置放置Appluck入口，在合适的时机打开Appluck页面

## AB Test

## 集成测试

## 常见问题
  
 
