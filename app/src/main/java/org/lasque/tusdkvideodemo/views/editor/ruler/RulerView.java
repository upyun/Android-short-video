package org.lasque.tusdkvideodemo.views.editor.ruler;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import org.lasque.tusdkvideodemo.views.editor.CUtils;

/**
 * 底部时间刻度控件
 */
public class RulerView extends View {

    /** 最大刻度 */
    protected int mMax;

    /** 刻度间距 */
    protected float mScaleMargin;

    /** 刻度线的高度 */
    protected int mScaleHeight;

    /** 整刻度线高度 */
    protected int mScaleMaxHeight;
    /** 总宽度 */
    protected int mRectWidth;
    /** 高度 */
    protected int mRectHeight;
    /* 左右间隔的距离 */
    private int startLineX = CUtils.dip2px(20);
    /** 总共需要画的刻度数目 */
    private int mCount = 0;
    /** 倍数 */
    private int mMultiple = 0;

    private int mScreenWidth;
    Paint mPaint = new Paint();
    Paint mBoldPaint = new Paint();
    Paint mTextPaint = new Paint();

    /** 控件的高度 */
    private int mViewHeight = 0;
    /** 控件的宽度 */
    private int mViewWidth = 0;

    public RulerView(Context context) {
        super(context);
        getScreenWidth(context);
    }

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getScreenWidth(context);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getScreenWidth(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RulerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getScreenWidth(context);
    }

    private void getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        mScreenWidth = displaymetrics.widthPixels;
    }

    private void init() {
        // 画笔
        mPaint.setColor(mColorId);
        // 抗锯齿
        mPaint.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mPaint.setDither(true);
        // 空心
        mPaint.setStyle(Paint.Style.STROKE);
        // 文字居中
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStrokeWidth(5);

        mBoldPaint.setColor(mColorId);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mBoldPaint.setDither(true);
        mBoldPaint.setStrokeWidth(6);

        // 抗锯齿
        mTextPaint.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mTextPaint.setDither(true);
        mTextPaint.setColor(mColorId);
        // 空心
        mTextPaint.setStyle(Paint.Style.STROKE);
        // 文字居中
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(30);

        mMax = getMaxValue();
        mRectWidth = mScreenWidth - startLineX - startLineX;

    }


    private int getMaxValue() {
        if (getMaxValueFloat() > 10f) {
            mMultiple = getMultiple(getMaxValueFloat());
            return Math.round(getSecond(getMaxValueFloat(), mMultiple) * 10);
        } else {
            mMultiple = 0;
            return Math.round(getMaxValueFloat() * 10);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);

        mScaleMargin = getMarginValue();
        mRectHeight = mViewHeight;
        mScaleMaxHeight = mViewHeight / 3;
        mScaleHeight = mScaleMaxHeight /2;

        mTextPaint.setTextSize(mViewHeight / 3);
    }

    private float getMaxValueFloat() {
        return mMaxValue;
    }

    private float mMaxValue = 5f;
    private int mColorId = Color.BLACK;

    /**
     * 设置刻度尺总秒数
     *
     * @param maxTimeUs
     * @param colorId   刻度颜色
     */
    public void setMaxValueAndPaintColor(long maxTimeUs, int colorId) {
        mMaxValue = maxTimeUs / (float) 1000000;
        mColorId = colorId;
        init();
    }


    //大于10秒时  10*n*5>= second
    private int getMultiple(float value) {
        int multiple = (int) value / 50;
        if (value % 50 > 0f) {
            return multiple + 1;
        } else {
            return multiple;
        }
    }


    /**
     * 秒数大于10秒时 获得对应的0-10秒
     *
     * @param secondInput
     * @param multipe
     * @return
     */
    private float getSecond(float secondInput, int multipe) {
        int secInt = (int) secondInput / (multipe * 5);
        int secTow = 0;
        if(multipe > 1){
            int secDw = (int) (secondInput - secInt * (multipe * 5));
            secTow = secDw ;
        }else {
            secTow = (int) ((secondInput - multipe * 5 * secInt) * 10 / 5);
        }
        String secStr = secInt + "." + secTow;
        float retrunSec = Float.valueOf(secStr).floatValue();
        return retrunSec;
    }


    /**
     * 获取刻度间距
     *
     * @return
     */
    private float getMarginValue() {
        if (getMaxValueFloat() > 10f) {
            //画多少个刻度
            int nValue = (int) (getSecond(getMaxValueFloat(), mMultiple) * 5) + 1;
            mCount = nValue;
            return mRectWidth / (nValue - 1);
        } else {
            //画多少个刻度
            int nValue = (int) (getMaxValueFloat() * 5) + 1;
            mCount = nValue;
            return mRectWidth / (nValue - 1);
        }
    }

    /**
     * 将秒数转为时分秒
     *
     * @param second
     * @return
     */
    private String changeSec(int second) {
        int h = 0;
        int m = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    m = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            m = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }

        if (h == 0) {
            if (m == 0) {
                return s + "";
            } else {
                return m + ":" + s;
            }
        } else {
            return h + ":" + m + ":" + s + "";
        }
    }

    /**
     * 将秒数转为分秒
     *
     * @param second
     * @return String
     */
    private String changeSecToMS(int second) {
        int m = 0;
        int s = 0;

        if (second > 60) {
            m = second / 60;
            s = second % 60;
        } else {
            s = second;
        }

        if (m == 0) {
            return s + "";
        } else {
            return m + ":" + s;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画刻度
        onDrawScale(canvas, mPaint, mBoldPaint, mTextPaint);

        invalidate();
    }


    /**
     * 画刻度
     *
     * @param canvas
     * @param paint
     * @param boldPaint
     * @param textPaint
     */
    protected void onDrawScale(Canvas canvas, Paint paint, Paint boldPaint, Paint textPaint) {
        paint.setTextSize(mRectHeight / 4);

        for (int i = 0, k = 0; i < mCount; i++) {
            if (mMultiple == 0) {
                if ((i - 1) * 0.2 > getMaxValueFloat()) return;
            } else {
                if ((i - 1) * mMultiple > getMaxValueFloat()) return;
            }

            if (i % 5 == 0) { //整值
                canvas.drawLine(i * mScaleMargin + startLineX, mRectHeight, i * mScaleMargin
                        + startLineX, mRectHeight - mScaleMaxHeight, boldPaint);
                if (k == 0 && mMultiple != 0) {
                    //整值文字
                    canvas.drawText(changeSecToMS(k / 5), i * mScaleMargin + startLineX,
                            mRectHeight - mScaleMaxHeight - 20, textPaint);
                } else {
                    if (mMultiple == 0) {
                        //整值文字
                        canvas.drawText(changeSecToMS(k / 5), i * mScaleMargin + startLineX,

                                mRectHeight - mScaleMaxHeight - 20, textPaint);
                    } else {
                        canvas.drawText(changeSecToMS(k * mMultiple * 5 / 5), i * mScaleMargin +
                                        startLineX,

                                mRectHeight - mScaleMaxHeight - 20, textPaint);
                    }
                }
                k += 5;
            } else {
                canvas.drawLine(i * mScaleMargin + startLineX, mRectHeight, i * mScaleMargin
                        + startLineX, mRectHeight - mScaleHeight, paint);
            }
        }

    }
}
