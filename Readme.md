# 又拍云短视频 

1. 提供短视频的拍摄、编辑、合成、上传等基础功能。

2. 提供播放器支持。


## 目录
1 [短视频](#1)

2 [上传](#2)

3 [播放器](#3)

<h2 id="1">短视频</h2>

## 配置环境

### 1.1 基本介绍

* 短视频拍摄、编辑、合成部分，包含断点录制、分段回删、美颜、大眼、瘦脸、滤镜、贴纸、视频剪辑、视频压缩、本地转码在内的 30 多种功能，支持自定义界面和二次开发。


### 1.2 运行环境

* Eclipse 或 Android Studio 1.3 + ，Android 系统 4.3 +

### 1.3 密钥 和 资源

* 使用短视频 SDK 需要授权（key）。请提供应用名称、申请使用的 SDK 版本、使用的平台（安卓、iOS）、包名给您的商务经理，或者[联系我们](https://www.upyun.com/contact) 来获取授权。

### 1.4 环境配置

1、[联系我们](https://www.upyun.com/contact) 获取资源文件，导入至 lib 以及 assert 目录。

### 1.5 TuSDK 的初始化

1、打开全局 `Application` 文件，全局 `Application` 类可以选择继承 `TuSdkApplication` 或不继承，然后在 `onCreate` 方法中使用 `TuSdk.init()` 方法来进行初始化，并将复制的密钥作为该方法的参数，如下：


	@Override
	public void onCreate()
	{
		TuSdk.enableDebugLog(true);
	    // 初始化SDK (请将目标项目所对应的密钥放在这里)
	    TuSdk.init(this.getApplicationContext(), "12aa4847a3a9ce68-04-ewdjn1");
	}

2、为方便开发时定位错误，可打开 TuSDK 的调试日志，即在初始化密钥之前添加以下代码（放在初始化密钥之后无效）：

 `TuSdk.enableDebugLog(true);`


发布应用时请关闭日志。

### 1.6 配置 AndroidManifest

在 [`AndroidManifest.xml`](https://github.com/TuSDK/TuSDK-for-Android-demo/blob/master/TuSDKDemo/AndroidManifest.xml) 中，首先定义程序需要的权限：


     <!-- 访问网络 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <!-- 获取WIFI信息 -->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <!-- 允许访问GPS -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <!-- 允许应用程序写数据到外部存储设备（主要是SD卡） -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <!-- 在sdcard中创建/删除文件的权限 -->
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
    <!-- 请求访问使用照相设备 -->
    <uses-permission android:name="android.permission.CAMERA" />
    <!-- 开启闪光灯权限 -->
    <uses-permission android:name="android.permission.FLASHLIGHT" />

    <!-- 访问麦克风 -->
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />

    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.autofocus" />
    <uses-feature android:name="android.hardware.camera.flash" />

    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.READ_LOGS" />



然后定义应用的全局 `Application` 对象，设置 `allowBackup`、`hardwareAccelerated` 和 `largeHeap` 三个重要选项为 `true`。


	<application
	    android:name="org.lasque.tusdk.TuApplication"
	    android:allowBackup="true"
	    android:hardwareAccelerated="true"
	    android:largeHeap="true"
	</application>
  


## 录制相机的使用

### 1.1 请求权限

除了按照集成向导步骤在AndroidManifest.xml加上权限外，在Android 6.0后都需申请动态权限，以下是打开相机所需申请的权限： `String[] permissions = new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };`

### 1.2 构建对象

创建并配置 `TuSdkRecorderVideoCamera` 对象，代码如下：

      RelativeLayout cameraView = (RelativeLayout) findViewById(R.id.lsq_cameraView);
    
      // 录制相机配置，目前只支持硬编
      TuSdkRecorderCameraSetting captureSetting = new TuSdkRecorderCameraSetting();
    
      TuSdkRecorderVideoCamera videoCamera = new TuSdkRecorderVideoCameraImpl(getBaseContext(),cameraView,captureSetting);
    

构造方法中需要传入下面三个参数：

（1）当前上下文（`Context`）

（2）相机采集配置（`TuSdkRecorderCameraSetting`）

（3）相机视图容器（`RelativeLayout`）

1.3 相机采集配置
----------

`TuSDKVideoCaptureSetting` 中可以对相机采集时的多项属性进行设置，包括：

*   `facing`，相机朝向（默认: `CameraFacing.Front` 前置）
    
*   相机视图容器是一个 `RelativeLayout` 布局，一般在 XML 布局文件中指定，传入 `TuSDKRecordVideoCamera` 构造方法中后，会将相机预览视图加载在该视图容器中。
    

1.4 相机设置
--------

获取录制相机对象后，可以对该相机的属性进行一些设置。

*   开启或关闭动态贴纸

    // 是否开启动态贴纸(默认: false)
    public void setEnableLiveSticker(boolean enableLiveStickr)
    

*   开启或关闭自动持续对焦

    // 禁用自动持续对焦 (默认: false)
    public void setDisableContinueFocus(boolean disableContinueFoucs)
    

*   设置水印(图片最大边长不宜超过 500)

    // 设置水印，默认为空
    public void setWaterMarkImage(Bitmap mWaterMarkImage)
    // 设置水印位置
    public void setWaterMarkPosition(WaterMarkPosition mWaterMarkPosition)
    

*   设置视频录制结果委托

    public void setRecorderVideoCameraCallback(TuSdkRecorderVideoCameraCallback recorderCallback)
    

指定录制相机的事件委托，该委托中有下面四个接口用来通知相机状态改变：

*   设置最小、最大录制时长（单位：/s）

    // 设置最小录制时长
    public void setMinRecordingTime(int minRecordingTime) 
    // 设置最大录制时长
    public void setMaxRecordingTime(int maxRecordingTime)
    

*   设置相机事件委托

使用 `setDelegate(TuSDKVideoCameraDelegate)` 设置相机事件委托，详细可以参看 demo 中的使用示例

*   设置相机拍照监听

    public void setCameraListener(TuSdkCameraListener cameraListener)
    

*   设置特效改变监听

    public void setMediaEffectChangeListener(TuSdkMediaEffectChangeListener mediaEffectChangeListener)
    

*   设置人脸检测结果回调

    public void setFaceDetectionCallback(TuSdkFaceDetectionCallback faceDetectionCallback)
    

*   设置视频编码配置

    public final void setVideoEncoderSetting(TuSdkRecorderVideoEncoderSetting videoEncoderSetting) 
    

可以设置视频编码时的参数，包括：

`videoSize`，输出视频尺寸（默认：`TuSdkSize(320, 480)`）

`videoQuality`，视频质量

`mediacodecAVCIFrameInterval`，I 帧时间间隔 （默认：1）

`enableAllKeyFrame` 是否最大限度输出 I 帧，默认false

推荐使用 SDK 默认的编码配置，如下：

    // 推荐编码配置
    TuSdkRecorderVideoEncoderSetting encoderSetting = TuSdkRecorderVideoEncoderSetting.getDefaultRecordSetting();
    mVideoCamera.setVideoEncoderSetting(encoderSetting);
    

*   保存系统相册

    // 保存系统相册 (默认保存, 当设置为 false 时, 保存为临时文件)
    public void setSaveToAlbum(boolean mSaveToAlbum)
    // 保存到系统相册的相册名称
    public void setSaveToAlbumName(String mSaveToAlbumName)
    

当设置 false 可 `TuSdkRecorderVideoCameraCallback` 回调中获取result.

*   添加开启人脸检测

    public void setEnableFaceDetection(boolean enableFaceDetection) 
    

*   是否启用音频录制

    public void setEnableAudioCapture(boolean mEnableAudioCapture)
    

### 1.5 相机操作接口

SDK 提供了多个操作相机的方法，供用户操作相机开启、关闭、恢复、暂停，如下：

*   启动相机采集
    
        startCameraCapture()
        
    
*   停止相机采集
    
        stopCameraCapture()
        
    
*   恢复相机采集
    
        resumeCameraCapture()
        
    
*   暂停相机采集
    
        pauseCameraCapture()
        
    

同时，这些方法需要配合所在 Activity 的生命周期中使用，如下：

      @Override
      protected void onResume()
      {
        super.onResume();
        resumeCameraCapture();
      }
    
      @Override
      protected void onPause()
      {
        super.onPause();
        pauseCameraCapture();
      }
    
      @Override
      protected void onDestroy()
      {
        super.onDestroy();
        stopCameraCapture();
    
        if (mVideoCamera != null) {
            mVideoCamera.destroy();
        }
      }
    

### 1.6 录制接口

录制相机对象可以通过下面四个接口控制开启、暂停和停止录制动作，以及判断是否正在录制中：

*   开始录制
    
        startRecording()
        
    
*   暂停录制
    
        pauseRecording()
        
    
*   结束录制
    
        stopRecording()
        
    
*   判断是否正在录制中
    
        isRecording()
        
    
*   录制结果
    

配合`setSaveToAlbum`，设置true将视频保存到相册,设置`false`通过`TuSDKVideoResult`获取对应视频的临时文件，可进行自定义操作。

    // 设置录制委托
    public void setVideoDelegate(TuSDKRecordVideoCameraDelegate mDelegate)
    

    /** 录制相机事件委托 */
    public static interface TuSdkRecorderVideoCameraCallback
    {
        /**
         * 视频录制结果
         * 
         * @param result
         *            视频结果
         */
        void onMovieRecordComplete(TuSDKVideoResult result);
    
        /**
         * 录制进度改变 (运行在主线程)
         * 
         * @param progress 当前录制进度( 0 - 1 ) 相对于mMaxRecordingTime
         * @param durationTime 当前录制持续时间 单位：/s
         */
        void onMovieRecordProgressChanged(float progress, float durationTime);
    
        /**
         * 录制状态改变
         * @param state
         */
        void onMovieRecordStateChanged(RecordState state);
    
        /**
         * 录制出错
         * 
         */
        void onMovieRecordFailed(RecordError error);
    
    }
    

### 1.7 相机状态

*   相机状态
    
         /** 相机运行状态 */
         public enum CameraState
         {
            /** 未知状态 */
            StateUnknow,
            /** 正在启动 */
            StateStarting,
            /** 已经启动 */
            StateStarted,
            /** 正在拍摄 */
            StateCapturing,
            /** 拍摄完成 */
            StateCaptured
         }
        
    
*   录制状态
    
        /** 录制状态 */
        public enum RecordState
        {
         /** 录制中 */
         Recording,
        
         /** 正在保存视频 */
         Saving,
        
         /** 暂停录制 */
         Paused,
        
         /** 录制完成 */
         RecordCompleted,
        
         /** 已取消 */
         Canceled,
        }
        
    
*   错误状态
    
        /** 录制时错误 */
        public enum RecordError
        {
         /** 未知错误 */
         Unknow,
        
         /** 可用空间不足 */
         NotEnoughSpace,
        
         /** 无效的录制时间（录制时间较短无法生成视频） */
         InvalidRecordingTime,
        
         /** 低于最小录制时间 */
         LessMinRecordingTime,
        
         /** 超过最大录制时间 */
         MoreMaxDuration,
        
         /** 保存失败 */
         SaveFailed,
        
        }
        
    

### 1.8 相机监听接口

*   拍摄图片
    
        captureImage()
        
    
*   可通过`TuSdkCameraListener` 获取拍照结果
    
        /** Video Camera Delegate */
        public static interface TuSdkCameraListener {
        
        /**
         * 滤镜更改事件，每次调用 switchFilter 切换滤镜后即触发该事件，运行在主线程
         *
         * @param filter 新的滤镜对象
         * @since V3.2.0
         */
        void onFilterChanged(FilterWrap filter);
        /**
         * 相机状态改变 (如需操作UI线程， 请检查当前线程是否为主线程)
         *
         * @param camera
         *            相机对象
         * @param newState
         *            相机运行状态
         */
        void onVideoCameraStateChanged(TuSdkStillCameraAdapter.CameraState newState);
        
        /**
         * 获取截屏图片
         *
         * @param camera
         *            相机对象
         * @param bitmap
         *            图片
         */
        void onVideoCameraScreenShot(Bitmap bitmap);
        }
        
    

### 1.9 RegionHandler 使用说明

使用自定义 `RegionHandler` 可以实现把相机预览视图（非全屏时）上下左右移动指定距离的功能。 可以新建子类继承 `RegionDefaultHandler`，然后子类中重写下面三个方法：

    void setWrapSize(TuSdkSize size)
    RectF recalculate(float ratio, TuSdkSize size)
    RectF changeWithRatio(float ratio, RegionChangerListener listener)
    

然后使用 `mVideoCamera.setRegionHandler(RegionHandler)` 方法将自己的子类设置进去，最后使用 `mVideoCamera.changeRegionRatio(float)` 方法刷新视图，使 `regionHandler` 生效。 以 1:1 视图为例：

     // 刷新视图，使 regionHandler 生效, 1:1 视图
      mVideoCamera.changeRegionRatio(1.0f);
    

在 Demo 中是以 1:1 视图显示的，如果要修改成全屏显示，需要修改两个地方：

*   删除自定义的 `RegionHandler`
    
*   将 `mVideoCamera.changeRegionRatio(1.0f)` 中的参数修改为 0
    

### 1.10 特效改变监听

*   特效数据改变监听

可以根据返回TuSdkMediaEffectData数据类型分类处理。

      /**
        * 特效数据改变
        *
        * @since V3.2.0
        **/
      public static interface TuSdkMediaEffectChangeListener {
        /**
         * 一个新的特效将要被应用
         *
         * @param mediaEffectData 将要应用的特效数据
         * @since V3.2.0
         */
        void didApplyingMediaEffect(TuSdkMediaEffectData mediaEffectData);
    
        /**
         * 特效将要被移除的特效
         *
         * @param mediaEffects
         * @since V3.2.0
         */
        void didRemoveMediaEffect(List<TuSdkMediaEffectData> mediaEffects);
      }
    

### 1.11 人脸检测委托

可以返回人脸检测到的`FaceDetectionResultType`和人脸个数。

    /**
     * 人脸检测结果委托
     *
     * @since V3.2.0
     */
    public enum FaceDetectionResultType {
        /**
         * Succeed
         */
        FaceDetected,
        /**
         * No face is detected
         */
        NoFaceDetected,
    }
    

    /**
      * 人脸检测委托
      *
      * @since V3.2.0
      */
    public interface TuSdkFaceDetectionCallback {
        /**
         * 人脸检测结果
         *
         * @param resultType 人脸检测结果类型
         * @param faceCount  检测到的人脸数量
         * @since V3.2.0
         */
        void onFaceDetectionResult(FaceDetectionResultType resultType, int faceCount);
    }
    

2\. 滤镜使用 (普通滤镜和漫画滤镜)
--------------------

### 2.1 获取滤镜 filterCode

可以在打包下载的资源文件中找到 `lsq_tusdk_configs.json` 文件，之前在控制台所打包的滤镜资源会在这个文件中显示，比如`"name":"lsq_filter_VideoFair"`，则该滤镜的 `filterCode` 即为 `VideoFair`，需注意大小写。 可以将需要用到的 `filterCode` 放到一个滤镜数组中，切换滤镜时从该数组中取出 `filterCode` 即可，如下：

    // 要支持多款滤镜，直接添加到数组即可
     private String[] videoFilters = new String[]{"VideoFair"};
    

### 2.2 加载并显示滤镜资源

Demo中滤镜列表使用`FilterRecyclerAdapter`和`RecyclerView`实现

    this.mFilterAdapter.setFilterList(Arrays.asList(Constants.VIDEOFILTERS));
    

详细实现可以参考短视频 Demo 中的示例。  
同时用户也可以使用自己的列表来显示滤镜，并可以自定义滤镜的缩略图（替换掉对应名称的图片即可）。

### 2.3 切换滤镜效果

*   通过滤镜 Code 使用普通滤镜
    
        mCamera.addMediaEffectData(new TuSdkMediaFilterEffectData(code));
        
    
*   通过滤镜 Code 使用动漫滤镜
    
        mCamera.addMediaEffectData(new TuSdkMediaComicEffectData(code));
        
    

### 2.2 滤镜参数调节

通过 `TuSdkMediaEffectChangeListener` 返回的`TuSdkMediaEffectData`进行滤镜参数调节，滤镜调节包含效果`mixied`等，我们提供了专门的参数调节View可进行快捷设置。

        mFilterConfigView.setFilterArgs(mediaEffectData,filterArg);
    

    // 获取滤镜返回的调节参数
    List<SelesParameters.FilterArg> filterArgs = mediaEffectData.getFilterArgs()
    // 设置最大值限制范围 默认1.0，smoothing、mixied建议0.7，whitening建议 0.6
    filterArg.setMaxValueFactor(factor);
    // 设置对应的百分比值
    filterArg.setPrecentValue(precentValue);
    // 提交修改后的参数
    mediaEffectData.submitFilterParameter();
    

3\. 美颜,微整形的使用
-------------

### 3.1 微整形

设置微整形特效

    // 添加一个默认微整形特效
    TuSdkMediaPlasticFaceEffect plasticFaceEffect = new TuSdkMediaPlasticFaceEffect();
    mCamera.addMediaEffectData(plasticFaceEffect);
    // 以下为改变默认值进行提交参数
    for (SelesParameters.FilterArg arg : plasticFaceEffect.getFilterArgs()) {
        if (arg.equalsKey("eyeSize")) {// 大眼
            arg.setMaxValueFactor(0.85f);// 最大值限制
        }
        if (arg.equalsKey("chinSize")) {// 瘦脸
            arg.setMaxValueFactor(0.8f);// 最大值限制
        }
        if (arg.equalsKey("noseSize")) {// 瘦鼻
            arg.setMaxValueFactor(0.6f);// 最大值限制
        }
    
    }
    for (String key : mDefaultBeautyPercentParams.keySet()) {
        submitPlasticFaceParamter(key,mDefaultBeautyPercentParams.get(key));
    }
    

详细实现可以参考短视频 Demo 中的示例。

### 3.2 美肤

设置美肤特效 true 自然(精准)美颜 false 极致美颜

    TuSdkMediaSkinFaceEffect skinFaceEffect = new TuSdkMediaSkinFaceEffect(true);
    
    // 美白
    SelesParameters.FilterArg whiteningArgs = skinFaceEffect.getFilterArg("whitening");
    whiteningArgs.setMaxValueFactor(0.6f);//设置最大值限制
    // 磨皮
    SelesParameters.FilterArg smoothingArgs = skinFaceEffect.getFilterArg("smoothing");
    smoothingArgs.setMaxValueFactor(0.7f);//设置最大值限制
    
    whiteningArgs.setPrecentValue(0.3f);//设置默认显示
    
    smoothingArgs.setPrecentValue(0.6f);//设置默认显示
    mCamera.addMediaEffectData(skinFaceEffect);
    

详细实现可以参考短视频 Demo 中的示例。

4\. 道具模块(动态贴纸和哈哈镜)
------------------

Demo中贴纸使用`ViewPager`和`Fragment`进行分类页面展示。 同时用户也可以使用自己的列表来显示贴纸，详细实现可以参考短视频 Demo。

    /**
     * 设置道具适配器
     */
    public void init(final FragmentManager fm){
    
        // 添加贴纸道具分类数据
        mPropsItemCategories.addAll(PropsItemStickerCategory.allCategories());
    
        // 添加哈哈镜道具分类
        mPropsItemCategories.addAll(PropsItemMonsterCategory.allCategories());
    
        mPropsItemPagerAdapter = new PropsItemPagerAdapter(fm, new PropsItemPagerAdapter.DataSource() {
            @Override
            public Fragment frament(int pageIndex) {
    
                PropsItemCategory category = mPropsItemCategories.get(pageIndex);
    
                switch (category.getMediaEffectType()) {
                    case TuSdKMediaEffectDataTypeSticker: {
                        StickerPropsItemPageFragment fragment = new StickerPropsItemPageFragment(pageIndex, mPropsItemCategories.get(pageIndex).getItems());
                        fragment.setItemDelegate(mStickerPropsItemDelegate);
                        return fragment;
                    }
                    default: {
                        PropsItemMonsterPageFragment fragment = new PropsItemMonsterPageFragment(pageIndex, mPropsItemCategories.get(pageIndex).getItems());
                        fragment.setItemDelegate(mPropsItemDelegate);
                        return fragment;
                    }
                }
    
            }
    
            @Override
            public int pageCount() {
                return mPropsItemCategories.size();
            }
        });
    
        mPropsItemViewPager.setAdapter(mPropsItemPagerAdapter);
    
        mPropsItemTabPagerIndicator.setViewPager(mPropsItemViewPager,0);
        mPropsItemTabPagerIndicator.setDefaultVisibleCounts(mPropsItemCategories.size());
    
    
    
        List<String> itemTitles  = new ArrayList<>();
        for (PropsItemCategory category : mPropsItemCategories)
            itemTitles.add(category.getName());
    
    
        mPropsItemTabPagerIndicator.setTabItems(itemTitles);
    }
    

### 4.1 动态贴纸

贴纸道具主要实现部分`StickerPropsItemPageFragment`,请参考Demo实现。

*   贴纸应用和取消

    // 应用贴纸
    TuSdkMediaStickerEffectData mediaStickerEffectData = new TuSdkMediaStickerEffectData(itemData);
    mVideoCamera.addMediaEffectData(mediaStickerEffectData);
    // 移除贴纸
    mVideoCamera.removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdKMediaEffectDataTypeSticker);
    

*   贴纸下载

请参考Demo中`StickerRecyclerAdapter`类  
通过`TuSDKOnlineStickerDownloader`下载器进行贴纸下载控制

    // 初始下载器
    mStickerDownloader = new TuSDKOnlineStickerDownloader();
    // 设置下载回到
    mStickerDownloader.setDelegate(this);
    

    // 下载贴纸
    public final void downloadStickerGroup(StickerGroup stickerGroup)
    

    /**
     * 下载事件委托
     *
     * @author gh.li
     *
     */
    public static interface TuSDKOnlineStickerDownloaderDelegate
    {
        /**
         * 下载进度改变
         *
         * @param stickerGroupId
         *            贴纸分组
         * @param progress
         *            当前进度
         * @param status
         *            状态
         */
        public void onDownloadProgressChanged(long stickerGroupId, float progress, DownloadTaskStatus status);
    }
    

*   动态贴纸配置分类

在资源文件夹raw下配置 `customstickercategories.json`,格式如下

    {
        "categories": [
        {
            "categoryName": "搞怪cos",
            "stickers": [
            {
                "name": "晕",
                "id": "1622",
                "previewImage": "/stickerGroup/img?id=1622"
            },
            {
                "name": "京剧花旦",
                "id": "1591",
                "previewImage": "/stickerGroup/img?id=1591"
            }
        }]
    }
    

动态贴纸资源需是控制台在线资源与打包资源里的动态贴纸，贴纸详情可在点击贴纸图片或者点击查看按钮查询  
`categoryName` 组名 `name` 可使用官网预设名称，也可自定义  
`id` 使用官网贴纸资源id  
`previewImage` id=官网贴纸资源id

*   获取动态贴纸配置分类文件

贴纸根据集成文档进行上线、打包等操作。

打包过的动态贴纸资源可在贴纸列表里直接使用，在线贴纸如果没有下载过则在列表里显示下载图标，下载至本地后才能使用。

        /**
         * 获取所有贴纸分类
         *
         * @return List<PropsItemStickerCategory>
         */
        public static List<PropsItemStickerCategory> allCategories() {
    
            List<PropsItemStickerCategory> categories = new ArrayList<>();
    
            try {
                InputStream stream = TuSdkContext.context().getResources().openRawResource(R.raw.customstickercategories);
    
                if (stream == null) return null;
    
                byte buffer[] = new byte[stream.available()];
                stream.read(buffer);
                String json = new String(buffer, "UTF-8");
    
                JSONObject jsonObject = JsonHelper.json(json);
                JSONArray jsonArray = jsonObject.getJSONArray("categories");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
    
                    // 该分类下的所有贴纸道具
                    List<PropsItemSticker> propsItems = new ArrayList<PropsItemSticker>();
    
                    JSONArray jsonArrayGroup = item.getJSONArray("stickers");
    
                    for (int j = 0; j < jsonArrayGroup.length(); j++) {
    
                        JSONObject itemGroup = jsonArrayGroup.getJSONObject(j);
                        StickerGroup group = new StickerGroup();
                        group.groupId = itemGroup.optLong("id");
                        group.previewName = itemGroup.optString("previewImage");
                        group.name = itemGroup.optString("name");
    
                        PropsItemSticker propsItem = new PropsItemSticker(group);
                        propsItems.add(propsItem);
                    }
    
                    // 该贴纸道具分类
                    PropsItemStickerCategory category = new PropsItemStickerCategory(propsItems);
                    category.setName(item.getString("categoryName"));
    
                    categories.add(category);
    
                }
    
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            return categories;
    
        }
    

详细实现可以参考短视频 Demo 中的示例。

### 4.2 哈哈镜

哈哈镜道具主要实现部分`PropsItemMonsterPageFragment`,请参考Demo实现。

*   哈哈镜应用和取消

    // 应用哈哈镜
    TuSDKMediaMonsterFaceEffect monsterFaceEffect = new TuSDKMediaMonsterFaceEffect(monsterFaceType);
    mVideoCamera.addMediaEffectData(monsterFaceEffect);
    // 移除哈哈镜
    mVideoCamera.removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeMonsterFace);
    

*   哈哈镜分类

        /**
         * 获取所有哈哈镜分类
         *
         * @return List<PropsItemMonsterCategory>
         */
        public static List<PropsItemMonsterCategory> allCategories() {
    
            TuSDKMonsterFaceWrap.TuSDKMonsterFaceType[] faceTypes =
                    {
                            TuSDKMonsterFaceTypeBigNose, // 大鼻子
                            TuSDKMonsterFaceTypePapayaFace, // 木瓜脸
                            TuSDKMonsterFaceTypePieFace, // 大饼脸
                            TuSDKMonsterFaceTypeSmallEyes, // 眯眯眼
                            TuSDKMonsterFaceTypeSnakeFace, // 蛇精脸
                            TuSDKMonsterFaceTypeSquareFace, // 国字脸
                            TuSDKMonsterFaceTypeThickLips // 厚嘴唇
                    };
    
    
            // 缩略图后缀
            String[] faceTypeTitles =
                    {
                            "bignose",
                            "papaya",
                            "pie",
                            "smalleyes",
                            "snake",
                            "square",
                            "thicklips"
                    };
    
    
    
            List<PropsItemMonsterCategory> categories = new ArrayList<>();
            List<PropsItemMonster> monsters = new ArrayList<>();
    
            for (int i = 0; i<faceTypes.length; i++) {
    
                PropsItemMonster monster = new PropsItemMonster(faceTypes[i]);
                monster.setThumbName(faceTypeTitles[i]);
                monsters.add(monster);
            }
    
            PropsItemMonsterCategory monsterCategory = new PropsItemMonsterCategory(monsters);
            monsterCategory.setName(TuSdkContext.getString(R.string.lsq_face_monster));
            categories.add(monsterCategory);
    
            return categories;
        }
    

5\. 变声
------

*   变调参数设置须在点击录制按钮前调用，支持断点录制下不同声调的变换

    public void setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType soundPitchType)
    

*   支持的声调：正常，怪兽，大叔，女生，萝莉

    public enum TuSdkSoundPitchType {
            //正常
            Normal,
            //怪兽
            Monster,
            //大叔
            Uncle,
            //女生
            Girl,
            //萝莉
            Lolita;
    }
    

6\. 快慢速控制
---------

设置正常、快速、极快、慢速、极慢多种模式。

*   设置快速模式

    public void setSpeedMode(SpeedMode speedMode)
    

    /** 速率模式 */
    public enum SpeedMode
    {
       NORMAL,  // 正常
       FAST1,  // 快速
       FAST2, // 极快
       Slow1,  // 慢速
       Slow2;    // 极慢
    
       private float mSpeedRate;
    
       SpeedMode(float speedRate)
       {
          this.mSpeedRate = speedRate;
       }
    
    }
    
## 录制相机的使用

1.相册导入视频
--------

类名

功能说明

MovieAlbumActivity

对视频列表进行选择

其中需要说明这几个参数

    /* 最小视频时长(单位：ms) */
    private static int MIN_VIDEO_DURATION = 3000;
    /* 最大视频时长(单位：ms) */
    private static int MAX_VIDEO_DURATION = 60000;
    /** 最大边长限制 **/
    private static final int MAX_SIZE = 3840;
    

最大边长限制，我们限制在了4K以下，因为不同的厂商的手机对4K的支持不同，我们统一限制视频为4K以下的视频可以编辑

2.视频裁剪
------

类名

功能说明

MovieEditorCutActivity

编辑-裁剪页面

TuSdkMediaMutableFilePlayer

视频播放器(支持多文件)

TuSdkMediaFilesCuterImpl

裁剪器(支持多文件)

TuSdkVideoImageExtractor

封面抽取(支持多文件)

*   传入视频路径列表

进入`MovieEditorCutActivity`需要在打开这个`activity`的时候通过`Bundle` 传入视频路径，`SerializableExtr`的`name`为`videoPaths`

*   获取视频裁剪栏缩略图，用于裁剪栏的展示。

        /** 加载视频缩略图 */
        public void loadVideoThumbList() {
    
            List<TuSdkMediaDataSource> sourceList = new ArrayList<>();
    
            for (MovieInfo movieInfo : mVideoPaths)
                sourceList.add(TuSdkMediaDataSource.create(movieInfo.getPath()).get(0));
    
            /** 准备视频缩略图抽取器 */
            final TuSdkVideoImageExtractor imageThumbExtractor = new TuSdkVideoImageExtractor(sourceList);
            imageThumbExtractor
                   //.setOutputImageSize(TuSdkSize.create(50,50)) // 设置抽取的缩略图大小
                    .setExtractFrameCount(20) // 设置抽取的图片数量
                    .setImageListener(new TuSdkVideoImageExtractorListener() {
    
                        /**
                         * 输出一帧略图信息
                         *
                         * @param videoImage 视频图片
                         * @since v3.2.1
                         */
                        public void onOutputFrameImage(final TuSdkVideoImageExtractor.VideoImage videoImage) {
                            ThreadHelper.post(new Runnable() {
                                @Override
                                public void run() {
                                    mEditorCutView.addBitmap(videoImage.bitmap);
                                    if(!isSetDuration) {
                                        float duration = mVideoPlayer.durationUs() / 1000000.0f;
                                        mEditorCutView.setRangTime(duration);
                                        mEditorCutView.setTotalTime(mVideoPlayer.durationUs());
                                        if(duration >0)
                                        isSetDuration = true;
                                    }
                                    mEditorCutView.setMinCutTimeUs(mMinCutTimeUs/(float)mDurationTimeUs);
                                }
                            });
                        }
    
                        /**
                         * 抽取器抽取完成
                         *
                         * @since v3.2.1
                         */
                        @Override
                        public void onImageExtractorCompleted(List<TuSdkVideoImageExtractor.VideoImage> videoImagesList) {
                            /** 注意： videoImagesList 需要开发者自己释放 bitmap */
                            imageThumbExtractor.release();
    
                        }
                     })
                    .extractImages(); // 抽取图片
    
        }
    

*   裁剪控制器会对视频本身进行裁剪处理，导出视频并保存到临时文件夹中，用户可自定义处理。

        /**
         * 开始合成视频
         */
        private void startCompound(){
            if (cuter != null) {
                return;
            }
    
            isCutting = true;
    
            List<TuSdkMediaDataSource> sourceList = new ArrayList<>();
    
            // 遍历视频源
            for (MovieInfo movieInfo : mVideoPaths) {
                sourceList.add(TuSdkMediaDataSource.create(movieInfo.getPath()).get(0));
            }
            // 准备切片时间
            TuSdkMediaTimeSlice tuSdkMediaTimeSlice = new TuSdkMediaTimeSlice(mLeftTimeRangUs,mRightTimeRangUs);
            tuSdkMediaTimeSlice.speed = mVideoPlayer.speed();
    
            // 准备裁剪对象
            cuter = new TuSdkMediaFilesCuterImpl();
            // 设置裁剪切片时间
            cuter.setTimeSlice(tuSdkMediaTimeSlice);
            // 设置数据源
            cuter.setMediaDataSources(sourceList);
            // 设置文件输出路径
            cuter.setOutputFilePath(getOutputTempFilePath().getPath());
    
            // 准备视频格式
            MediaFormat videoFormat = TuSdkMediaFormat.buildSafeVideoEncodecFormat( cuter.preferredOutputSize().width,  cuter.preferredOutputSize().height,
                    30, TuSdkVideoQuality.RECORD_MEDIUM2.getBitrate(), MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface, 0, 0);
    
    
            // 设置视频输出格式
            cuter.setOutputVideoFormat(videoFormat);
            // 设置音频输出格式
            cuter.setOutputAudioFormat(TuSdkMediaFormat.buildSafeAudioEncodecFormat());
    
            // 开始裁剪
            cuter.run(new TuSdkMediaProgress() {
                /**
                 *  裁剪进度回调
                 * @param progress        进度百分比 0-1
                 * @param mediaDataSource 当前处理的视频媒体源
                 * @param index           当前处理的视频索引
                 * @param total           总共需要处理的文件数
                 */
                @Override
                public void onProgress(final float progress, TuSdkMediaDataSource mediaDataSource, int index, int total) {
                    ThreadHelper.post(new Runnable() {
                        @Override
                        public void run() {
                            mLoadContent.setVisibility(View.VISIBLE);
                            mLoadProgress.setValue(progress * 100);
                        }
                    });
                }
    
                /**
                 *  裁剪结束回调
                 * @param e 如果成功则为Null
                 * @param outputFile 输出文件路径
                 * @param total 处理文件总数
                 */
                @Override
                public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total) {
                    isCutting = false;
                    ThreadHelper.post(new Runnable() {
                        @Override
                        public void run() {
                            setEnable(true);
                            mLoadContent.setVisibility(View.GONE);
                            mLoadProgress.setValue(0);
                            mPlayBtn.setVisibility(mVideoPlayer.isPause()?View.VISIBLE:View.GONE);
                        }
                    });
                    Intent intent = new Intent(MovieEditorCutActivity.this,MovieEditorActivity.class);
                    intent.putExtra("videoPath", outputFile.getPath());
                    startActivity(intent);
                    cuter = null;
                }
            });
        }
    

*   视频裁剪可以对输出视频设置时间范围、画布裁剪、图像裁剪等，示例如下：

    // 设置裁剪切片
    public void setTimeSlice(TuSdkMediaTimeSlice slice)
    // 设置数据源
    public final void setMediaDataSources(List<TuSdkMediaDataSource> mediaDataSources) 
    // 设置输出文件路径
    public void setOutputFilePath(String filePath)
    // 设置输出视频格式
    public int setOutputVideoFormat(MediaFormat videoFormat)
    // 设置输出音频格式
    public int setOutputAudioFormat(MediaFormat audioFormat)
    // 进行裁剪
    public boolean run(TuSdkMediaProgress progress)
    

裁剪的回调为

    /** 媒体处理进度接口 */
    public interface TuSdkMediaProgress {
        /**
         * 执行进度 [主线程]
         *
         * @param progress        进度百分比 0-1
         * @param mediaDataSource 当前处理的视频媒体源
         * @param index           当前处理的视频索引
         * @param total           总共需要处理的文件数
         */
        void onProgress(float progress, TuSdkMediaDataSource mediaDataSource, int index, int total);
    
        /***
         * 完成转码 [主线程]
         * @param e 如果成功则为Null
         * @param outputFile 输出文件路径
         * @param total 处理文件总数
         */
        void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total);
    }
    

3.视频编辑
------

由`MovieEditorCutActivity` 转码后，进入了视频编辑`MovieEditorActivity`页面,

类名

功能描述

MovieEditorActivity

视频编辑页面

MovieEditorController

视频编辑控制器

TuSdkMovieEditor

视频编辑器

视频编辑的功能是由`TuSdkMovieEditor` 提供,Demo界面以及相关操作，都在`MovieEditorController`里以操作组件的方式封装

*   视频编辑控制器的结构与组成

类名

功能描述

EditorHomeComponent

视频编辑主页面组件

EditorFilterComponent

滤镜效果组件

EditorMVComponent

MV效果组件

EditorMusicComponent

配音效果组件

EditorTextComponent

文字效果组件

EditorEffectComponent

特效组件（包括场景特效、时间特效、魔法特效）

### 3.1 视频编辑器的初始化

在`MovieEditorController` 的构造方法中需要初始化视频编辑器,以下是一个最简单的视频加载逻辑。

        /**
        * context 当前context
        * holderView 视频播放器的父容器
        * options 视频配置项
        **/
        TuSdkMovieEditor mMovieEditor = new TuSdkMovieEditorImpl(context, holderView, options);
        //之前经历过MovieEditorCutActivity裁剪加载 则不用开启转码
        mMovieEditor.setEnableTranscode(false);
        //加载视频
        mMovieEditor.loadVideo();
    

接口描述：

*   设置转码

        /**
         * 是否开启转码
         *
         * @param isEnableTranscode true 开启 false 不开启 默认开启
         */
        public void setEnableTranscode(boolean isEnableTranscode);
    

*   加载视频

        /**
         * 加载视频
         *
         * @since 3.0
         */
        void loadVideo();
    

*   保存视频

        /**
         * 保存视频
         *
         * @since 3.0
         */
        void saveVideo();
    

#### 3.1.1视频编辑配置项中可配置的参数：

简单使用方式如下：

        TuSdkMovieEditor.TuSdkMovieEditorOptions defaultOptions = TuSdkMovieEditor.TuSdkMovieEditorOptions.defaultOptions();
        defaultOptions
            // 设置视频数据源
            .setVideoDataSource(new TuSdkMediaDataSource(mVideoPath))
            // 设置是否保存或者播放原音
            .setIncludeAudioInVideo(true) 
            // 设置MovieEditor销毁时是否自动清除缓存音频解码信息
            .setClearAudioDecodeCacheInfoOnDestory(false)
            // 设置时间线模式
            .setPictureEffectReferTimelineType(TuSdkMediaEffectReferInputTimelineType)
            // 设置水印
            .setWaterImage(BitmapHelper.getBitmapFormRaw(this, R.raw.sample_watermark), TuSdkWaterMarkOption.WaterMarkPosition.TopRight, true);
    

接口描述：

*   设置视频数据源

    public TuSdkMovieEditorOptions setVideoDataSource(TuSdkMediaDataSource videoDataSource);
    

*   设置影片保存路径

     public TuSdkMovieEditorOptions setMovieOutputFilePath(File movieOutputFilePath);
    

*   设置视频裁剪区域

    public TuSdkMovieEditorOptions setCutTimeRange(TuSDKTimeRange cutTimeRange);
    

*   设置画布裁剪区域

    public TuSdkMovieEditorOptions setCanvasRectF(RectF canvasRect);
    

*   设置是否保存视频原音 默认 true

    public TuSdkMovieEditorOptions setIncludeAudioInVideo(boolean includeAudioInVideo);
    

*   设置视频画面特效参照时间线类型

    public TuSdkMovieEditorOptions setPictureEffectReferTimelineType(TuSdkMediaPictureEffectReferTimelineType timelineType)；
    

*   设置视频输出的宽高

    public TuSdkMovieEditorOptions setOutputSize(TuSdkSize outputSize);
    

*   设置是否将视频保存到相册 默认：true

    public TuSdkMovieEditorOptions setSaveToAlbum(Boolean saveToAlbum);
    

*   保存到系统相册的相册名称 (saveToAlbum 为true时可用)

    public TuSdkMovieEditorOptions setSaveToAlbumName(String saveToAlbumName);
    

*   MovieEditor销毁时是否自动清除音频缓存信息（默认：false 设置为false下次再次使用时可加快载入速度）

    public TuSdkMovieEditorOptions setClearAudioDecodeCacheInfoOnDestory(boolean clearAudioDecodeCacheInfoOnDestory);
    

*   设置水印图片

    /**
    * waterImage          水印图片 (Bitmap)
    * watermarkPosition   水印的位置
    * isRecycleWaterImage 是否回收水印图片(Bitmap)
    **/
    public TuSdkMovieEditorOptions setWaterImage(Bitmap waterImage, TuSdkWaterMarkOption.WaterMarkPosition watermarkPosition, boolean isRecycleWaterImage)
    

#### 3.1.2 视频编辑的API组成与特效数据类

视频编辑`TuSdkMovieEditor` 中由一下几个组件组成，调用的时候通过一下不同的功能组件调用不同的API

1.  `TuSdkEditorTranscoder`转码器 如果没有预转码或者开启了转码，则由此转码器进行视频的裁剪与处理
2.  `TuSdkEditorPlayer` 播放器 编辑内的播放器，负责控制时间特效，以及相关播放的API
3.  `TuSdkEditorEffector` 特效器 负责特效 添加 删除相关的API
4.  `TuSdkEditorAudioMixer`混音器 混音相关的API
5.  `TuSdkEditorSaver` 保存器 最后保存视频的相关API

相关特效分为**普通特效**和**时间特效**两种 普通特效：

特效类名

功能描述

TuSDKMediaTextEffectData

文字贴纸特效

TuSDKMediaParticleEffectData

魔法特效

TuSDKMediaStickerAudioEffectData

MV特效

TuSDKMediaFilterEffectData

滤镜效果

TuSDKMediaStickerEffectData

贴纸特效

TuSDKMediaAudioEffectData

配音特效

TuSDKMediaSceneEffectData

场景特效

TuSDKMediaComicEffectData

卡通特效

上述特效由`TuSdkEditorEffector` 特效器进行_添加_ 、_删除_的操作，下列三种是时间特效，由`TuSdkEditorPlayer`管理

特效类名

功能描述

TuSDKMediaReversalTimeEffect

倒序时间特效

TuSDKMediaRepeatTimeEffect

反复时间特效

TuSDKMediaSlowTimeEffect

慢动作时间特效

### 3.2 视频编辑特效的使用

#### 3.2.1 视频编辑滤镜使用

在`EditorFilterComponent`中`mFilterRecyclerView`的`Item`点击回调内，回去到当前点击的滤镜的`code`(通过`mFilterRecyclerAdapter.setFilterList(filterList)`设置)

*   滤镜列表，获取滤镜前往 TuSDK.bundle/others/lsq\_tusdk\_configs.json
    
*   TuSDK 滤镜信息介绍 @see-https://tusdk.com/docs/ios/self-customize-filter
    

    TuSDKMediaFilterEffectData mediaFilterEffectData = new TuSDKMediaFilterEffectData(code);
    getMovieEditor().getEditorEffector().addMediaEffectData(filterEffectData);
    

滤镜的改变回调可以通过`TuSdkEditorEffector` 来设置

    //设置滤镜改变的回调
    getEditorEffector().setFilterChangeListener(mFilterChangeListener);
    

#### 3.2.2 视频编辑MV使用

在`EditorMVComponent`中的`mMvRecyclerView`的`Item`点击回调内

    /*********************  添加  *************/
     if (mMusicMap != null && mMusicMap.containsKey(groupId)) {
        //带音效的MV
        Uri uri = Uri.parse("android.resource://" + getEditorController().getActivity().getPackageName() + "/" + mMusicMap.get(groupId));
       //创建MV数据类
        TuSDKMediaStickerAudioEffectData stickerAudioEffectDat = new TuSDKMediaStickerAudioEffectData(new TuSdkMediaDataSource(context, uri), itemData);
         //设置时间范围
        stickerAudioEffectDat.setAtTimeRange(TuSDKTimeRange.makeRange(0, Float.MAX_VALUE));
        //设置MV的背景音效是否循环播放
        stickerAudioEffectDat.getMediaAudioEffectData().getAudioEntry().setLooping(true);
        //添加MV效果
        getEditorEffector().addMediaEffectData(stickerAudioEffectDat);
    
    } else {
        //纯贴纸的MV
        TuSDKMediaStickerEffectData stickerEffectData = new TuSDKMediaStickerEffectData(itemData);
        //设置时间范围
        stickerEffectData.setAtTimeRange(TuSDKTimeRange.makeRange(0, Float.MAX_VALUE));
        //添加MV效果
        getMovieEditor().getEditorEffector().addMediaEffectData(stickerEffectData);
     }
    

#### 3.2.3 视频编辑配乐使用

在`EditorMusicComponent`中的`mMusicRecycle`的`Item`点击中

    //创建音频特效对象
    TuSDKMediaAudioEffectData audioEffectData = new TuSDKMediaAudioEffectData(new TuSdkMediaDataSource(context, audioPathUri));
    //设置时间
    audioEffectData.setAtTimeRange(TuSDKTimeRange.makeTimeUsRange(0,   getEditorPlayer().getOutputTotalTimeUS()));
    

加载音频回调是在混音器中加入

    getEditorMixer().addTaskStateListener(mAudioDecoderTask);
    

回调的状态有

    /**
     * 当前执行类状态
     */
    public enum State {
        /** 空闲状态 **/
        Idle,
        /** 正在解码 **/
        Decoding,
        /** 解码完成 **/
        Complete,
        /** 已取消  **/
        Cancelled
    }
    

音频录音回调

    /**
     * 录音裁剪进度监听
     */
    public interface OnAudioRecordCuterListener {
        /**
         * 当前执行的进度
         *
         * @param percent       当前进度的百分比 （0 ~ 1）
         * @param currentTimeUS 当前执行的时间（微秒）
         * @param totalTimeUS   总时长 （微秒）
         */
        void onProgressChanged(float percent, long currentTimeUS, long totalTimeUS);
    
        /**
         * 输出完毕
         *
         * @param outputFile 输出完成的文件
         */
        void onComplete(File outputFile);
    
    }
    

#### 3.2.4 视频编辑文字使用

文字功能在`EditorTextComponent` 中，添加一个文字特效

    /**
     * 将数据转成公用的 TuSDKMediaEffectData
     *
     * @param sticker     贴纸数据
     * @param bitmap      文字生成的图片
     * @param offsetX     相对视频左上角X轴的位置
     * @param offsetY     相对视频左上角Y轴的位置
     * @param rotation    旋转的角度
     * @param startTimeUs 文字特效开始的时间
     * @param stopTimeUs  文字特效结束的时间
     * @param stickerSize 当前StickerView的宽高（计算比例用）
    */
    //创建一个文字贴纸包装类
    TuSDKTextStickerImage stickerImage = new TuSDKTextStickerImage();
    //创建文字贴纸数据类
    TextStickerData stickerData = new TextStickerData(bitmap, bitmap.getWidth(), bitmap.getHeight(), 0, offsetX, offsetY, rotation);
    stickerImage.setCurrentSticker(stickerData);
    //设置设计画布的宽高
    stickerImage.setDesignScreenSize(stickerSize);
    //创建文字特效类
    TuSDKMediaTextEffectData mediaTextEffectData = new TuSDKMediaTextEffectData(stickerImage);
    //设置当前文字特效的时间
    mediaTextEffectData.setAtTimeRange(TuSDKTimeRange.makeTimeUsRange(startTimeUs, stopTimeUs
    //添加特效
    getEditorEffector().addMediaEffectData(textMediaEffectData)
    

#### 3.2.5 视频编辑特效使用

特效里分为**场景特效** 、 **时间特效**、**魔法特效** 三种，在`EditorEffectComponent`中，分为三个`Fragment`

1.  `ScreenEffectFragment`场景特效Fragment
2.  `TimeEffectFragment` 时间特效Fragment
3.  `MagicEffectFragment` 魔法特效Fragment

#### 3.2.5.1 场景特效

添加一个场景特效

    //创建一个场景特效数据类
    TuSDKMediaSceneEffectData mediaSceneEffectData = new TuSDKMediaSceneEffectData(mScreenCode);
    //设置场景特效的时间
    mediaSceneEffectData.setAtTimeRange(TuSDKTimeRange.makeTimeUsRange(starTimeUs, endTimeUs));
    //添加当前场景特效
    getEditorEffector().addMediaEffectData(mediaSceneEffectData);
    

删除一个场景特效

    getEditorEffector().removeMediaEffectData(mediaEffectData);
    

#### 3.2.5.2 时间特效

*   反复特效

    //实例化反复特效数据类
    TuSDKMediaRepeatTimeEffect repeatTimeEffect = new TuSDKMediaRepeatTimeEffect();
    //设置开始与结束的时间范围
    repeatTimeEffect.setTimeRange(startTimeUS, endTimeUS);
    //设置反复的次数
    repeatTimeEffect.setRepeatCount(2);
    //是否裁剪多余的时间 
    repeatTimeEffect.setDropOverTime(false);
    //应用时间特效
    getEditorPlayer().setTimeEffect(repeatTimeEffect);
    

*   慢动作

    //实例化慢动作特效数据
    TuSDKMediaSlowTimeEffect slowTimeEffect = new TuSDKMediaSlowTimeEffect();
    //设置慢动作的时间范围
    slowTimeEffect.setTimeRange(startTimeUS, endTimeUS);
    //设置慢动作的速率
    slowTimeEffect.setSpeed(0.6f);
    //应用时间特效
    getEditorPlayer().setTimeEffect(slowTimeEffect);
    

*   时光倒流

    //实例化时光倒流特效数据
    TuSDKMediaReversalTimeEffect reversalTimeEffect = new TuSDKMediaReversalTimeEffect();
    //应用时间特效
    getEditorPlayer().setTimeEffect(reversalTimeEffect);
    

*   清除时间特效

    getEditorPlayer().clearTimeEffect();
    

#### 3.2.5.2 魔法特效

*   魔法特效的添加

    //实例化魔法效果
    TuSDKMediaParticleEffectData effectModel = new TuSDKMediaParticleEffectData(mCurrentMagicCode);
    //设置粒子大小
    effectModel.setSize(mMagicConfig.getSize());
    //设置粒子颜色
    effectModel.setColor(mMagicConfig.getColor());   
    //设置粒子位置(持续的移动不断的put 参考Demo中的MagicEffectFragment)
    effectModel.putPoint(getEditorPlayer().getCurrentTimeUs(), pointF);
    //预览魔法特效
    getEditorEffector().addMediaEffectData(effectModel);
    
    
    

### 3.3 保存视频

保存视频调用`TuSdkMovieEditor.saveVideo()`,保存的回调可以在`saveVieo()` 之前向保存器内添加回调

    getEditorSaver().addSaverProgressListener(mSaveProgressListener);
    

该回调为

    //保存进度监听
    interface TuSdkSaverProgressListener {
        /**
         * 当前进度
         *
         * @param progress
         * @since v3.0
         */
        void onProgress(float progress);
    
        /**
         * 保存完成
         *
         * @param outputFile
         * @since v3.0
         */
        void onCompleted(TuSdkMediaDataSource outputFile);
    
        /**
         * 保存错误
         *
         * @param e
         * @since v3.0
         */
        void onError(Exception e);
    }
    
## API 使用示例

1\. 多音轨混合
---------

类名

功能说明

TuSDKAACAudioFileEncoder

AAC音频文件编码器

TuSDKAverageAudioMixer

音频混合器

(参考Demo中`AudioMixedActivity`)

*   初始化音频混合对象
    
        TuSDKAverageAudioMixer audioMixer = new TuSDKAverageAudioMixer();
        //设置混音回调
        audioMixer.setOnAudioMixDelegate(mAudioMixerDelegate);
        
    
    这里需要设置一个混音的回调`mAudioMixerDelegate`
    
        /**
        * 音频混合Delegate
        */
        private TuSDKAudioMixer.OnAudioMixerDelegate mAudioMixerDelegate = new TuSDKAudioMixer.OnAudioMixerDelegate() {
          /**
           * 混合状态改变事件
           */
          @Override
          public void onStateChanged(TuSDKAudioMixer.State state) {
              if (state == TuSDKAudioMixer.State.Complete) {
                  // 停止AAC编码器
                  mAACFileEncoder.stop();
                  TuSdk.messageHub().showSuccess(AudioMixedActivity.this, "混合完成");
              } else if (state == Decoding || state == Mixing) {
                  TuSdk.messageHub().setStatus(AudioMixedActivity.this, "混合中");
              } else if (state == TuSDKAudioMixer.State.Cancelled) {
                  //删除混合的音频文件
                  delMixedFile();
              }
          }
        
          /**
           * 当前解析到主背景音乐信息时回调该方法，其他音乐将参考该信息进行混合
           */
          @Override
          public void onReayTrunkTrackInfo(TuSDKAudioInfo rawInfo) {
          }
        
          @Override
          public void onMixingError(int errorCode) {
              TuSdk.messageHub().showError(AudioMixedActivity.this, "混合失败");
          }
        
          /**
           * 混合后的音频数据（未经编码）
           */
          @Override
          public void onMixed(byte[] mixedBytes) {
              // 编码音频数据
              mAACFileEncoder.queueAudio(mixedBytes);
          }
        };
        
    
    混音的状态是在`TuSDKAudioMixer.State`这个枚举中，有以下几种状态
    
        /**
         * 混音状态
         */
        public enum State
        {
            /** 空闲状态 */
            Idle,
            /** 正在解码 */
            Decoding,
            /** 解码完成 */
            Decoded,
            /** 混合中 */
            Mixing,
            /** 混合完成 */
            Complete,
            /** 已取消 */
            Cancelled
        }
        
    
    我们看到在回调中用到了`TuSDKAACAudioFileEncoder`AAC文件编码器 ,AAC文件编码器的初始化方式为
    
*   初始化AAC音频文件编码器
    

    /** AAC 音频文件编码器，可将混合的音频数据编码为AAC文件 */
    TuSDKAACAudioFileEncoder mAACFileEncoder = new TuSDKAACAudioFileEncoder();
    // 初始化音频编码器
    mAACFileEncoder.initEncoder(TuSDKAudioEncoderSetting.defaultEncoderSetting());
    // 设置输入路径
    mAACFileEncoder.setOutputFilePath(getMixedAudioPath());
    

在初始化调用`initEncoder`的时候,我们会传入一个`TuSDKAudioEncoderSetting`对象，这个是音频编码器的设置，主要用来设置以下的参数

    /** TuSDKAudioEncoderSetting **/
    
    /** 录制的音频数据格式 默认：AudioFormat.ENCODING_PCM_16BIT */
    public int audioFormat;
    /** 录制的音频采样率 默认:44100 */
    public int sampleRate;
    /** 录制的音频通道设置 AudioFormat.CHANNEL_IN_MONO 或 AudioFormat.CHANNEL_IN_STEREO  默认：AudioFormat.CHANNEL_IN_STEREO */
    public int channelConfig;
    /** 音频质量 默认:AudioQuality.MEDIUM1 */
    public AudioQuality audioQuality;
    /** 默认：MediaCodecInfo.CodecProfileLevel.AACObjectLC */
    public int mediacodecAACProfile;
    /** 音道数 默认：2 */
    public int mediacodecAACChannelCount;
    /** 是否启用音频缓冲区 */
    public boolean enableBuffers;
    

如果不需要自定义参数的话，直接调用`TuSDKAudioEncoderSetting.defaultEncoderSetting()`获取默认配置即可

*   多音轨开始混合
    
        /** 启动AAC文件编码器 **/
        mAACFileEncoder.start();
        /** 向音频混合器输入需要混合的音频对象列表 **/
        mAudioMixer.mixAudios(getAudioEntryList());
        
    
    `getAudioEntryList()`为获取音频对象`TuSDKAudioEntry`列表
    
    *   获取音频文件列表
        
        音频对象为`TuSDKAudioEntry.java` 支持`URI`和`Path`两种形式，其中可支持的操作有
        
    
        /**
         * 设置改音频是否为主背景
         * @param trunk 其他音频混合时将参考该音频 
         */
        public TuSDKAudioEntry setTrunk(boolean trunk);
        
        /**
        * 设置是否循环
        * @param looping true : 在背景音乐中循环
        */
        public TuSDKAudioEntry setLooping(boolean looping);
        
        /**
        * 该音频在主干音频的位置  （mTrunk == true时 忽略该设置）
        * @param timeRange 时间区间
        */
        public TuSDKAudioEntry setTimeRange(TuSDKTimeRange timeRange);
        
        /**
        * 设置裁剪区域
        * @param cutTimeRange 裁剪时间区间
        */
        public TuSDKAudioEntry setCutTimeRange(TuSDKTimeRange cutTimeRange)
        
        /**
         * 设置音量
         * @param volume （0f - 1f）
         */
        public TuSDKAudioEntry setVolume(float volume);
        
    

2\. 音视频混合
---------

类名

功能说明

TuSDKMP4MovieMixer

对视频和多个音频进行混合

（参考Demo中的`TuSDKMP4MovieMixer`）

*   初始化混合器
    
    混合器采用了建造者模式去构建,构建方式如下
    
        TuSDKMP4MovieMixer mMP4MovieMixer = new TuSDKMP4MovieMixer();
        mMP4MovieMixer.setDelegate(this)
              .setOutputFilePath(getMixedVideoPath()) // 设置输出路径
              .setVideoSoundVolume(1.0f) // 设置音乐音量
              .setClearAudioDecodeCacheInfoOnCompleted(true); // 设置音视频混合完成后是否清除缓存信息 默认：true （false:再次混合时可加快混合速度）
        
    

​ 这边会设置一个回调 `OnMP4MovieMixerDelegate` 这个回调内部有这几个方法

    /**
     * 音视频混合Delegate
     */
    public interface OnMP4MovieMixerDelegate
    {
       /**
        * 混合状态改变
        * @param state
        * @see TuSDKMP4MovieMixer.State
        */
       void onStateChanged(TuSDKMP4MovieMixer.State state);
    
       /**
        * 错误状态
        * @param code
        * @see ErrorCode
        */
       void onErrrCode(ErrorCode code);
    
       /**
        * 混合完成
        * @param result 视频混合结果
        */
       void onMixerComplete(TuSDKVideoResult result);
    }
    

*   开始混合
    
        //  mVideoDataSource : 视频路径 mAudioTracks : 待混合的音频数据 true ： 是否混合视频原音
        mMP4MovieMixer.mix(TuSDKMediaDataSource.create(getVideoPath()), mAudioEntryList, false); 
        
    
    ​ 开始混合的时候需要传入
    
    1.  **视频的路径`TuSDKMediaDataSource`**
    2.  **待混合的音频数据`mAudioEntryList`**（`TuSDKAudioEntry`列表，多音轨混合有详细介绍）
    3.  **是否混合视频原音**

这三个设置

*   状态和错误码
    
*       /**
        *  TuSDKMP4VideoMixer 状态信息
        */
        public enum State
        {
         /** 空闲状态 */
         Idle,
         /** 正在解码 */
         Decoding,
         /** 解码完成 */
         Decoded,
         /** 混合中 */
         Mixing,
         /** 已取消 */
         Cancelled,
         /** 混合视频 */
         Failed
        }
        
        /**
        * 错误码
        */
        public enum ErrorCode
        {
         /** 不支持的视频格式 */
         UnsupportedVideoFormat,
        }
        
    

3\. 获取视频缩略视图
------------

类名

功能说明

TuSDKVideoImageExtractor

视频帧提取器

(参考Demo中`MovieThumbActivity`)

*   视频帧提取器用法
    
        //获取出的每帧图片大小
        TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56), TuSdkContext.dip2px(30));
        //创建视频帧提取器
        TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();
        //设置输出图片大小
        extractor.setOutputImageSize(tuSdkSize)
            //设置视频数据源
              .setVideoDataSource(TuSDKMediaDataSource.create(getVideoPath()))
            //设置分离图片的数量
              .setExtractFrameCount(15);
        //设置分离回调，开始提取图片
        extractor.asyncExtractImageList(mImageExtractorDelegate);
        
    

​ 这里会设置分离的回调，回调的方法为

    public  interface TuSDKVideoImageExtractorDelegate
    {   
       /** 此方法是在所有图片分离完毕之后回调 只会回调一次 **/
       void onVideoImageListDidLoaded(List<Bitmap> images);
       /** 此方法是在每分离出一张图片就会回调一次  **/
       void onVideoNewImageLoaded(Bitmap bitmap);
    }
    

*   精确提取视频帧

在`TuSDKVideoImageExtractor`中调用此方法可以精确提取视频帧

    /**
    * 获取指定时间的视频缩略图 单位：微秒
    * 
    * @param frameTimeUs 侦时间 单位：微妙
    * @param quality 质量 0-100
    * @return Bitmap
    */
    public Bitmap getFrameAtTime(long frameTimeUs, int quality) 
    

4.多视频拼接
-------

类名

功能说明

TuSdkMediaSuit.merge()

多视频拼接

*   多视频拼接使用方法

    TuSdkMediaSuit.merge(mMoviePathList, muxerPath, ouputVideoFormat, ouputAudioFormat, mediaProgress);
    

1.  `mMoviePathList` 待合成视频列表
2.  `muxerPath` 最终生成的文件路径
3.  `ouputVideoFormat` 输出的视频格式
4.  `ouputAudioFormat`输出的音频格式
5.  `mediaProgress`进度回调

进度的回调有以下几个方法

    /** 媒体处理进度接口 */
    public interface TuSdkMediaProgress {
        /**
         * 执行进度 [主线程]
         *
         * @param progress        进度百分比 0-1
         * @param mediaDataSource 当前处理的视频媒体源
         * @param index           当前处理的视频索引
         * @param total           总共需要处理的文件数
         */
        void onProgress(float progress, TuSdkMediaDataSource mediaDataSource, int index, int total);
    
        /***
         * 完成转码 [主线程]
         * @param e 如果成功则为Null
         * @param outputFile 输出文件路径
         * @param total 处理文件总数
         */
        void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total);
    }
    

5.视频时间范围裁剪
----------

类名

功能说明

TuSdkMediaSuit.cuter()

视频裁剪

（参考Demo中的 `MovieCutActivity`）

*   视频裁剪使用方法

    TuSdkMediaSuit.cuter(inputMediaSource, outputFilePath, ouputVideoFormat, ouputAudioFormat, orientation,rectDrawF, rectCutF, timeSlice, mCuterMediaProgress);
    

1.  `inputMediaSource` 输入的视频源
2.  `outputFilePath` 输出的地址
3.  `ouputVideoFormat` 输出的视频格式信息
4.  `ouputAudioFormat` 输入的视频格式信息
5.  `orientation` 设置输出视频方向 详见`ImageOrientation`
6.  `timeSlice` 设置时间的范围

6.音频录制
------

类名

功能说明

TuSDKAudioFileRecorder

音频录制器

（参考Demo中的`AudioRecordActivity`）

*   初始化音频录制器
    
        TuSDKAudioFileRecorder mAudioRecorder = new TuSDKAudioFileRecorder();
        //设置输出文件类型
        mAudioRecorder.setOutputFormat(TuSDKAudioFileRecorder.OutputFormat.AAC);
        //设置文件录制回调
        mAudioRecorder.setAudioRecordDelegate(mRecordAudioDelegate);
        
    

​ 文件输出类型可以设置为`PCM`和`AAC`两种文件类型

回调接口有以下几个方法

    /** 录音事件回调 */
    public static interface TuSDKRecordAudioDelegate
    {
       /**
        * 录制完成
        * 
        * @param file
        */
       public void onAudioRecordComplete(File file);
    
    
       /**
        * 录制状态改变
        * 
        * @param state @see RecordState
        */
       void onAudioRecordStateChanged(RecordState state);
    
       /**
        * 错误信息回调
        * 
        * @param error @see RecordError
        */
       void onAudioRecordError(RecordError error);
    
    }
    

开始、暂停、继续、结束的方法分别是

    //开始录制
    mAudioRecorder.start();
    //暂停录制
    mAudioRecorder.pauseRecord();
    //继续录制
    mAudioRecorder.resumeRecord();
    //结束录制
    mAudioRecorder.stop();
    

7\. 音频变声
--------

类名

功能名称

TuSdkAudioPitchEngine

音频变声

### 1.音频特效的类型

目前音频特效类型有以下几种:

     //正常
    TuSdkSoundPitchType.Normal
    //怪兽
    TuSdkSoundPitchType.Monster
    //大叔
    TuSdkSoundPitchType.Uncle
    //女生
    TuSdkSoundPitchType.Girl
    //萝莉
    TuSdkSoundPitchType.Lolita
    

### 2.音频特效 API

*   将 PCM 数据放入`TuSdkAudioPitchEngine` 队列中

     /***
      * 处理音频数据
      * @param byteBuffer 输入缓存
      * @param bufferInfo 缓存信息
      * @return 是否已处理
     */
     void processInputBuffer(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo);
    
    

*   通知`TuSdkAudioPitchEngine`输入的 PCM 音频信息变更

    /**
     * 更新音频信息
     * @param inputAudioInfo 新的音频信息
     */
    void changeAudioInfo(TuSdkAudioInfo inputAudioInfo);
    

*   清除缓存并重置音频处理器

    /**
    * 重置处理器 
    */
    void reset();
    

*   释放当前音频处理器

    /**
    * 释放处理器
    */
    void release();
    

### 3\. 使用示例

    // setp1: 初始化输入的音频信息
    TuSdkAudioInfo  inputAudioInfo = new TuSdkAudioInfo(TuSdkMediaFormat.buildSafeAudioEncodecFormat());
    
    // setp2: 初始化 TuSdkAudioPitchEngine
    TuSdkAudioPitchEngine audioPitchEngine = new TuSdkAudioPitchEngine(inputAudioInfo);
    
    // setp3: 设置 TuSdkAudioPitchEngine 处理回调
    audioPitchEngine.setOutputBufferDelegate(mAudioPitchEngineOutputBufferDelegate);
    
    // setp4: 设置当前需要的音效
    audioPitchEngine.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Normal);
    
    // setp5: 在输出PCM数据的地方调用
    audioPitchEngine.processInputBuffer(outputByteBuffer, bufferInfo);
    
    // setp6: 经过TuSdkAudioPitchEngine处理后的数据在会在回调中返回
    /** 音频处理数据输出委托 **/
    interface TuSdKAudioEngineOutputBufferDelegate {
        void onProcess(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo);
    }
    
    // 在这里添加后续处理逻辑,可用于编码,播放,或写入文件。
 
    
    
