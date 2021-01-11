package org.lasque.tusdkvideodemo.views.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;


import org.lasque.tusdkvideodemo.views.editor.ruler.HorizontalPlayViewOne;
import org.lasque.tusdkvideodemo.views.editor.ruler.PlayLineView;
import org.lasque.tusdkvideodemo.views.editor.ruler.RangeSelectionBar;

import java.util.ArrayList;
import java.util.List;

import static org.lasque.tusdkvideodemo.views.editor.LineView.LineViewType.CanRolling;
import static org.lasque.tusdkvideodemo.views.editor.LineView.LineViewType.DrawColorRect;

/**
 * 播放缩略图控件
 */

public class LineView extends FrameLayout {

    private Context mContext;

    // 是否隐藏Bar
    private boolean isShowRangeBar = true;

    private List<Bitmap> mBitmapList = new ArrayList<>();
    private RangeSelectionBar mRangeSelectionBar;

    /**
     * 时间轴总长
     */
    private int mTotalWidth = 0;
    /**
     * Bar宽度
     */
    private int mLeftBarWidth = CUtils.dip2px(12);
    /**
     * Bar高度
     */
    private int selectionBarHeight = 0;
    /**
     * 外边框高度
     */
    private int mOutLineWidth = CUtils.dip2px(2);
    private int mScreenWidth;
    /**
     * 在父控件中的起始位置
     */
    private int mInParentLeft = 0;
    public HorizontalPlayViewOne horizontalPlayViewOne;
    private PlayLineView playLineView;

    /**
     * 光标颜色
     */
    private int mPointerColorId = Color.WHITE;
    /**
     * 封面矩形选择框颜色
     */
    private int mCoverOutBorderColor = Color.GREEN;

    /**
     * 播放指针所在位置的百分比
     */
    private float mPlayPointerPositionTimePercent;
    /**
     * 播放指针所在位置对应的时间
     */
    private long mPlayPointerPositionTimeUs;

    /**
     * 控件播放到的时间的百分比
     */
    private float playPositionTimePercent;
    /**
     * 控件播放到的时间
     */
    private long playPositionTimeUs;

    /**
     * 封面矩形选择框起始时间百分比
     */
    private float mCoverStartTimePercent;
    /**
     * 封面矩形选择框结束时间百分比
     */
    private float mCoverEndTimePercent;
    /**
     * 封面矩形选择框起始时间
     */
    private long mCoverStartTimeUs;
    /**
     * 封面矩形选择框结束时间
     */
    private long mCoverEndTimeUs;

    /**
     * 左右bar选择的时间长百分比
     */
    private float mSelectTimePercent;
    /**
     * 左右bar选择的起始时间百分比
     */
    private float mLeftStartTimePercent;
    /**
     * 左右bar选择的结束时间百分比
     */
    private float mRightEndTimePercent = 1;
    /**
     * 左右bar选择的起始时间
     */
    private long mLeftStartTimeUs;
    /**
     * 左右bar选择的结束时间
     */
    private long mRightEndTimeUs;
    /**
     * 左右bar选择的时间长
     */
    private long mSelectTimeUs;
    private LineViewType mLineViewType;
    private long mTotalTimeUs;
    /** 是否 正在触摸状态 */
    private boolean mIsTouching = false;

    /** 最长选取时长 **/
    private long mMaxSelectTimeUs = -1;
    private long mMaxDrawWidth = -1;
    /** 最短选取时长 **/
    private long mMinSelectTimeUs = -1;
    private long mMinDrawWidth = -1;

    private int mBitmapCount = 20;


    /**
     * DrawColorRect  画颜色矩形方块  如魔法特效时
     * DrawPointer  画可移动的播放指针   第一次编辑时
     * IsCoverRect  画封面选择矩形
     * CanRolling   指示针固定（如固定于屏幕中央） 控件可移动
     */
    public enum LineViewType {
        DrawColorRect,
        DrawPointer,
        IsCoverRect,
        CanRolling
    }

    public LineView(@NonNull Context context) {
        this(context, null);
    }

