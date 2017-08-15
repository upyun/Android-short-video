# 又拍云短视频 

1. 提供短视频的拍摄、编辑、合成、上传等基础功能。

2. 提供播放器支持。


## 目录
1 [短视频](#1)

2 [上传](#2)

3 [播放器](#3)

<h2 id="1">短视频</h2>

### 1.配置环境

#### 1.1 基本介绍

* 短视频拍摄、编辑、合成部分，包含断点录制、分段回删、美颜、滤镜、贴纸、视频剪辑、视频压缩、本地转码在内的 30 多种功能，支持自定义界面和二次开发。


#### 1.2 运行环境

* Eclipse 或 Android Studio 1.3 + ，Android 系统 4.3 +

#### 1.3 密钥 和 资源

* 短视频 SDK 提供一个免费版和两个收费版，任何版本使用都需要授权（key），收费版可以免费试用一个月。

* 获取授权，请提供应用名称、申请使用的 SDK 版本、使用的平台（安卓、iOS）、应用 bundleID、包名给您的商务经理，或者[联系我们](https://www.upyun.com/contact)。

#### 1.4 环境配置

1、[联系我们](https://www.upyun.com/contact) 获取资源文件，导入至 lib 以及 assert 目录。

#### 1.5 TuSDK 的初始化

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
    
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.FLASHLIGHT" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />



然后定义应用的全局 `Application` 对象，设置 `allowBackup`、`hardwareAccelerated` 和 `largeHeap` 三个重要选项为 `true`。


	<application
	    android:name="org.lasque.tusdk.TuApplication"
	    android:allowBackup="true"
	    android:hardwareAccelerated="true"
	    android:largeHeap="true"
	</application>
  


## 2. 配置并开启录制相机

### 2.1 创建录制相机对象

创建并配置 `TuSDKRecordVideoCamera` 对象，代码如下：

	RelativeLayout cameraView = (RelativeLayout) findViewById(R.id.lsq_cameraView);

	// 录制相机采集配置，目前只支持硬编录制
	TuSDKVideoCaptureSetting captureSetting = new TuSDKVideoCaptureSetting();
	captureSetting.fps = 20;
	captureSetting.videoAVCodecType = AVCodecType.HW_CODEC;
	
	TuSDKRecordVideoCamera videoCamera = new TuSDKRecordVideoCamera(getBaseContext(), captureSetting, cameraView);


构造方法中需要传入下面三个参数：

+ 当前上下文（`Context`）
+ 相机采集配置（`TuSDKVideoCaptureSetting`）
+ 相机视图容器（`RelativeLayout`）

`TuSDKVideoCaptureSetting` 中可以对相机采集时的多项属性进行设置，包括：

+ `facing`，相机朝向（默认: `CameraFacing.Front` 前置）
+ `videoSize`，输出画面尺寸，仅当 `videoAVCodecType == CUSTOM_CODEC` 时生效（默认：`TuSdkSize(320, 480)`）
+ `fps`，采集帧率（默认：`24`）
+ `videoAVCodecType`，视频编码类型，当前录制视频仅支持硬件编码 (默认: `AVCodecType.HW_CODEC` (硬编))

相机视图容器是一个 `RelativeLayout` 布局，一般在 XML 布局文件中指定，传入 `TuSDKRecordVideoCamera` 构造方法中后，会将相机预览视图加载在该视图容器中。



### 2.2 配置相机属性

获取录制相机对象后，可以对该相机的属性进行一些设置。

#### 2.2.1 开启动态贴纸
使用 `setEnableLiveSticker(boolean)` 方法开启或关闭动态贴纸，默认为 `false`。


#### 2.2.2 设置水印
使用 `setWaterMarkImage(Bitmap)` 方法设置水印图片，图片最大边长不宜超过 500；

使用 `setWaterMarkPosition(WaterMarkPosition)` 方法设置水印的位置，默认 `WaterMarkPosition.BottomRight`。


#### 2.2.3 设置视频录制结果委托

使用 `setVideoDelegate(TuSDKRecordVideoCameraDelegate)` 指定录制相机的事件委托，该委托中有下面四个接口用来通知相机状态改变：

+ onMovieRecordComplete(TuSDKVideoResult result);

	视频录制完成时回调该接口，`result` 参数中包含录制视频的信息
+ onMovieRecordProgressChanged(float progress, float durationTime);

	录制进度改变时回调该接口，两个参数分别代表当前相对于最大录制时长的录制进度(0-1) 以及当前录制持续时间（单位：/s）
+ onMovieRecordStateChanged(RecordState state);

	录制状态改变时回调该接口，返回的参数中记录了当前录制的状态
+ onMovieRecordFailed(RecordError error);

	录制出错时调用该接口，返回的参数中记录了错误信息


#### 2.2.4 设置最小、最大录制时长

使用 `setMinRecordingTime(int)` 和 `setMaxRecordingTime(int)` 设置最小、最大录制时长（单位：/s）

#### 2.2.5 指定录制模式

使用 `setRecordMode(RecordMode)` 设置录制模式，录制模式分为`正常模式 (RecordMode.Normal)` 和 `续拍模式 (RecordMode.Keep)`


#### 2.2.6 设置相机事件委托

使用 `setDelegate(TuSDKVideoCameraDelegate)` 设置相机事件委托，可以实现使用拖动条控制美颜效果变化的功能，详细可以参看 demo 中的使用示例


#### 2.2.7 设置视频编码配置

使用 `setVideoEncoderSetting(TuSDKVideoEncoderSetting)` 可以设置视频编码时的参数。

`TuSDKVideoEncoderSetting` 中可以对编码器进行多项设置，包括：

+ `videoSize`，输出视频尺寸（默认：`TuSdkSize(320, 480)`）
+ `videoQuality`，视频质量
+ `mediacodecAVCIFrameInterval`，I帧时间间隔 （默认：2）

推荐使用 SDK 默认的编码配置，如下：

    // 推荐编码配置
    TuSDKVideoEncoderSetting encoderSetting = TuSDKVideoEncoderSetting.getDefaultRecordSetting();
    
    mVideoCamera.setVideoEncoderSetting(encoderSetting);


#### 2.2.8 禁用前置摄像头自动水平镜像 (默认: false) 

使用 `setDisableMirrorFrontFacing(boolean)` 方法设置是否禁用前置摄像头自动水平镜像，默认开启


#### 2.2.9 保存系统相册

使用 `setSaveToAlbum(boolean)` 方法设置是否将录制的视频保存到相册，默认保存到相册，当设置为 false 时, 保存为临时文件；

使用 `setSaveToAlbumName(String)` 方法设置保存到系统相册的名称

### 2.3 相机操作接口

SDK 提供了多个操作相机的方法，供用户操作相机开启、关闭、恢复、暂停，如下：

* startCameraCapture()

* stopCameraCapture()

* resumeCameraCapture()

* pauseCameraCapture()

同时，这些方法需要配合所在 `Activity` 的生命周期中使用，如下：

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


### 2.4 RegionHandler 使用说明

使用自定义 `RegionHandler` 可以实现把相机预览视图（非全屏时）上下左右移动指定距离的功能。

可以新建子类继承 `RegionDefaultHandler`，然后子类中重写下面三个方法：

	void setWrapSize(TuSdkSize size)
	RectF recalculate(float ratio, TuSdkSize size)
	RectF changeWithRatio(float ratio, RegionChangerListener listener)

然后使用 `mVideoCamera.setRegionHandler(RegionHandler)` 方法将自己的子类设置进去，最后使用 `mVideoCamera.changeRegionRatio(float)` 方法刷新视图，使 `regionHandler` 生效。

以 1:1 视图为例：

		// 刷新视图，使 regionHandler 生效, 1:1 视图
		mVideoCamera.changeRegionRatio(1.0f);

在 demo 中是以 1:1 视图显示的，如果要修改成全屏显示，需要修改两个地方：

+ 删除自定义的 `RegionHandler`
+ 将 `mVideoCamera.changeRegionRatio(1.0f)` 中的参数修改为 0

### 2.5 控制录制动作

录制相机对象可以通过下面三个接口控制开启、暂停和停止录制动作：

+ `startRecording()`
+ `pauseRecording()`
+ `stopRecording()`

其中可以使用 `isRecording()` 获取当前相机是否处于正在录制的状态。



### 2.6 滤镜使用


#### 2.6.1 获取滤镜 filterCode

可以在打包下载的资源文件中找到 `lsq_tusdk_configs.json` 文件，之前在控制台所打包的滤镜资源会在这个文件中显示，比如`"name":"lsq_filter_VideoFair"`，则该滤镜的 filterCode 即为 `VideoFair`，需注意大小写。

可以将需要用到的 `filterCode` 放到一个滤镜数组中，切换滤镜时从该数组中取出 `filterCode` 即可，如下：

	 // 要支持多款滤镜，直接添加到数组即可
	 private String[] videoFilters = new String[]{"VideoFair"};


关于滤镜更多介绍，可以参看[「自定义滤镜」](https://tusdk.com/docs/android/customize-filter)文档。


#### 2.6.2 加载并显示滤镜资源

创建了滤镜代号数组中，每一个字符串都代表一个滤镜的 `filterCode`，将数组通过 `mFilterListView.setModeList(List<String>)` 的方式传给 `FilterListView` 类，然后在 `FilterCellView` 类中的 `bindModel()` 方法中通过 `filterCode` 获取滤镜对应的缩略图，并将该缩略图显示在该项滤镜视图上面。

详细实现可以参考短视频 demo 中的示例。

同时用户也可以使用自己的列表来显示滤镜，并可以自定义滤镜的缩略图（替换掉对应名称的图片即可）。

#### 2.6.3 切换滤镜效果

使用 `mVideoCamera.switchFilter(filterCode)` 方法即可在录制相机中切换滤镜效果。



### 2.7 动态贴纸使用

#### 2.7.1 加载并显示贴纸资源

动态贴纸资源可以通过 `StickerLocalPackage.shared().getSmartStickerGroups()` 方法获取，并返回一个 `List<StickerGroup>` 类型的返回值，里面包含着所有打包下载到本地的动态贴纸资源。

跟加载滤镜资源不一样，加载贴纸资源时使用的是从打包下载的资源文件中读取的贴纸缩略图，然后通过 `mStickerListView.setModeList(groups)` 方法将贴纸资源传给 `StickerListView` 类中，最后在 `StickerCellView` 类中的 `bindModel()` 方法中使用 `StickerLocalPackage.shared().loadGroupThumb(StickerGroup data, ImageView posterView)` 接口将贴纸缩略图绑定到对应视图上。


详细实现可以参考短视频 demo 中的示例。

同时用户也可以使用自己的列表来显示贴纸，参考 demo 中的实现。

#### 2.7.2 添加贴纸效果

在 demo 中，点击贴纸栏会调用 `onStickerGroupSelected()` 方法，在该方法中调用 `mVideoCamera.showGroupSticker(StickerGroup)` 方法显示传入的贴纸，使用 `mVideoCamera.removeAllLiveSticker()` 方法取消动态贴纸效果。


## 3. 视频编辑


### 3.1 系统相册中选取待编辑视频片段

首先通过使用系统相册选取或是直接传入的方式获取到待编辑的视频地址，打开系统相册代码如下：

	Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
	pickIntent.setType("video/*");
	startActivityForResult(pickIntent, IMAGE_PICKER_SELECT);

选中视频后可以在 `onActivityResult(int requestCode, int resultCode, Intent data)` 方法中使用 `data.getData()` 获取视频地址。

### 3.2 获取视频缩略图

获取视频地址之后，需要把地址传入到 SDK 中。

使用 `TuSDKVideoImageExtractor` 类把指定的视频中的帧数据按照设置的尺寸和数量提取出来，创建该类对象，代码如下：

	TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();
	
	// 设置输出的图片尺寸
	extractor.setOutputImageSize(tuSdkSize);
 	// 设置视频地址
	extractor.setVideoDataSource(TuSDKMediaDataSource.create(mVideoPath));
	// 设置提取的帧数量
	extractor.setExtractFrameCount(6);

	// 异步加载视频缩略图
	extractor.asyncExtractImageList(new TuSDKVideoImageExtractorDelegate() 
	{
		@Override   
		public void onVideoImageListDidLoaded(List<Bitmap> images) {
			// 加载视频缩略图完成回调		
		}
		
		@Override
		public void onVideoNewImageLoaded(Bitmap bitmap){
			// 加载一张图片完成回调	
		}
	});	

提取视频帧数据完成后会回调两个回调函数，其中 `onVideoNewImageLoaded(Bitmap bitmap)` 方法会在每加载一张图片完成后回调并返回图片对象，而 `onVideoImageListDidLoaded(List<Bitmap> images)` 方法会在全部图片加载完成后回调，并返回图片对象列表。


### 3.3 选取视频区域

获取缩略图列表之后，可以使用进度条控件 `MovieRangeSelectionBar` 类来展示缩略图，通过 `setList(List<Bitmap>)` 将缩略图与展示视图绑定。

同时通过拖动该进度条控件上的光标，可以选择想要编辑的视频的开始和结束的位置，并把这两个位置传入下一步的编辑器中。

其中`MovieRangeSelectionBar`类可以在 demo 下可以看到完整源码，可以参考 demo 中的具体使用方法。


### 3.4 创建视频编辑器对象

上一步中使用进度条控件选取好待编辑的视频片段之后，可以使用 `TuSDKMovieEditor` 类为该段视频添加滤镜、贴纸等效果。

创建并配置 `TuSDKMovieEditor` 对象，如下：

	// 视频预览视图
	RelativeLayout cameraView =  (RelativeLayout) findViewById(R.id.lsq_cameraView);
	
	TuSDKMovieEditorOptions defaultOptions = TuSDKMovieEditorOptions.defaultOptions();
	
	defaultOptions.setMoviePath(mVideoPath)
				  .setCutTimeRange(mCutTimeRange)
				  // 是否需要按原视频比例显示
				  .setOutputRegion(new RectF(movieLeft,movieTop, movieRight, movieBottom))
				  // 是否开启美颜
				  .setEnableBeauty(true)
				  // 设置混音时是否保存视频原音
				  .setIncludeAudioInVideo(true)
				  // 设置是否循环播放视频
				  .setLoopingPlay(true)
				  // 设置视频加载完成后是否自动播放 
				  .setAutoPlay(true); 
	
	mMovieEditor = new TuSDKMovieEditor(this.getBaseContext(), cameraView, defaultOptions);
	mMovieEditor.loadVideo();


创建 `TuSDKMovieEditor` 对象需要传入下面三个参数：

+ 当前上下文（`Context`）
+ 视频预览视图（`RelativeLayout`）
+ 编辑配置项（`TuSDKMovieEditorOptions`）

其中可以在配置项中配置视频文件路径和起始、结束时间；
而传入一个 `RelativeLayout` 类型的视图后，SDK 会在该视图上添加一个 `GLSurfaceview` 播放视频，以方便在上面预览添加的滤镜效果。

### 3.5 设置视频编辑委托

使用 `mMovieEditor.setDelegate(TuSDKMovieEditorDelegate)` 方法设置视频编辑委托，该委托中有下面三个回调方法会通知编辑状态的改变，如下：

+ onMovieEditProgressChanged(float progress);

	通知视频处理进度。预览时表示播放进度，导出视频时表示导出进度。
+ onMovieEditComplete(TuSDKVideoResult result);

	通知视频处理完成。返回生成的新视频信息，预览时该对象为 null。
+ onMoveEditorStatusChanged(TuSDKMovieEditorStatus status); 

	通知视频编辑器状态改变。
	
	其中视频状态定义如下：

		/**
		 * 状态信息
		 */
		public enum TuSDKMovieEditorStatus
		{
			UnKnow,
			// 加载失败 
			LoadVideoFaield,
			// 加载完成
			Loaded,
			// 正在预览
			Previewing,
			// 预览完成
			PreviewingCompleted,
			// 正在录制
			Recording,
			// 录制失败
			RecordingFailed,
		}


### 3.6 视频编辑器配置项

+ setSaveToAlbum(boolean)

	设置是否保存到相册（默认：true）
+ setSaveToAlbumName(String)

	设置保存到相册的名称（需在 `DCIM` 目录下），默认保存到系统默认相册

+ setVideoSoundVolume(float)

    设置混音时的原音音量

+ setWaterMarkImage(Bitmap)

	设置水印，默认为空

+ setWaterMarkPosition(WaterMarkPosition)

	设置水印位置

+ switchFilter(String filterCode)

	根据 `filterCode` 切换滤镜效果

+ loadVideo()

	加载视频，并显示第一帧

+ startPreview()

	启动预览，不保存视频

+ stopPreview()

	停止预览

+ isPreviewing()

	是否正在预览视频

+ startRecording()

	开始生成视频文件

+ stopRecording()

	停止生成视频文件

+ isRecording()

	是否正在生成视频文件


### 3.7 滤镜使用


#### 3.7.1 获取滤镜代号

可以在打包下载的资源文件中找到 `lsq_tusdk_configs.json` 文件，之前在控制台所打包的滤镜资源会在这个文件中显示，比如`"name":"lsq_filter_VideoFair"`，则该滤镜的 filterCode 即为 `VideoFair`，需注意大小写。

可以将需要用到的 `filterCode` 放到一个滤镜数组中，如下：

	 // 要支持多款滤镜，直接添加到数组即可
	 private String[] videoFilters = new String[]{"VideoFair"};


关于滤镜更多介绍，可以参看[「自定义滤镜」](https://tusdk.com/docs/android/customize-filter)文档。


#### 3.7.2 加载并显示滤镜资源

创建了滤镜代号数组中，每一个字符串都代表一个滤镜的 `filterCode`，将数组通过 `mFilterListView.setModeList(List<String>)` 的方式传给 `FilterListView` 类，然后在 `FilterCellView` 类中的 `bindModel()` 方法中通过 `filterCode` 获取滤镜对应的缩略图，并将该缩略图显示在该项滤镜视图上面。

详细实现可以参考短视频 demo 中的示例。

同时用户也可以使用自己的列表来显示滤镜，并可以自定义滤镜的缩略图（替换掉对应名称的图片即可）。

#### 3.7.3 切换滤镜效果

使用 `mMovieEditor.switchFilter(filterCode)` 方法即可在视频编辑器中切换滤镜效果。


### 3.8 MV 使用

#### 3.8.1 加载并显示 MV 资源

MV 贴纸资源可以通过 `StickerLocalPackage.shared().getSmartStickerGroups(false)` 方法获取，参数代表是否返回基于人脸识别的贴纸组，这里传入 false，代表获取的是不基于人脸识别的贴纸；

该方法会返回一个 `List<StickerGroup>` 类型的返回值，里面包含着所有打包下载到本地的 MV 贴纸资源。

加载 MV 贴纸资源是使用从打包下载的资源文件中读取的贴纸缩略图，然后通过 `mMvListView.setModeList(groups)` 方法将贴纸资源传给 `MVListView` 类中，最后在 `MVCellView` 类中的 `bindModel()` 方法中使用 `StickerLocalPackage.shared().loadGroupThumb(StickerGroup data, ImageView posterView)` 接口将贴纸缩略图绑定到对应视图上。


详细实现可以参考短视频 demo 中的示例。

同时用户也可以使用自己的列表来显示贴纸，参考 demo 中的实现。

#### 3.8.2 添加贴纸效果

在 demo 中，点击贴纸栏会调用 `onMvGroupSelected()` 方法，在该方法中调用 `mMovieEditor.addMediaEffectData(TuSDKMediaEffectData)` 方法显示传入的贴纸，使用 `mMovieEditor.removeAllMediaEffects()` 方法取消动态贴纸效果。


## 4. UI 自定义

短视频 demo 中所有的界面实现都可以在 demo 项目源码中找到，用户可以根据自己需求修改界面。

## 5. 自定义使用


### 5.1 录制相机全屏录制

demo 中断点续拍相机是按照 1:1 的方形画幅录制的，如果要修改成全屏录制，需要修改以下几个地方：

1. 取消设置的 RegionHandler，即取消 `mVideoCamera.setRegionHandler(customeRegionHandler);` 设置；
2. 取消设置的视频预览显示比例，即取消 `mVideoCamera.changeRegionRatio(1.0f);` 设置，该方法默认值为 0，即全屏显示；
3. 设置编码尺寸，如下：

	    // 编码配置
    	TuSDKVideoEncoderSetting encoderSetting = TuSDKVideoEncoderSetting.getDefaultRecordSetting();
    	
		// 设置为 TuSdkSize(0, 0), 即输出采集尺寸
    	encoderSetting.videoSize = TuSdkSize.create(0, 0);
    	
    	mVideoCamera.setVideoEncoderSetting(encoderSetting);


### 5.2 设置录制相机编码时的配置

可以通过 TuSDKVideoEncoderSetting 设置编码时的帧率和码率，如下：
	
	// 获取推荐的编码配置，建议用户直接使用该配置，也可以在此基础上自定义修改
    TuSDKVideoEncoderSetting encoderSetting = TuSDKVideoEncoderSetting.getDefaultRecordSetting();
    
	// VideoQuality 分为多个等级，每一个等级都对应一对我们提供的帧率和码率的值
	encoderSetting.videoQuality = VideoQuality.RECORD_HIGH3;
    
    mVideoCamera.setVideoEncoderSetting(encoderSetting);

同时也可以自定义 VideoQuality 中帧率和码率的值，如下：

    TuSDKVideoEncoderSetting encoderSetting = TuSDKVideoEncoderSetting.getDefaultRecordSetting();
    
	// 自定义帧率和码率值分别为 fps 和 bitrate
	encoderSetting.videoQuality = VideoQuality.RECORD_HIGH3.setFps(fps).setBitrate(bitrate);    
    	
    mVideoCamera.setVideoEncoderSetting(encoderSetting);



### 5.3 录制相机模式切换

录制相机中支持两种录制模式，如下：

	/** 录制模式 */
	public enum RecordMode
	{
		/** 正常模式 */
		Normal,
		
		/** 续拍模式 */
		Keep,
	}

可以在打开相机前通过下面的方法设置：

	// 设置录制模式为断点续拍模式
	mVideoCamera.setRecordMode(RecordMode.Keep);


### 5.5 编辑界面设置视频输出区域

在打开视频编辑器时，可以通过 TuSDKMovieEditorOptions 设置最后视频的输出区域，如下：
	
	TuSDKMovieEditorOptions defaultOptions = TuSDKMovieEditorOptions.defaultOptions();
	// 是否需要按原视频比例显示
	defaultOptions.setOutputRegion(new RectF(movieLeft,movieTop, movieRight, movieBottom));

`setOutputRegion(RectF)` 方法接收一个 `RectF` 类型的参数，代表视频的输出区域，该参数默认为 null，表示按照原视频比例输出。


## 6. API 使用示例

* API 使用示例具体使用，参考「VideoDemo」- 「API」文件夹内组件范例

### 6.1 多音轨混合

支持多个音频混合成单个音频，并可生成 AAC 格式文件。

目前只支持相同采样率、声道数和精度的音频进行混合。

1. 设置待混合音轨路径


	获取待混合音频的文件路径或是 Uri 地址，把该地址封装到 `TuSDKAudioEntry` 类中，后面会把生成的 `TuSDKAudioEntry` 对象列表传到混合器中。

	生成 `TuSDKAudioEntry` 对象方法如下：

		
		/** 待混合的音频数据地址 */
		Uri[] audioUris = new Uri[]{uri1, uri2, uri3};
		List<TuSDKAudioEntry> mAudioEntryList = new ArrayList<TuSDKAudioEntry>(audioUris.length);
		
		for (int i = 0; i < audioUris.length; i++)
		{
			TuSDKAudioEntry audioEntry = new TuSDKAudioEntry(audioUris[i]);
			// 设置第一个音频为主背景
			audioEntry.setTrunk( i == 0 );
			
			mAudioEntryList.add(audioEntry);
		}



2. 初始化音频混合器

	构造音频混合对象时，需要设置音频混合状态回调 `OnAudioMixerDelegate`，如下：

		/**
		 * 音频混合Delegate
		 */
		private OnAudioMixerDelegate mAudioMixerDelegate = new OnAudioMixerDelegate() 
		{
			/**
			 * 混合状态改变事件
			 */
			@Override
			public void onStateChanged(TuSDKAudioMixer.State state) 
			{
				if (state == TuSDKAudioMixer.State.Complete)
				{
					// 停止AAC编码器
					mAACFileEncoder.stop();
					
					TuSdk.messageHub().showSuccess(AudioMixerActivity.this, "混合完成");
					
				}else if(state == TuSDKAudioMixer.State.Decoding || state == TuSDKAudioMixer.State.Mixing)
				{
					TuSdk.messageHub().setStatus(AudioMixerActivity.this, "混合中");		
					
				}else if(state == TuSDKAudioMixer.State.Cancelled)
				{
					// 取消任务，删除文件
					delMixedFile();
				}
			}
			
			/**
			 * 当前解析到主背景音乐信息时回调该方法，其他音乐将参考该信息进行混合
			 */
			@Override
			public void onReayTrunkTrackInfo(TuSDKAudioInfo rawInfo)
			{
			}
			
			@Override
			public void onMixingError(int errorCode) 
			{
				TuSdk.messageHub().showError(AudioMixerActivity.this, "混合失败");
			}
			
			/**
			 * 混合后的音频数据（未经编码）
			 */
			@Override
			public void onMixed(byte[] mixedBytes) 
			{
				// 编码音频数据
				mAACFileEncoder.queueAudio(mixedBytes);
			}
		};

		/** 音频混合对象 */
		TuSDKAverageAudioMixer mAudioMixer = new TuSDKAverageAudioMixer();
		mAudioMixer.setOnAudioMixDelegate(mAudioMixerDelegate);



3. 初始化音频编码器

		/** AAC音频文件编码器 可将混合的音频数据编码为AAC文件 */
		TuSDKAACAudioFileEncoder mAACFileEncoder = new TuSDKAACAudioFileEncoder();

		// 初始化音频编码器
		mAACFileEncoder.initEncoder(TuSDKAudioEncoderSetting.defaultEncoderSetting());
		// 设置生成的音频文件保存路径
		mAACFileEncoder.setOutputFilePath(getMixedAudioPath());


4. 启动多音轨混合

		// 启动音频编码器
		mAACFileEncoder.start();
		// 启动音频混合器
		mAudioMixer.mixAudios(getAudioEntryList());


5. 取消混合

	混合过程中，可以取消混合操作，如下：

		mAudioMixer.cancel();

6. 播放混合后音频




### 6.2 音视频混合

1. 初始化音频文件

		/** 混合的音频数据 */
		private List<TuSDKAudioEntry> mAudioTracks = new ArrayList<TuSDKAudioEntry>();

		// 待混合音频地址
		Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.lsq_audio_children);
		
		// 构建音效数据
		TuSDKAudioEntry audioEntry = new TuSDKAudioEntry(uri);
		
		// 设置待混合音频的音量 (0 - 1)
		audioEntry.setVolume(0.8f)
			// 设置待混合音频是否循环
			.setLooping(true);
			// 设置待混合音频在主背景的位置
			.setTimeRange(TuSDKTimeRange.makeRange(5, 10)); 
		
		mAudioTracks.add(audioEntry);



2. 设置视频地址

		/** 视频地址包装 */
		private TuSDKMediaDataSource mVideoDataSource;
	
		/** 视频 Uri 地址 */
		Uri selectedMediaUri;
	
		// 构造视频地址包装对象
		mVideoDataSource = TuSDKMediaDataSource.create(selectedMediaUri);


3. 配置音视频混合器

	配置音视频混合器时需要实现混合状态通知回调接口 `OnMP4MovieMixerDelegate`，该接口下面定义了混合状态改变的几个回调方法，如下：
	
		/**
		 * 音视频混合Delegate
		 */
		public interface OnMP4MovieMixerDelegate
		{
		    /**
		     * 混合状态改变
		     *
		     * @param state
		     * @see TuSDKMP4VideoMixer.State
		     */
		    void onStateChanged(TuSDKMP4MovieMixer.State state);
		
		    /**
		     * 错误状态
		     *
		     * @param code
		     * 			@see ErrorCode
		     */
		    void onErrrCode(ErrorCode code);
		
		    /**
		     * 混合完成
		     *
		     * @param result
		     *            视频混合结果
		     */
		    void onMixerComplete(TuSDKVideoResult result);
		}


	实现了状态回调接口之后，就可以使用下面的方式配置音视频混合器：	


		TuSDKMP4MovieMixer mMP4MovieMixer = new TuSDKMP4MovieMixer();
		
		mMP4MovieMixer.setDelegate(this)
					  // 设置输出路径
					  .setOutputFilePath(mMixedVideoPath) 
					  // 设置音乐音量
					  .setVideoSoundVolume(1.f) 
					  // 设置音视频混合完成后是否清除缓存信息 默认：true （false:再次混合时可加快混合速度）
					  .setClearAudioDecodeCacheInfoOnCompleted(true);


4. 开始混合

	传入待混合的音频、视频路径，开始混合，如下：

		/**
		 * mVideoDataSource : 视频路径
		 * mAudioTracks : 待混合的音频数据
		 * true ： 是否混合视频原音
		 */
		mMP4MovieMixer.mix(mVideoDataSource, mAudioTracks, true);



### 6.3 获取视频缩略视图

1. 使用 `TuSDKVideoImageExtractor` 类可以使用异步方式获取指定视频中的缩略图，并以 `Bitmap` 的形式返回，同时可以指定获取缩略图的数量和获取缩略图的尺寸，如下：

	
		// 缩略图的尺寸
		TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56),TuSdkContext.dip2px(30));
		
		// 创建对象
		TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();
		
		// 设置获取的缩略图的尺寸
		extractor.setOutputImageSize(tuSdkSize)
				 // 设置目标视频的地址
				 .setVideoDataSource(TuSDKMediaDataSource.create(getVideoPath()))
				 // 设置获取缩略图的数量  
				 .setExtractFrameCount(15);
		
		// 异步获取视频缩略图
		extractor.asyncExtractImageList(new TuSDKVideoImageExtractorDelegate() 
		{
			@Override   
			public void onVideoImageListDidLoaded(List<Bitmap> images) 
			{
				// 加载视频缩略图完成回调
			}
			
			@Override
			public void onVideoNewImageLoaded(Bitmap bitmap)
			{
				// 加载一张图片完成回调
			}
		});	
	
		private Uri getVideoPath() {
			Uri videoPathUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tusdk_sample_video);
			return videoPathUri;
		}



### 6.4 多视频拼接

1. 获取待拼接视频

	
		/** 待拼接视频路径 */
		private ArrayList<String> movieList = new ArrayList<String>();
		String videoPath1, videoPath2;
		
		// 加入路径列表
		movieList.add(videoPath1);
		movieList.add(videoPath2);


2. 生成合成片段实体

		List<TuSDKMovieSegment> movieSegmentList = new ArrayList<TuSDKMovieSegment>();
		
		for (int i = 0; i < this.movieList.size(); i++) 
		{
			String moviePath = this.movieList.get(i);

			TuSDKMovieSegment segment = new TuSDKMovieSegment();
			segment.sourcePath = moviePath;

			// 开始时间，单位毫秒，默认合成完整文件
			segment.startTime = 1000;
			// 开始时间，单位毫秒，默认合成完整文件
			segment.endTime = 3000;
			
			movieSegmentList.add(segment);
		}


3. 配置拼接工具类

		TuSDKMovieSplicerOption option = new TuSDKMovieSplicerOption();
		// 合成结果保存路径
		option.savePath = savePath;
		

4. 开始合成视频
	
		TuSDKMovieSplicer movieDataHelper = new TuSDKMovieSplicer(option);
		movieDataHelper.start(movieSegmentList);



### 6.5 视频时间范围裁剪

使用 `TuSDKMovieClipper` 类可以实现从视频中裁切出指定区域的视频片段的功能。

实现方式是传入除去目标视频片段的其他两个待移除片段到 `TuSDKMovieClipper` 类中，该类会移除这两个不需要视频片段，而返回目标视频片段。

1. 获取需要移除的视频片段

		// 目标视频片段的起始时间点
		float startTime;
		// 目标视频片段的结束时间点
		float endTime;
		// 视频总时长
		int mVideoTotalTime;

		// 添加需要移除的片段到 list 容器中
	    List<TuSDKMovieSegment> segmentList = new ArrayList<TuSDKMovieSegment>();
	    for (int i = 0; i < 2; i++)
	    {
	    	TuSDKMovieSegment segment = new TuSDKMovieSegment();
	    	
			if (i == 0)
	    	{
	    		// 时间单位μs
	    		segment.setStartTime(0);
	    		segment.setEndTime((long) startTime * 1000000);	
	    	}
	    	else if (i == 1)
	    	{
	    		segment.setStartTime((long) endTime * 1000000);
		 	    segment.setEndTime(mVideoTotalTime * 1000);	
			}

	 	    segmentList.add(segment);
		}


2. 设置配置项

		// 配置项
		TuSDKMovieClipperOption clipperOption = new TuSDKMovieClipperOption();
		// 设置目标视频保存的路径
		clipperOption.setSavePath(getOutPutFilePath());
		// 设置源视频路径
		clipperOption.setSrcUri(mVideoPathUri);
		// 设置状态监听
		clipperOption.setListener(mClipperProgressListener);
		// 设置源视频时长 单位μs
		clipperOption.setDuration(mVideoTotalTime*1000);

	其中 `TuSDKMovieClipperListener` 定义如下：
	
	    public static interface TuSDKMovieClipperListener
	    {
	        /** Notify started */
	        public void onStart();
	        
	        /** Notify cancel */
	        public void onCancel();
	        
	        /** Notify finished */
	        public void onDone(String outputFilePath);
	
	        /** Notify error */
	        public void onError(Exception exception);
	    }


3. 开始裁切

		TuSDKMovieClipper mMovieClipper = new TuSDKMovieClipper();

		// 设置配置选项
		mMovieClipper.setOption(clipperOption);
	    // 开始裁剪
 		mMovieClipper.startEdit(segmentList);



### 6.6 音频录制

使用 `TuSDKAudioFileRecorder` 类可以录制音频文件并保存到本地，目前支持写入 `PCM` 和 `AAC` 格式。


1. 设置输出文件格式

		/** 音频文件录制实例 */
		private TuSDKAudioFileRecorder mAudioRecorder = new TuSDKAudioFileRecorder();
		
		mAudioRecorder.setOutputFormat(OutputFormat.AAC);
	
2. 设置录音事件委托

		mAudioRecorder.setAudioRecordDelegate(new TuSDKRecordAudioDelegate()
		{
			@Override
			public void onAudioRecordComplete(File file) {
				// 录制完成事件回调
			}

			@Override
			public void onAudioRecordStateChanged(RecordState state){
				// 录制状态改变回调
			}
			
			@Override
			public void onAudioRecordError(RecordError error){
				// 录制出错回调
			}
		});

3. 开始录制

		mAudioRecorder.start();
	
4. 停止录制

		mAudioRecorder.stop();
<h2 id="2">又拍云短视频上传</h2>

### 使用说明

1.导入上传依赖：compile 'com.upyun:upyun-android-sdk:2.0.4' 

### 示例代码
* 表单上传

```
     //表单上传
     final Map<String, Object> paramsMap = new HashMap<>();
     //上传空间
     paramsMap.put(Params.BUCKET, Config.BUCKET);
     //保存路径，任选其中一个
     paramsMap.put(Params.SAVE_KEY, "/uploads/{year}{mon}{day}/{random32}{.suffix}");

     //上传结果回调
     UpCompleteListener completeListener = new UpCompleteListener() {
        @Override
        public void onComplete(boolean isSuccess, String result) {
             Log.e(TAG, isSuccess + ":" + result);
        }
     ;
     //进度条回调
     UpProgressListener progressListener = new UpProgressListener() {
        @Override
        public void onRequestProgress(final long bytesWrite, final long contentLength) {
		//Log.e(TAG, (100 * bytesWrite) / contentLength + "%");
        };
    UploadEngine.getInstance().formUpload(file, paramsMap, Config.OPERATER, UpYunUtils.md5(Config.PASSWORD), completeListener, progressListener);

```

* 断点续传

```
    //断点续传
    final ResumeUploader uploader = new ResumeUploader(Config.BUCKET, Config.OPERATER, UpYunUtils.md5(Config.PASSWORD));

    new Thread() {
        @Override
        public void run() {
            try {
                uploader.upload(file, "/resume/" + System.currentTimeMillis() + ".mp4", null);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UpYunException e) {
                 e.printStackTrace();
            }
    	}
    }.start();
```




<h2 id="3">又拍云播放器</h2>

#### 功能说明 

* 支持在线视频协议：`HLS`, `RTMP`, `HTTP-FLV` 等，支持 `HLS` 多种分辨率切换

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

	
#### 手动安装：

直接将示例工程源码中 `UPLiveSDK.framework`文件夹拖拽到目标工程目录。

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