<h2 id="2">又拍云短视频上传</h2>

### 使用说明

1.导入上传依赖：compile 'com.upyun:upyun-android-sdk:2.1.0'

### 示例代码
* 表单上传

```
        //空间名
        String SPACE = "formtest";
        //操作员
        String OPERATER = "one";
        //密码
        String PASSWORD = "***";

        //上传路径
        String savePath = "/uploads/{year}{mon}{day}/{random32}{.suffix}";

        final Map<String, Object> paramsMap = new HashMap<>();
        //上传空间
        paramsMap.put(Params.BUCKET, SPACE);
        //保存路径
        paramsMap.put(Params.SAVE_KEY, savePath);
        //添加 CONTENT_LENGTH 参数使用大文件表单上传
        paramsMap.put(Params.CONTENT_LENGTH, file.length());

        //可选参数（详情见api文档介绍）
        paramsMap.put(Params.CONTENT_MD5, UpYunUtils.md5Hex(file));
        paramsMap.put(Params.RETURN_URL, "httpbin.org/post");

        //上传结果回调
        UpCompleteListener completeListener = new UpCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, String result) {
                Log.e(TAG, isSuccess + ":" + result);
            }
        };

        //进度条回调
        UpProgressListener progressListener = new UpProgressListener() {
            @Override
            public void onRequestProgress(final long bytesWrite, final long contentLength) {
                Log.e(TAG, (100 * bytesWrite) / contentLength + "%");
            }
        };

        UploadEngine.getInstance().formUpload(file, paramsMap, OPERATER, UpYunUtils.md5(PASSWORD), completeListener, progressListener);
```

