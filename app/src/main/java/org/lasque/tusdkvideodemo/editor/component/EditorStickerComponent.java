package org.lasque.tusdkvideodemo.editor.component;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.impl.components.widget.sticker.StickerDynamicItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerImageItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerTextItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerDynamicData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerImageData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerItemViewInterface;
import org.lasque.tusdk.modules.view.widget.sticker.StickerTextData;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaStickerImageEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.editor.MovieEditorController;
import org.lasque.tusdkvideodemo.views.TileRecycleAdapter;
import org.lasque.tusdkvideodemo.views.editor.TuSdkMovieScrollPlayLineView;
import org.lasque.tusdkvideodemo.views.editor.playview.TuSdkMovieScrollView;
import org.lasque.tusdkvideodemo.views.editor.playview.TuSdkRangeSelectionBar;
import org.lasque.tusdkvideodemo.views.editor.playview.rangeselect.TuSdkMovieColorGroupView;
import org.lasque.tusdkvideodemo.views.editor.playview.rangeselect.TuSdkMovieColorRectView;

import static android.view.View.GONE;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2019/3/4 11:10
 * @Copright (c) 2019 tusdk.com. All rights reserved.
 * <p>
 * 裁剪组件
 */
public class  EditorStickerComponent extends EditorComponent {
    /** 底部视图 **/
    private View mBottomView;
    /** 贴纸父容器视图 **/
    private StickerView mStickerView;
    /** 返回按钮 **/
    private ImageButton mBackBtn;
    /** 下一步 **/
    private ImageButton mNextBtn;
    /** RecycleView **/
    private RecyclerView mTileRecycle;
    /** 贴纸的Adapter **/
    private TileRecycleAdapter mTileAdapter;
    /** 播放器控件 **/
    private TuSdkMovieScrollPlayLineView mLineView;
    /** 播放按钮 **/
    private ImageView mPlayBtn;
    /** 默认持续时间 **/
    private long defaultDurationUs =  1 * 1000000;
    /** 最小持续时间 **/
    private int minSelectTimeUs =  1 * 1000000;
    /** 当前选中的贴纸 **/
    private StickerImageData mCurrentSticker;
    /** 当前的颜色块 **/
    private TuSdkMovieColorRectView mCurrentColorRectView;
    /** 贴纸备忘管理 **/
    private EditorStickerImageBackups mStickerImageBackups;

    private TuSdkSize mCurrentPreviewSize = null;