    public LineView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mScreenWidth = getScreenWidth(context);

    }

    /** 初始化 **/
    public void loadView(){

        final LayoutParams[] lp = {new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                .WRAP_CONTENT)};
        horizontalPlayViewOne = new HorizontalPlayViewOne(mContext);
        horizontalPlayViewOne.setBitmapCount(mBitmapCount);
        horizontalPlayViewOne.setInitValue(mBitmapList, mTotalTimeUs, mScreenWidth / 2-mInParentLeft,
                mPointerColorId, mCoverOutBorderColor);
        horizontalPlayViewOne.setLayoutParams(lp[0]);
        horizontalPlayViewOne.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mOnScrollingPlayPositionListener != null) {
                    mOnScrollingPlayPositionListener.noticePlayerStop();
                }
                return false;
            }
        });
        playLineView = new PlayLineView(mContext);
        mRangeSelectionBar = new RangeSelectionBar(mContext);
        mRangeSelectionBar.setShowBar(isShowRangeBar);
        if (mMaxDrawWidth > -1) mRangeSelectionBar.setMaxDrawWidth(mMaxDrawWidth);
        if (mMinDrawWidth > -1) mRangeSelectionBar.setMinDrawWidth(mMinDrawWidth);
        mTotalWidth = horizontalPlayViewOne.getTotalBitmapWidth();
        selectionBarHeight = horizontalPlayViewOne.getBitmapHeight();
        mRangeSelectionBar.setValue(horizontalPlayViewOne.getTotalBitmapWidth(), horizontalPlayViewOne.getBitmapHeight(), 0);
        mRangeSelectionBar.setLayoutParams(lp[0]);
        //DrawColorRect
        if (DrawColorRect == mLineViewType){
            horizontalPlayViewOne.setDrawWhat(true, false, false, true);//画颜色矩形
            horizontalPlayViewOne.setOnSizeChanedListener(new HorizontalPlayViewOne.OnSizeChanedListener() {
                @Override
                public void onSizeChanged(int totalWidth, int height) {
//                    mTotalWidth = totalWidth;
                    selectionBarHeight = height;
                }

                @Override
                public void onBitmapListSizeChanged(int count) {
                    if(count == mBitmapCount){
                        horizontalPlayViewOne.setInitValue(mBitmapList, mTotalTimeUs, mScreenWidth / 2 -
                                mInParentLeft, mPointerColorId, mCoverOutBorderColor);
                        playLineView.setValue(mTotalWidth, selectionBarHeight, mOutLineWidth,
                                mScreenWidth / 2 - mInParentLeft, mPointerColorId);
                        addView(playLineView);
                    }
                }
            });

            horizontalPlayViewOne.setOnSelfScrollingListener(new HorizontalPlayViewOne
                    .OnSelfScrollingListener() {
                @Override
                public void onSelfScrolling(int moveDistance, boolean isTouch) {
                    playPositionTimePercent = moveDistance / (float) mTotalWidth;
                    playPositionTimeUs = (long) (playPositionTimePercent * mTotalTimeUs);
                    if (mOnScrollingPlayPositionListener != null) {
                        mOnScrollingPlayPositionListener.onPlayPosition(playPositionTimeUs,
                                playPositionTimePercent, isTouch);
                    }
                }
            });
            horizontalPlayViewOne.setOnIsTouchingListener(new HorizontalPlayViewOne.OnIsTouchingListener() {
                @Override
                public void isTouching(boolean isTouching) {

                    mIsTouching = isTouching;
                }
            });

            this.addView(horizontalPlayViewOne);
        }


        //DrawPointer
        if(LineViewType.DrawPointer == mLineViewType) {

            horizontalPlayViewOne.setInitValue(mBitmapList, mTotalTimeUs, mLeftBarWidth,
                    mPointerColorId, mCoverOutBorderColor);

            horizontalPlayViewOne.setOnSizeChanedListener(new HorizontalPlayViewOne.OnSizeChanedListener() {
                private int mHeight;
                private int mWidth;

                @Override
                public void onSizeChanged(int totalWidth, int height) {
                    mWidth = totalWidth;
                    mHeight = height;
                }

                @Override
                public void onBitmapListSizeChanged(int count) {
                    if (count == mBitmapCount) {

                        playLineView.setValue(mTotalWidth, selectionBarHeight, mOutLineWidth, mScreenWidth /
                                2, mPointerColorId);
                        playLineView.setLayoutParams(lp[0]);

//                        mTotalWidth = mWidth;
                        lp[0] = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                        lp[0].gravity = Gravity.CENTER_VERTICAL;
//                        mRangeSelectionBar.setValue(mWidth, mHeight, 0);
//                        mRangeSelectionBar.setLayoutParams(lp[0]);
                        mRangeSelectionBar.setShowBar(isShowRangeBar);
                        horizontalPlayViewOne.setOnStopPlayListener(new HorizontalPlayViewOne
                                .OnStopPlayListener() {
                            @Override
                            public void stopPlay() {
                                if (mOnStopPlayListener != null) {
                                    mOnStopPlayListener.stopPlay();
                                }
                            }
                        });
                        mRangeSelectionBar.setTwoBarsMinDistance(mTwoBarsMinDistance);

                    }
                }
            });

            drawPointerType();

            horizontalPlayViewOne.setDrawWhat(false, true, false, false);
            this.addView(horizontalPlayViewOne);
            addView(mRangeSelectionBar);
        }



        if (LineViewType.IsCoverRect == mLineViewType){
            horizontalPlayViewOne.setDrawWhat(false, false, true, false);
            horizontalPlayViewOne.setOnSizeChanedListener(new HorizontalPlayViewOne.OnSizeChanedListener() {
                @Override
                public void onSizeChanged(int totalWidth, int height) {
//                    mTotalWidth = totalWidth;
//                    selectionBarHeight = height;
                }

                @Override
                public void onBitmapListSizeChanged(int count) {
                    if(count == mBitmapCount){
                        horizontalPlayViewOne.setInitValue(mBitmapList, mTotalTimeUs, mScreenWidth / 2 -
                                mInParentLeft, mPointerColorId, mCoverOutBorderColor);
                    }
                }
            });
            this.addView(horizontalPlayViewOne);
        }


        if (CanRolling == mLineViewType){
            horizontalPlayViewOne.setDrawWhat(false, false, false, true);

            lp[0] = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            lp[0].gravity = Gravity.CENTER_VERTICAL;

            horizontalPlayViewOne.setOnSizeChanedListener(new HorizontalPlayViewOne.OnSizeChanedListener() {
                @Override
                public void onSizeChanged(int totalWidth, int height) {
//                    mTotalWidth = totalWidth;
//                    selectionBarHeight = height;
                }

                @Override
                public void onBitmapListSizeChanged(int count) {
                    if(count == mBitmapCount){
                        horizontalPlayViewOne.setInitValue(mBitmapList, mTotalTimeUs, mScreenWidth / 2 -
                                mInParentLeft, mPointerColorId, mCoverOutBorderColor);



//                        mRangeSelectionBar.setValue(mTotalWidth, selectionBarHeight, 0);
                        mRangeSelectionBar.setShowBar(isShowRangeBar);
                        if (mMaxDrawWidth > -1) mRangeSelectionBar.setMaxDrawWidth(mMaxDrawWidth);
                        if (mMinDrawWidth > -1) mRangeSelectionBar.setMinDrawWidth(mMinDrawWidth);

                        mRangeSelectionBar.setValue(mTotalWidth, selectionBarHeight, mScreenWidth / 2 -
                                mLeftBarWidth - mInParentLeft);
                        mRangeSelectionBar.setTwoBarsMinDistance(mTwoBarsMinDistance);

                        startDrawing();
                    }
                }
            });

            horizontalPlayViewOne.setOnDistanceChangeListener(new HorizontalPlayViewOne
                    .OnDistanceChangeListener() {
                @Override
                public void onScaleScroll(int scale) {
                    mRangeSelectionBar.scrollBy(scale, 0);
                    mRangeSelectionBar.setDisatnce(horizontalPlayViewOne.movedDistancePx);
                }
            });
            horizontalPlayViewOne.setOnSelfScrollingListener(new HorizontalPlayViewOne
                    .OnSelfScrollingListener() {
                @Override
                public void onSelfScrolling(int moveDistance, boolean isTouch) {
                    playPositionTimePercent = moveDistance / (float) mTotalWidth;
                    playPositionTimeUs = (long) (playPositionTimePercent * mTotalTimeUs);
                    if (mOnScrollingPlayPositionListener != null) {
                        mOnScrollingPlayPositionListener.onPlayPosition(playPositionTimeUs,
                                playPositionTimePercent, isTouch);
                    }
                }
            });

            horizontalPlayViewOne.setOnIsTouchingListener(new HorizontalPlayViewOne.OnIsTouchingListener() {
                @Override
                public void isTouching(boolean isTouching) {

                    mIsTouching = isTouching;
                }
            });
            mRangeSelectionBar.setOnBarMoveListener(new RangeSelectionBar.OnBarMoveListener() {
                @Override
                public void onBarMove(int leftBarX, int rightBarX) {
                    //选择了多长时间
                    mSelectTimePercent = (mTotalWidth - leftBarX - rightBarX) / (float)
                            mTotalWidth;
                    mSelectTimeUs = (long) (mSelectTimePercent * mTotalTimeUs);
                    //起始时间位置
                    mLeftStartTimePercent = leftBarX / (float) mTotalWidth;
                    mLeftStartTimeUs = (long) (mLeftStartTimePercent * mTotalTimeUs);
                    //结束时间位置
                    mRightEndTimePercent = (mTotalWidth - rightBarX) / (float) mTotalWidth;
                    mRightEndTimeUs = (long) (mRightEndTimePercent * mTotalTimeUs);
                    if (mOnSelectTimeChangeListener != null) {
                        mOnSelectTimeChangeListener.onTimeChange(mLeftStartTimeUs,
                                mRightEndTimeUs, mSelectTimeUs, mLeftStartTimePercent,
                                mRightEndTimePercent, mSelectTimePercent);
                    }

                }

                @Override
                public void onMoveLeftBar(int leftBarX) {
                    //起始时间位置
                    mLeftStartTimePercent = leftBarX / (float) mTotalWidth;
                    mLeftStartTimeUs = (long) (mLeftStartTimePercent * mTotalTimeUs);
                    if (mOnSelectTimeChangeListener != null) {
                        mOnSelectTimeChangeListener.onLeftTimeChange(mLeftStartTimeUs,
                                mLeftStartTimePercent);
                    }
                }

                @Override
                public void onMoveRightBar(int rightBarX) {
                    //结束时间位置
                    mRightEndTimePercent = (mTotalWidth - rightBarX) / (float) mTotalWidth;
                    mRightEndTimeUs = (long) (mRightEndTimePercent * mTotalTimeUs);
                    if (mOnSelectTimeChangeListener != null) {
                        mOnSelectTimeChangeListener.onRightTimeChange(mRightEndTimeUs,
                                mRightEndTimePercent);
                    }
                }

                @Override
                public void onMaxValue() {
                    if (mOnSelectTimeChangeListener != null) {
                        mOnSelectTimeChangeListener.onMaxValue();
                    }
                }

                @Override
                public void onMinValue() {
                    if (mOnSelectTimeChangeListener != null) {
                        mOnSelectTimeChangeListener.onMinValue();
                    }
                }
            });


            this.addView(horizontalPlayViewOne);
            addView(mRangeSelectionBar);
            addView(playLineView);

        }

        mRangeSelectionBar.setOnBarTouchUpListener(new RangeSelectionBar.OnBarTouchUpListener() {

            @Override
            public void onLeftBarTouchUp(int leftBarX) {
                //起始时间位置
                mLeftStartTimePercent = leftBarX / (float) mTotalWidth;
                mLeftStartTimeUs = (long) (mLeftStartTimePercent * mTotalTimeUs);
                if (mOnBarTouchUpListener != null) {
                    mOnBarTouchUpListener.onLeftBarTouchUp(mLeftStartTimeUs, mLeftStartTimePercent);
                }
            }

            @Override
            public void onRightBarTouchUp(int rightBarX) {
                //结束时间位置
                mRightEndTimePercent = (mTotalWidth - rightBarX) / (float) mTotalWidth;
                mRightEndTimeUs = (long) (mRightEndTimePercent * mTotalTimeUs);
                if (mOnBarTouchUpListener != null) {
                    mOnBarTouchUpListener.onRightBarTouchUp(mRightEndTimeUs, mRightEndTimePercent);
                }
            }
        });



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /** 设置最大选取时长 微秒(us) **/
    public void setMaxSelectTimeUs(long maxSelectTimeUs) {
        this.mMaxSelectTimeUs = maxSelectTimeUs;
        float percent = (float) mMaxSelectTimeUs/(float)mTotalTimeUs;
        long drawMaxWidth = (long) (mTotalWidth * percent);
        mMaxDrawWidth = drawMaxWidth;
        if(mRangeSelectionBar == null) return;
        mRangeSelectionBar.setMaxDrawWidth(drawMaxWidth);
    }

    /** 设置最小选取时长 微秒(us) **/
    public void setMinSelectTimeUs(long minSelectTimeUs) {
        this.mMinSelectTimeUs = minSelectTimeUs;
        float percent = (float) mMinSelectTimeUs/(float) mTotalTimeUs;
        long drawMinWidth = (long) (mTotalWidth * percent);
        mMinDrawWidth = drawMinWidth;
        if(mRangeSelectionBar == null) return;
        mRangeSelectionBar.setMinDrawWidth(drawMinWidth);
    }

    private int count = 0;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        count = count + 1;
        mInParentLeft = left;

        if(mLineViewType == CanRolling || mLineViewType == DrawColorRect) {
            horizontalPlayViewOne.setInitValue(mBitmapList, mTotalTimeUs, mScreenWidth / 2 - mInParentLeft,
                    mPointerColorId, mCoverOutBorderColor);

            playLineView.setValue(mTotalWidth, selectionBarHeight, mOutLineWidth,
                    mScreenWidth / 2 - mInParentLeft, mPointerColorId);
        }



        if(playLineView!=null)playLineView.setLeftMargin(mScreenWidth/2 - mInParentLeft);
        if(horizontalPlayViewOne!=null && (mLineViewType == DrawColorRect || mLineViewType ==CanRolling ))horizontalPlayViewOne.setLeftMargin(mScreenWidth/2 - mInParentLeft);
        if(mRangeSelectionBar!=null && (mLineViewType == DrawColorRect || mLineViewType ==CanRolling ))mRangeSelectionBar.setStartLineX(mScreenWidth/2 - mInParentLeft);
    }

    private void init() {
        count = count + 1;
        if (mBitmapList.size() > 0 && mTotalTimeUs > 0) {
            if (DrawColorRect == mLineViewType) {//画颜色矩形
                drawColorRectType();
            }

            if (LineViewType.DrawPointer == mLineViewType) { //画播放指針
//                drawPointerType();
            }

            if (LineViewType.IsCoverRect == mLineViewType) {//画封面选择视图
                drawIsCoverRectType();
            }
            if (CanRolling == mLineViewType) { //可滚动 有左右bar
                drawCanRollingType();
            }



        }
    }

    /**
     * Type为CanRolling 时的方法
     */
    private void drawCanRollingType() {


    }

    /**
     * Type为画封面选择视图
     */
    private void drawIsCoverRectType() {
        mTotalWidth = horizontalPlayViewOne.getTotalBitmapWidth();
        horizontalPlayViewOne.setOnIsTouchingListener(new HorizontalPlayViewOne.OnIsTouchingListener() {
            @Override
            public void isTouching(boolean isTouching) {

                mIsTouching = isTouching;
            }
        });
        horizontalPlayViewOne.setOnCoverRectMoveListener(new HorizontalPlayViewOne
                .OnCoverRectMoveListener() {
            @Override
            public void onCoverRectMove(int movex, int coverRectWidth) {
                mCoverStartTimePercent = movex / (float) mTotalWidth;
                mCoverStartTimeUs = (long) (mCoverStartTimePercent * mTotalTimeUs);
                mCoverEndTimePercent = (movex + coverRectWidth) / (float) mTotalWidth;
                mCoverEndTimeUs = (long) (mCoverEndTimePercent * mTotalTimeUs);
                if (mOnSelectCoverTimeListener != null) {
                    mOnSelectCoverTimeListener.onCoverSelectTime(mCoverStartTimeUs,
                            mCoverEndTimeUs, mCoverStartTimePercent, mCoverEndTimePercent);
                }
            }
        });
    }

    /**
     * Type为画播放指针
     */
    private void drawPointerType() {
        horizontalPlayViewOne.setOnIsTouchingListener(new HorizontalPlayViewOne.OnIsTouchingListener() {
            @Override
            public void isTouching(boolean isTouching) {

                mIsTouching = isTouching;
            }
        });
        mRangeSelectionBar.setOnBarMoveListener(new RangeSelectionBar.OnBarMoveListener() {
            @Override
            public void onBarMove(int leftBarX, int rightBarX) {
                //选择了多长时间
                mSelectTimePercent = (mTotalWidth - leftBarX - rightBarX) / (float)
                        mTotalWidth;
                mSelectTimeUs = (long) (mSelectTimePercent * mTotalTimeUs);
                //起始时间位置
                mLeftStartTimePercent = leftBarX / (float) mTotalWidth;
                mLeftStartTimeUs = (long) (mLeftStartTimePercent * mTotalTimeUs);
                //结束时间位置
                mRightEndTimePercent = (mTotalWidth - rightBarX) / (float) mTotalWidth;
                mRightEndTimeUs = (long) (mRightEndTimePercent * mTotalTimeUs);
                if (mOnSelectTimeChangeListener != null) {
                    mOnSelectTimeChangeListener.onTimeChange(mLeftStartTimeUs,
                            mRightEndTimeUs, mSelectTimeUs, mLeftStartTimePercent,
                            mRightEndTimePercent, mSelectTimePercent);
                }
            }

            @Override
            public void onMoveLeftBar(int leftBarX) {
                //起始时间位置
                mLeftStartTimePercent = leftBarX / (float) mTotalWidth;
                mLeftStartTimeUs = (long) (mLeftStartTimePercent * mTotalTimeUs);
                if (mOnSelectTimeChangeListener != null) {
                    mOnSelectTimeChangeListener.onLeftTimeChange(mLeftStartTimeUs,
                            mLeftStartTimePercent);
                }
            }

            @Override
            public void onMoveRightBar(int rightBarX) {
                //结束时间位置
                mRightEndTimePercent = (mTotalWidth - rightBarX) / (float) mTotalWidth;
                mRightEndTimeUs = (long) (mRightEndTimePercent * mTotalTimeUs);
                if (mOnSelectTimeChangeListener != null) {
                    mOnSelectTimeChangeListener.onRightTimeChange(mRightEndTimeUs,
                            mRightEndTimePercent);
                }
            }

            @Override
            public void onMaxValue() {
                if (mOnSelectTimeChangeListener != null) {
                    mOnSelectTimeChangeListener.onMaxValue();
                }
            }

            @Override
            public void onMinValue() {
                if (mOnSelectTimeChangeListener != null) {
                    mOnSelectTimeChangeListener.onMinValue();
                }
            }
        });

        mRangeSelectionBar.setOnBarTouchUpListener(new RangeSelectionBar.OnBarTouchUpListener() {

            @Override
            public void onLeftBarTouchUp(int leftBarX) {
                //起始时间位置
                mLeftStartTimePercent = leftBarX / (float) mTotalWidth;
                mLeftStartTimeUs = (long) (mLeftStartTimePercent * mTotalTimeUs);
                if (mOnBarTouchUpListener != null) {
                    mOnBarTouchUpListener.onLeftBarTouchUp(mLeftStartTimeUs, mLeftStartTimePercent);
                }
            }

            @Override
            public void onRightBarTouchUp(int rightBarX) {
                //结束时间位置
                mRightEndTimePercent = (mTotalWidth - rightBarX) / (float) mTotalWidth;
                mRightEndTimeUs = (long) (mRightEndTimePercent * mTotalTimeUs);
                if (mOnBarTouchUpListener != null) {
                    mOnBarTouchUpListener.onRightBarTouchUp(mRightEndTimeUs, mRightEndTimePercent);
                }
            }
        });

        horizontalPlayViewOne.setOnPlayPointerChangeListener(new HorizontalPlayViewOne
                .OnPlayPointerChangeListener() {
            @Override
            public void onPlayPointerChange(int moveDistance) {
                mPlayPointerPositionTimePercent = moveDistance / (float) mTotalWidth;
                mPlayPointerPositionTimeUs = (long) (mPlayPointerPositionTimePercent *
                        mTotalTimeUs);
                mRangeSelectionBar.setCursorPos(moveDistance);
                if (mOnPlayPointerChangeListener != null) {
                    mOnPlayPointerChangeListener.onPlayPointerPosition
                            (mPlayPointerPositionTimeUs, mPlayPointerPositionTimePercent);
                }
            }
        });
    }

    /**
     * Type为画颜色矩形
     */
    private void drawColorRectType() {

    }


    /**
     * 解决特效界面与viewpager滑动冲突问题
     *
     * @param
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }

    public void startDrawing() {
        if (mBitmapList.size() >= mBitmapCount && mTotalTimeUs > 0) {
            init();
        }
    }

    /** 获取当前播放的时间 **/
    public long getCurrentPlayTimeUs(){
        return playPositionTimeUs;
    }

    private OnSelectTimeChangeListener mOnSelectTimeChangeListener;

    public interface OnSelectTimeChangeListener {
        void onTimeChange(long startTime, long endTime, long selectTime, float
                startTimePercent, float endTimePercent, float selectTimePercent);

        void onLeftTimeChange(long startTime, float startTimePercent);

        void onRightTimeChange(long endTime, float endTimePercent);

        void onMaxValue();

        void onMinValue();
    }

    /**
     * bar 移动之后 选择的时间范围
     *
     * @param onSelectTimeChangeListener
     */
    public void setOnSelectTimeChangeListener(OnSelectTimeChangeListener
                                                      onSelectTimeChangeListener) {
        this.mOnSelectTimeChangeListener = onSelectTimeChangeListener;
    }


    private OnBarTouchUpListener mOnBarTouchUpListener;

    public interface OnBarTouchUpListener {
        void onLeftBarTouchUp(long startTime, float startTimePercent);

        void onRightBarTouchUp(long endTime, float endTimePercent);
    }

    /**
     * bar 松手时的监听
     *
     * @param onBarTouchUpListener
     */
    public void setOnBarTouchUpListener(OnBarTouchUpListener
                                                onBarTouchUpListener) {
        this.mOnBarTouchUpListener = onBarTouchUpListener;
    }


    private OnSelectCoverTimeListener mOnSelectCoverTimeListener;

    public interface OnSelectCoverTimeListener {
        //封面选择时间
        void onCoverSelectTime(long startTime, long endTime, float startTimePercent, float
                endTimePercent);
    }

    /**
     * 封面视图下 选择的封面矩形的时间
     *
     * @param onSelectCoverTimeListener
     */
    public void setOnSelectCoverTimeListener(OnSelectCoverTimeListener onSelectCoverTimeListener) {
        this.mOnSelectCoverTimeListener = onSelectCoverTimeListener;
    }


    private OnScrollingPlayPositionListener mOnScrollingPlayPositionListener;

    public interface OnScrollingPlayPositionListener {
        void onPlayPosition(long playPositionTime, float playPositionTimePercent, boolean isTouch);

        void noticePlayerStop();
    }

    /**
     * 滚动时 播放位置
     *
     * @param onScrollingDistanceListener
     */
    public void setOnScrollingPlayPositionListener(OnScrollingPlayPositionListener
                                                           onScrollingDistanceListener) {
        this.mOnScrollingPlayPositionListener = onScrollingDistanceListener;
    }

    //滚动时 播放位置
    private OnPlayPointerChangeListener mOnPlayPointerChangeListener;

    public interface OnPlayPointerChangeListener {
        void onPlayPointerPosition(long playPointerPositionTime, float
                playPointerPositionTimePercent);
    }

    /**
     * 播放指针 位置改变监听
     *
     * @param onPlayPointerChangeListener
     */
    public void setOnPlayPointerChangeListener(OnPlayPointerChangeListener
                                                       onPlayPointerChangeListener) {
        this.mOnPlayPointerChangeListener = onPlayPointerChangeListener;
    }


    private OnStopPlayListener mOnStopPlayListener;

    public interface OnStopPlayListener {
        void stopPlay();
    }

    /**
     * 停止播放监听
     *
     * @param onStopPlayListener
     */
    public void setOnStopPlayListener(OnStopPlayListener onStopPlayListener) {
        this.mOnStopPlayListener = onStopPlayListener;
    }


    private OnDrawRectListChangeListener mOnDrawRectListChangeListener;

    public interface OnDrawRectListChangeListener {
        void onDrawRectListChange(int num);
    }

    /**
     * 设置颜色方块矩形个数改变监听  画特效矩形时用
     *
     * @param onColorRectListChangeListener
     */
    public void setOnDrawRectListChangeListener(OnDrawRectListChangeListener
                                                        onColorRectListChangeListener) {
        this.mOnDrawRectListChangeListener = onColorRectListChangeListener;
    }


    private OnThumbTouchListener mOnThumbTouchListener;

    public interface OnThumbTouchListener {
        void onThumbTouch();
    }

    /**
     * 设置颜色方块矩形个数改变监听  画特效矩形时用
     *
     * @param onThumbTouchListener
     */
    public void setOnThumbTouchListener(OnThumbTouchListener
                                                onThumbTouchListener) {
        this.mOnThumbTouchListener = onThumbTouchListener;
    }


    /**
     * 控件移动到某位置  播放器播放时调用
     *
     * @param percent
     */
    public void moveToPercent(float percent) {
        if (horizontalPlayViewOne == null) return;
        horizontalPlayViewOne.moveToPercent(percent);
    }

    /**
     * 控件移动到某位置 播放器播放时调用
     *
     * @param moveToTimeUs
     */
    public void moveToPercent(long moveToTimeUs) {
        if (horizontalPlayViewOne == null) return;
        horizontalPlayViewOne.moveToPercent(moveToTimeUs);
    }

    /**
     * 播放指针移动到 某位置
     *
     * @param percent
     */
    public void pointerMoveToPercent(float percent) {
        if (horizontalPlayViewOne == null) return;
        horizontalPlayViewOne.pointerMoveToPercent(percent);
    }

    /**
     * 播放指针移动到 某位置
     *
     * @param moveToTimeUs
     */
    public void pointerMoveToPercent(long moveToTimeUs) {
        float percent = moveToTimeUs / (float) mTotalTimeUs;
        if (horizontalPlayViewOne == null) return;
        horizontalPlayViewOne.pointerMoveToPercent(percent);
    }

    /**
     * 初始化
     *
     * @param initType
     * @param totalTimeUs    总时间
     * @param bitmapList     缩略图集合
     * @param pointerColorId 指针颜色
     */
    public void setInitType(LineViewType initType, long totalTimeUs, List<Bitmap> bitmapList, int
            pointerColorId) {
        mLineViewType = initType;
        mTotalTimeUs = totalTimeUs;
        this.mBitmapList = bitmapList;
        if (pointerColorId >= 0) {

        } else {
            mPointerColorId = Color.argb(Color.alpha(pointerColorId), Color.red(pointerColorId),
                    Color.green(pointerColorId), Color.blue(pointerColorId));
        }
    }


    /**
     * 初始化
     *
     * @param initType
     * @param pointerColorId 指针颜色
     */
    public void setInitType(LineViewType initType, int pointerColorId) {
        mLineViewType = initType;
        mPointerColorId = pointerColorId;
    }

    /**
     * 设置缩略图
     *
     * @param bitmapList
     */
    public void setBitmapList(List<Bitmap> bitmapList) {
        this.mBitmapList = bitmapList;
        startDrawing();
    }

    /**
     * 设置总时间
     *
     * @param totalTimeUs
     */
    public void setTotalTimeUs(long totalTimeUs) {
        mRightEndTimeUs = mTotalTimeUs = totalTimeUs;
        if(horizontalPlayViewOne != null) {
            horizontalPlayViewOne.setTotalTimeUs(totalTimeUs);
        }
        startDrawing();
    }

    /**
     * 封面矩形选择框颜色
     *
     * @param coverOutBorderColor
     */
    public void setCoverOutBorderColor(int coverOutBorderColor) {
        mCoverOutBorderColor = coverOutBorderColor;
    }


    /**
     * 开始画色
     */
    public void starDrawColor(int colorId, float startPercent, float endPercent) {
        if (horizontalPlayViewOne != null) {
            horizontalPlayViewOne.setOnColorRectListChangeListener(new HorizontalPlayViewOne
                    .OnColorRectListChangeListener() {
                @Override
                public void OnColorRectListChange(int num) {
                    if (mOnDrawRectListChangeListener != null) {
                        mOnDrawRectListChangeListener.onDrawRectListChange(num);
                    }
                }
            });
            horizontalPlayViewOne.startDrawColorRect(colorId, startPercent, endPercent);
        }
    }

    /**
     * 开始画色
     */
    public void starDrawColor(int colorId, long startTimeUs, long endTimeUs) {
        float startPercent = startTimeUs / (float) mTotalTimeUs;
        float endPercent = endTimeUs / (float) mTotalTimeUs;
        starDrawColor(colorId, startPercent, endPercent);
    }

    /**
     * 删除画色
     */
    public void removeColor() {
        if (horizontalPlayViewOne == null) return;
        horizontalPlayViewOne.removeColorRect();
        horizontalPlayViewOne.setOnColorRectListChangeListener(new HorizontalPlayViewOne
                .OnColorRectListChangeListener() {
            @Override
            public void OnColorRectListChange(int num) {
                if (mOnDrawRectListChangeListener != null) {
                    mOnDrawRectListChangeListener.onDrawRectListChange(num);
                }
            }
        });
    }

    /**
     * 设置左 bar位置
     *
     * @param percent
     */
    public void setLeftBarPosition(float percent) {
        mLeftStartTimePercent = percent;
        mLeftStartTimeUs = (long) (mTotalTimeUs * mLeftStartTimePercent);
        mSelectTimeUs = mRightEndTimeUs - mLeftStartTimeUs;
        mSelectTimePercent = mSelectTimeUs / (float) mTotalTimeUs;
        if (mRangeSelectionBar != null)
            mRangeSelectionBar.setLeftBarPosition(mLeftStartTimePercent);
    }

    /**
     * 设置左 bar位置
     *
     * @param leftTimeUs
     */
    public void setLeftBarPosition(long leftTimeUs) {
        if (leftTimeUs < 0) leftTimeUs = 0;
        mLeftStartTimeUs = leftTimeUs;
        mLeftStartTimePercent = mLeftStartTimeUs / (float) mTotalTimeUs;
        mSelectTimeUs = mRightEndTimeUs - mLeftStartTimeUs;
        mSelectTimePercent = mSelectTimeUs / (float) mTotalTimeUs;
        if (mRangeSelectionBar != null)
            mRangeSelectionBar.setLeftBarPosition(mLeftStartTimePercent);
    }

    /**
     * 设置右 bar位置
     *
     * @param rightTimeUs
     */
    public void setRightBarPosition(long rightTimeUs) {

        if (rightTimeUs > mTotalTimeUs) rightTimeUs = mTotalTimeUs;
        mRightEndTimeUs = rightTimeUs;
        mRightEndTimePercent = mRightEndTimeUs / (float) mTotalTimeUs;
        mSelectTimeUs = mRightEndTimeUs - mLeftStartTimeUs;
        mSelectTimePercent = mSelectTimeUs / (float) mTotalTimeUs;
        if (mRangeSelectionBar != null)
            mRangeSelectionBar.setRightBarPosition(mRightEndTimePercent);
    }

    /**
     * 设置右 bar位置
     *
     * @param percent
     */
    public void setRightBarPosition(float percent) {
        mRightEndTimePercent = percent;
        mRightEndTimeUs = (long) (mTotalTimeUs * mRightEndTimePercent);
        mSelectTimeUs = mRightEndTimeUs - mLeftStartTimeUs;
        mSelectTimePercent = mSelectTimeUs / (float) mTotalTimeUs;
        if (mRangeSelectionBar != null)
            mRangeSelectionBar.setRightBarPosition(mRightEndTimePercent);
    }


    /**
     * 获取左bar位置对应的时间
     *
     * @return
     */
    public long getLeftStartTime() {
        return mLeftStartTimeUs;
    }

    /**
     * 获取左bar位置对应的百分比
     *
     * @return
     */
    public float getLeftStartTimePercent() {
        return mLeftStartTimePercent;
    }

    /**
     * 获取右bar位置对应的百分比
     *
     * @return
     */
    public float getRightEndTimePercent() {
        return mRightEndTimePercent;
    }

    /**
     * 获取右bar位置对应的时间
     *
     * @return
     */
    public long getRightEndTime() {
        return mRightEndTimeUs;
    }

    /**
     * 获取控件选择的时间范围长度
     *
     * @return
     */
    public long getSelectTimeUs() {
        return mSelectTimeUs;
    }

    /**
     * 获取控件选择的时间范围百分比
     *
     * @return
     */
    public float getSelectTimePercent() {
        return mSelectTimePercent;
    }

    /**
     * 停止画色
     */
    public void stopDrawColor() {
        if (horizontalPlayViewOne == null) return;
        horizontalPlayViewOne.stopDrawColorRect();
    }


    private int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }


    public void setShowRangeBar(boolean isShow) {
        this.isShowRangeBar = isShow;
        if (mRangeSelectionBar == null) return;
        mRangeSelectionBar.setShowBar(isShow);
    }


    public boolean isShowRangeBar() {
        return isShowRangeBar;
    }

    /**
     * 获取触摸状态的方法
     *
     * @return
     */
    public boolean getTouchingState() {
        return mIsTouching;
    }


    /**
     * 设置左右bar的最小间距 默认0
     *
     * @param twoBarsDistance
     */
    private int mTwoBarsMinDistance = 0;

    public void setTwoBarsMinDistance(int twoBarsDistance) {
        this.mTwoBarsMinDistance = twoBarsDistance;
    }

    public void addBitmap(Bitmap bitmap){
        horizontalPlayViewOne.addBitmap(bitmap);
    }

    public void setBitmapCount(int bitmapCount){
        mBitmapCount = bitmapCount;
    }

    public long getTotalTimeUs(){
        return mTotalTimeUs;
    }

    /**  **/
    public void setReversPlayer(boolean isReversePlayer){
        if(horizontalPlayViewOne == null)return;
        horizontalPlayViewOne.setReversePlay(isReversePlayer);
    }
}