* 断点续传

```
        //空间名
        String SPACE = "formtest";
        //操作员
        String OPERATER = "one";
        //密码
        String PASSWORD = "***";

        //上传路径
        String path = "/test.mp4";

        //初始化断点续传
        ResumeUploader uploader = new ResumeUploader(SPACE, OPERATER, UpYunUtils.md5(PASSWORD));

        //设置 MD5 校验
        uploader.setCheckMD5(true);

        //设置进度监听
        uploader.setOnProgressListener(new UpProgressListener() {
            @Override
            public void onRequestProgress(long bytesWrite, long contentLength) {
                Log.e(TAG, bytesWrite + ":" + contentLength);
            }
        });

        uploader.upload(file, path, null, new UpCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, String result) {
                Log.e(TAG, "isSuccess:" + isSuccess + "  result:" + result);
            }
        });
```




<h2 id="3">又拍云播放器</h2>

#### 功能说明 

* 支持在线视频协议：`HLS`, `RTMP`, `HTTP-FLV` 等

* 支持本地视频播放

* 支持设置窗口大小和全屏设置

* 支持缓冲大小设置

* 提供 UpVideoView 控件

* 支持 ARM, ARMv7a, ARM64v8a, X86 主流芯片体系架构

### 1.配置环境

#### 1.1 基本介绍

`UPYUN` 播放器 `SDK`。功能完备接口简练，可以快速安装使用, 灵活性强可以满足复杂定制需求。

### 2.SDK使用说明

#### 2.1 运行环境和兼容性

Android 2.3 (API 9) 及其以上     

### 2.2 安装使用说明

#### 工程使用：    
导入 upplayer module 使用：compile project(':upplayer')

### 3.播放器使用
#### 3.1 播放器简单调用

直接使用控件 UpVideoView 

```java

        //设置播放地址
        upVideoView.setVideoPath(path);

        //开始播放
        upVideoView.start();
        
        //暂停播放
        upVideoView.pause();
        
        //停止释放播放器
        upVideoView.release(true);
        
        //设置默认缓存区大小 (需在setVideoPath 或者 resume 前执行生效)
        upVideoView.setBufferSize(int size);

		//拖拽
		upVideoView.seekTo(int msec)
```