    /**
     * 显示区域改变回调
     *
     * @since V3.0.0
     */
    private TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener mOnDisplayChangeListener = new TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener() {
        /**
         * @param previewSize 当前预览画布宽高
         */
        @Override
        public void onPreviewSizeChanged(final TuSdkSize previewSize) {
            if (getEditorController().getActivity().getImageStickerView() == null) return;
            mCurrentPreviewSize = TuSdkSize.create(previewSize.width,previewSize.height);
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
//                    getEditorController().getActivity().getImageStickerView().resize(previewSize, getEditorController().getVideoContentView());
                }
            });

        }
    };

    /** 贴纸控件代理 **/
    private StickerView.StickerViewDelegate mStickerDelegate = new StickerView.StickerViewDelegate() {
        @Override
        public boolean canAppendSticker(StickerView view, StickerData sticker) {
            return true;
        }

        @Override
        public boolean canAppendSticker(StickerView view, StickerDynamicData sticker) {
            return false;
        }

        @Override
        public void onStickerItemViewSelected(StickerData stickerData, String text, boolean needReverse) {
            if (stickerData != null && stickerData instanceof StickerImageData) {
                mLineView.setShowSelectBar(true);
                mCurrentSticker = (StickerImageData) stickerData;
                mLineView.setLeftBarPosition(((StickerImageData) stickerData).starTimeUs/(float)getEditorPlayer().getTotalTimeUs());
                mLineView.setRightBarPosition(((StickerImageData) stickerData).stopTimeUs/(float)getEditorPlayer().getTotalTimeUs());
                mCurrentColorRectView = mStickerImageBackups.findColorRect(stickerData);
            }
        }

        @Override
        public void onStickerItemViewSelected(StickerDynamicData stickerData, String text, boolean needReverse) {

        }

        @Override
        public void onStickerItemViewReleased() {

        }

        @Override
        public void onCancelAllStickerSelected() {
            mLineView.setShowSelectBar(false);
            mCurrentColorRectView = null;
        }

        @Override
        public void onStickerCountChanged(StickerData stickerData, StickerItemViewInterface stickerItemViewInterface, int operation, int count) {
            if(stickerItemViewInterface.getStickerType() == StickerView.StickerType.Text)return;
            if(operation == 0){
                mStickerImageBackups.removeBackupEntityWithSticker((StickerImageItemView) stickerItemViewInterface);
                mLineView.setShowSelectBar(false);
            }else {
                mLineView.setShowSelectBar(true);
                float startPercent = mLineView.getCurrentPercent();
                float endPercent = ((StickerImageData)stickerData).stopTimeUs/(float)getEditorPlayer().getTotalTimeUs();
                TuSdkMovieColorRectView rectView = mLineView.recoverColorRect(R.color.lsq_scence_effect_color_EdgeMagic01,startPercent,endPercent);
                mCurrentColorRectView = rectView;
                mStickerImageBackups.addBackupEntity(EditorStickerImageBackups.createBackUpEntity(stickerData, (StickerImageItemView) stickerItemViewInterface,rectView));
            }
        }

        @Override
        public void onStickerCountChanged(StickerDynamicData stickerData, StickerItemViewInterface stickerItemViewInterface, int operation, int count) {

        }
    };

    private TuSdkMovieColorGroupView.OnSelectColorRectListener mSelectColorListener = new TuSdkMovieColorGroupView.OnSelectColorRectListener() {
        @Override
        public void onSelectColorRect(final TuSdkMovieColorRectView rectView) {

            if(rectView == null){
                mLineView.setShowSelectBar(false);
                mStickerView.cancelAllStickerSelected();
            }
            if(mStickerView.getStickerImageItems().size() == 0)return;
            final StickerImageData stickerData = (StickerImageData) mStickerImageBackups.findStickerData(rectView);
//                if(rectView == mCurrentColorRectView)return;

            if (stickerData != null && stickerData instanceof StickerImageData) {
                mLineView.setShowSelectBar(true);
                mCurrentSticker = stickerData;
                final float leftPercent =  stickerData.starTimeUs/(float)getEditorPlayer().getTotalTimeUs();
                float rightPercent =stickerData.stopTimeUs/(float)getEditorPlayer().getTotalTimeUs();
                mLineView.setLeftBarPosition(leftPercent);
                mLineView.setRightBarPosition(rightPercent);

                if(mCurrentColorRectView == rectView)return;

                mCurrentColorRectView = rectView;
                ThreadHelper.post(new Runnable() {
                    @Override
                    public void run() {
                        mLineView.seekTo(rectView.getStartPercent()+0.002f);
                    }
                });
            }

            if(mStickerImageBackups.findStickerItem(rectView) != null){
                mStickerView.onStickerItemViewSelected(mStickerImageBackups.findStickerItem(rectView));
                mStickerImageBackups.findStickerItem(rectView).setSelected(true);
            }
        }
    };



    /** 播放状态和进度回调 */
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
            if (mBottomView == null) return;
            setPlayState(state);
        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if (mBottomView == null || isAnimationStaring ) return;
            mLineView.seekTo(percentage);
        }
    };


    private TuSdkMovieScrollView.OnProgressChangedListener mOnScrollingPlayPositionListener = new TuSdkMovieScrollView.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(float progress, boolean isTouching) {
            long playPositionTime = (long) (progress * getEditorPlayer().getTotalTimeUs());
            for (StickerItemViewInterface itemViewInterface : mStickerView.getStickerItems()) {
                if (itemViewInterface instanceof StickerImageItemView) {
                    StickerImageItemView itemView = (StickerImageItemView) itemViewInterface;
                    StickerImageData textData = (StickerImageData) itemView.getSticker();
                    if (textData.isContains(playPositionTime)) {
                        itemView.setVisibility(View.VISIBLE);
                    } else {
                        itemView.setVisibility(View.GONE);
                    }
                }else if (itemViewInterface instanceof StickerTextItemView){
                    StickerTextItemView itemView = (StickerTextItemView) itemViewInterface;
                    StickerTextData imageData = (StickerTextData) itemView.getSticker();
                    if(imageData.isContains(playPositionTime)){
                        itemView.setVisibility(View.VISIBLE);
                    }else {
                        itemView.setVisibility(GONE);
                    }
                }else if (itemViewInterface instanceof StickerDynamicItemView){
                    StickerDynamicItemView itemView = ((StickerDynamicItemView) itemViewInterface);
                    StickerDynamicData dynamicData = itemView.getCurrentStickerGroup();
                    itemView.updateStickers(System.currentTimeMillis());
                    if (dynamicData.isContains(playPositionTime)){
                        itemView.setVisibility(View.VISIBLE);
                    } else {
                        itemView.setVisibility(GONE);
                    }
                }
            }

            if(!isTouching)return;
            if(isTouching){
                getEditorPlayer().pausePreview();
            }

            if (getEditorPlayer().isPause())
                getEditorPlayer().seekOutputTimeUs(playPositionTime);

        }

        @Override
        public void onCancelSeek() {

        }
    };


    private TuSdkRangeSelectionBar.OnSelectRangeChangedListener mOnSelectTimeChangeListener = new TuSdkRangeSelectionBar.OnSelectRangeChangedListener() {
        @Override
        public void onSelectRangeChanged(float leftPercent, float rightPerchent, int type) {
            if (mCurrentSticker == null) return;
            if(type == 0){
                mCurrentSticker.starTimeUs = (long) (leftPercent * getEditorPlayer().getTotalTimeUs());
            }else if(type == 1) {
                mCurrentSticker.stopTimeUs = (long) (rightPerchent * getEditorPlayer().getTotalTimeUs());
            }
            mLineView.changeColorRect(mCurrentColorRectView,leftPercent,rightPerchent);
        }
    };


    private TuSdkRangeSelectionBar.OnTouchSelectBarListener mOnTouchSelectBarlistener = new TuSdkRangeSelectionBar.OnTouchSelectBarListener() {
        @Override
        public void onTouchBar(float leftPercent, float rightPerchent, int type) {
            if(type == 0){
                mLineView.seekTo(leftPercent);
            }else if(type == 1) {
                mLineView.seekTo(rightPerchent);
            }
        }
    };

    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorStickerComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.Sticker;
        mStickerView = getEditorController().getActivity().getImageStickerView();
        getEditorPlayer().addPreviewSizeChangeListener(mOnDisplayChangeListener);
        getEditorPlayer().addProgressListener(mPlayProgressListener);

        mStickerImageBackups = new EditorStickerImageBackups(mStickerView,getEditorEffector(),editorController.getImageTextRankHelper());

        mStickerView.setDelegate(mStickerDelegate);
    }

    @Override
    public void attach() {
        getEditorController().getActivity().getTextStickerView().setVisibility(View.VISIBLE);
        getEditorController().getBottomView().addView(getBottomView());
        getEditorPlayer().pausePreview();
        getEditorController().getVideoContentView().setClickable(false);
        getEditorController().getPlayBtn().setVisibility(View.GONE);

        mStickerView.setDelegate(mStickerDelegate);
        mStickerView.changeOrUpdateStickerType(StickerView.StickerType.Image);
    }

    @Override
    public void detach() {
        getEditorPlayer().seekOutputTimeUs(0);
        getEditorController().getPlayBtn().setVisibility(View.VISIBLE);
        getEditorController().getVideoContentView().setClickable(true);
        getEditorController().getActivity().getTextStickerView().setVisibility(GONE);

        mStickerImageBackups.onComponentDetach();
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        getEditorPlayer().seekOutputTimeUs(0);
        if(getEditorPlayer().isReversing()) {
            mLineView.seekTo(1f);
        }else {
            mLineView.seekTo(0f);
        }
    }

    @Override
    public View getHeaderView() {
        return null;
    }

    @Override
    public View getBottomView() {
        if (mBottomView == null) {
            mBottomView = initBottomView();
        }
        return mBottomView;
    }

    public EditorStickerImageBackups getStickerImageBackups(){
        return mStickerImageBackups;
    }

    private View initBottomView() {
        if (mBottomView == null) {
            View bottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_sticker_bottom, null);
            mBottomView = bottomView;

            mLineView = bottomView.findViewById(R.id.lsq_editor_sticker_play_range);
            mLineView.setOnSelectColorRectListener(mSelectColorListener);
            mLineView.setSelectRangeChangedListener(mOnSelectTimeChangeListener);
            mLineView.setOnTouchSelectBarListener(mOnTouchSelectBarlistener);
            mLineView.setOnProgressChangedListener(mOnScrollingPlayPositionListener);
            float minPercent = minSelectTimeUs/(float)getEditorPlayer().getTotalTimeUs();
            mLineView.setMinWidth(minPercent);

            mLineView.setShowSelectBar(false);
            mLineView.setType(1);
            mStickerImageBackups.setLineView(mLineView);

            mPlayBtn = bottomView.findViewById(R.id.lsq_editor_sticker_play);
            mPlayBtn.setOnClickListener(mOnClickListener);

            mBackBtn = bottomView.findViewById(R.id.lsq_sticker_close);
            mBackBtn.setOnClickListener(mOnClickListener);
            mNextBtn = bottomView.findViewById(R.id.lsq_sticker_sure);
            mNextBtn.setOnClickListener(mOnClickListener);
            mTileRecycle = bottomView.findViewById(R.id.lsq_sticker_list_view);
            mTileRecycle.setLayoutManager(new LinearLayoutManager(getEditorController().getActivity(), LinearLayoutManager.HORIZONTAL, false));
            mTileAdapter = new TileRecycleAdapter();
            mTileRecycle.setAdapter(mTileAdapter);
            mTileAdapter.setItemClickListener(new TileRecycleAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int resId) {
                    getEditorPlayer().pausePreview();

                    Bitmap bitmap = BitmapFactory.decodeResource(getEditorController().getActivity().getResources(), resId);
                    StickerImageData imageData = new StickerImageData();
                    imageData.setImage(bitmap);
                    imageData.height =  TuSdkContext.px2dip(bitmap.getHeight());
                    imageData.width = TuSdkContext.px2dip(bitmap.getWidth());
                    imageData.starTimeUs = 0;
                    imageData.stopTimeUs = 2 * 1000000;

                    //时间间隔为2s
                    imageData.starTimeUs = (long) (mLineView.getCurrentPercent() * getEditorPlayer().getInputTotalTimeUs());
                    if(imageData.starTimeUs + defaultDurationUs > getEditorPlayer().getInputTotalTimeUs() ){
                        imageData.stopTimeUs = getEditorPlayer().getOutputTotalTimeUS();
                    }else {
                        imageData.stopTimeUs = imageData.starTimeUs + defaultDurationUs;
                    }

                    getEditorController().getActivity().getImageStickerView().appendSticker(imageData);
                }
            });
        }
        return mBottomView;
    }

    public void backUpDatas(){
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediEffectDataTypeStickerImage);
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeText);
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeDynamicSticker);
        ThreadHelper.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mStickerImageBackups == null)return;
                mStickerImageBackups.onComponentAttach();
            }
        },50);

    }

    /**
     * 设置播放状态
     *
     * @param state 0 播放  1 暂停
     * @since V3.0.0
     */
    public void setPlayState(int state) {
        if(state == 1){
            getEditorPlayer().pausePreview();
        }
        else {
            mStickerView.cancelAllStickerSelected();
            getEditorPlayer().startPreview();
        }

        mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(state == 0 ? R.drawable.edit_ic_pause : R.drawable.edit_ic_play));
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lsq_sticker_close:
                    mStickerImageBackups.onBackEffect();
                    getEditorController().onBackEvent();
                    break;
                case R.id.lsq_sticker_sure:
                    mStickerImageBackups.onApplyEffect();
                    handleCompleted();
                    getEditorController().onBackEvent();
                    break;
                case R.id.lsq_editor_sticker_play:
                    if (getEditorPlayer().isPause())
                        getEditorPlayer().startPreview();
                    else
                        getEditorPlayer().pausePreview();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 开始预览
     */
    private void startPreview() {
        getEditorPlayer().startPreview();
        getEditorController().getPlayBtn().setVisibility(View.GONE);
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {
        getBottomView();
        mLineView.addBitmap(bitmap);
    }

    /**
     *
     */
    protected void handleCompleted() {
        for (StickerItemViewInterface stickerItem : getEditorController().getActivity().getImageStickerView().getStickerItems()) {
            if(stickerItem instanceof StickerImageItemView){
                float renderWidth = mCurrentPreviewSize.width;
                float renderHeight = mCurrentPreviewSize.height;

//                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mStickerView.getLayoutParams();
//                float renderWidth = layoutParams.width - layoutParams.leftMargin;
//                float renderHeight = layoutParams.height - layoutParams.topMargin;


//                float renderWidth = mStickerView.getWidth() - mStickerView.getLeft();
//                float renderHeight = getEditorController().getVideoContentView().getMeasuredHeight() - getEditorController().getVideoContentView().getLeft();



                StickerImageItemView stickerItemView = ((StickerImageItemView) stickerItem);

                //生成图片前重置一些视图
                stickerItemView.resetRotation();
                stickerItemView.setStroke(TuSdkContext.getColor(R.color.lsq_color_transparent), 0);

                TuSdkSize sclaSize = stickerItemView.getRenderScaledSize();
                //生成相应的图片
                Bitmap textBitmap = stickerItemView.getStickerData().getImage();

                stickerItemView.setStroke(TuSdkContext.getColor(R.color.lsq_color_white), 2);
                StickerView stickerView = getEditorController().getActivity().getImageStickerView();
                int[] parentLocaiont = new int[2];
                stickerView.getLocationInWindow(parentLocaiont);


                //获取计算相应的位置
                int[] locaiont = new int[2];
                /** 当SDKVersion >= 27 需要使用 getLocationInWindow() 方法 不然会产生极大的误差 小于27时 getLocationInWindow() 与 getLocationOnScreen()方法返回值相同*/
                stickerItemView.getImageView().getLocationInWindow(locaiont);
                int pointX = locaiont[0] - parentLocaiont[0];
                int pointY = (int) (locaiont[1] - parentLocaiont[1]);

                /** 归一化计算 */
                float offsetX = pointX / renderWidth;
                float offsetY = pointY / renderHeight;
                float stickerWidth = (float) sclaSize.width / renderWidth;
                float stickerHeight = (float) sclaSize.height / renderHeight;
                float degree = stickerItemView.getResult(null).degree;
                float ratio = sclaSize.maxMinRatio();

                //设置初始化的时间
                long starTimeUs = ((StickerImageData) stickerItemView.getSticker()).starTimeUs;
                long stopTimeUs = ((StickerImageData) stickerItemView.getSticker()).stopTimeUs;
                //创建特效对象并且应用
                TuSdkMediaStickerImageEffectData stickerImageEffectData = createTileEffectData(textBitmap,stickerWidth,stickerHeight,offsetX,offsetY,degree,starTimeUs,stopTimeUs,ratio);
                getEditorEffector().addMediaEffectData(stickerImageEffectData);

                EditorStickerImageBackups.StickerImageBackupEntity backupEntity = mStickerImageBackups.findTextBackupEntityByMemo(stickerItemView);
                if (backupEntity != null)
                    backupEntity.stickerImageMediaEffectData = stickerImageEffectData;

                stickerItemView.setVisibility(GONE);
            }else if(stickerItem instanceof StickerTextItemView){
                StickerTextItemView stickerItemView = ((StickerTextItemView) stickerItem);
                EditorTextBackups.TextBackupEntity backupEntity = getEditorController().getTextComponent().getTextBackups().findTextBackupEntityToMemo(stickerItemView);
                if(backupEntity != null)
                    getEditorEffector().addMediaEffectData(backupEntity.textMediaEffectData);
                stickerItemView.setVisibility(GONE);
            }
        }


        //清空重置相关数据
        getEditorController().getActivity().getImageStickerView().cancelAllStickerSelected();
        getEditorController().getActivity().getImageStickerView().removeAllSticker();
    }

    /**
     * 将数据转成公用的 TuSdkMediaEffectData
     *
     * @param bitmap      图片
     * @param displaySize 图片显示的大小
     * @param offsetX     相对视频左上角X轴的位置
     * @param offsetY     相对视频左上角Y轴的位置
     * @param rotation    旋转的角度
     * @param startTimeUs 特效开始的时间
     * @param stopTimeUs  特效结束的时间
     * @param stickerSize 当前StickerView的宽高（计算比例用）
     * @return
     */
    @Deprecated
    protected TuSdkMediaStickerImageEffectData createTileEffectData(Bitmap bitmap, TuSdkSize displaySize, float offsetX, float offsetY, float rotation, long startTimeUs, long stopTimeUs ,TuSdkSize stickerSize) {
        TuSdkMediaStickerImageEffectData mediaTextEffectData = new TuSdkMediaStickerImageEffectData(bitmap,offsetX,offsetY,rotation,displaySize,stickerSize);
        mediaTextEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(startTimeUs, stopTimeUs));
        return mediaTextEffectData;
    }

    /**
     * @param bitmap 图片
     * @param stickerWidth 归一化后 图片的显示宽度
     * @param stickerHeight 归一化后 图片的显示高度
     * @param offsetX 归一化后 x轴相对左上角偏移量
     * @param offsetY 归一化后 y轴相对左上角偏移量
     * @param rotation 旋转的角度
     * @param startTimeUs 特效开始的时间
     * @param stopTimeUs 特效结束的时间
     * @return
     */
    protected TuSdkMediaStickerImageEffectData createTileEffectData(Bitmap bitmap,float stickerWidth,float stickerHeight,float offsetX,float offsetY,float rotation,long startTimeUs,long stopTimeUs,float ratio){
        TuSdkMediaStickerImageEffectData mediaStickerImageEffectData = new TuSdkMediaStickerImageEffectData(bitmap, stickerWidth, stickerHeight, offsetX, offsetY, rotation,ratio);
        mediaStickerImageEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(startTimeUs,stopTimeUs));
        return mediaStickerImageEffectData;
    }

}
