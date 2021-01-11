package org.lasque.tusdkvideodemo.views.editor.color;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.lasque.tusdk.core.TuSdkContext;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.views.editor.CUtils;

/**
 * Created by tutu-penggao on 2018/9/18.
 */

public class ColorView extends View {

    /**
     * 颜色背景图片
     */
    private Bitmap mColorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable
            .edit_ic_colorbar);
    /**
     * 画图片的矩形
     */
    private RectF mRectBitmap;
    /**
     * 图片矩形高度
     */
    private int mBitmapHeight = CUtils.dip2px(3);
    /**
     * 圆的半径
     */
    private int mCircleRadius = CUtils.dip2px(4);
    /**
     * 图片矩形的 left坐标
     */
    private int mBitmapLeft = mCircleRadius;
    /**
     * 图片矩形的 top坐标
     */
    private int mBitmapTop = mCircleRadius;
    /**
     * 圆心的X坐标
     */
    private int mCircleX = mCircleRadius;
    /**
     * 圆心的Y坐标
     */
    private int mCircleY = mBitmapTop + mBitmapHeight / 2;
    /**
     * 外圆画笔的宽度
     */
    private int mOutCirclePaintStrokeWidth = 5;
    /**
     * 圆形球移动距离
     */
    private int moveX = 0;
    /**
     * 外圆(即边框)颜色
     */
    private int mOutCircleColor = Color.parseColor("#ffffff");
    /**
     * 内部圆的颜色 即颜色选择条选择的颜色
     */
    private int mInnerCircleColor = Color.argb(0, 0, 0, 0);
    private Paint mBitmapPaint;
    private Paint mPaintCircleOut;
    private Paint mPaintCircleInner;
    //无色范围  图片的24 / 760

    private Runnable mDrawRunable;

    public ColorView(Context context) {
        this(context,null);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBitmapPaint = new Paint();
        mRectBitmap = new RectF();
        mPaintCircleOut = new Paint();
        // 抗锯齿
        mPaintCircleOut.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mPaintCircleOut.setDither(true);
        // 空心 外圆
        mPaintCircleOut.setStyle(Paint.Style.STROKE);
        mPaintCircleOut.setStrokeWidth(mOutCirclePaintStrokeWidth);
        mPaintCircleInner = new Paint();
        // 抗锯齿
        mPaintCircleInner.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mPaintCircleInner.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mDrawRunable != null){
            mDrawRunable.run();
            mDrawRunable = null;
        }

        mRectBitmap.left = mBitmapLeft + mOutCirclePaintStrokeWidth;
        mRectBitmap.top = mBitmapTop + mOutCirclePaintStrokeWidth;
        mRectBitmap.right = getRight() - getLeft() - mOutCirclePaintStrokeWidth-mCircleRadius;
        mRectBitmap.bottom = mBitmapTop + mBitmapHeight + mOutCirclePaintStrokeWidth;
        //画颜色条
        canvas.drawBitmap(mColorBitmap, null, mRectBitmap, mBitmapPaint);
        mPaintCircleOut.setColor(mOutCircleColor);
        //画外圆
        canvas.drawCircle(mCircleX + mOutCirclePaintStrokeWidth + moveX, mCircleY +
                        mOutCirclePaintStrokeWidth,
                mCircleRadius, mPaintCircleOut);
        mPaintCircleInner.setColor(mInnerCircleColor);
        //画内圆
        canvas.drawCircle(mCircleX + mOutCirclePaintStrokeWidth + moveX, mCircleY +
                        mOutCirclePaintStrokeWidth,
                mCircleRadius, mPaintCircleInner);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();//获取触摸位置
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                if ((int) event.getX() < mCircleX) {
                    moveX = 0;
                } else if ((int) event.getX() > getRight() - mBitmapLeft - getLeft()-mCircleRadius) {
                    moveX = getRight() - mBitmapLeft - getLeft()-mCircleRadius*2 ;
                } else {
                    moveX = (int) event.getX();
                }

                if (moveX <= (getRight() - mBitmapLeft - getLeft()-mCircleRadius) * ((float) 2 / 505)) {
                    mInnerCircleColor = Color.argb(0, 0, 0, 0);
                } else {
                    int bitmapX = (int) ((x) * ((mColorBitmap.getWidth() / mRectBitmap.width())));
                    if (bitmapX >= mColorBitmap.getWidth()) {
                        bitmapX = mColorBitmap.getWidth() - 1;
                    }
                    int pixelBitmpa = mColorBitmap.getPixel(bitmapX, mColorBitmap.getHeight() - 1);
                    int resA = Color.alpha(pixelBitmpa);
                    int resR = Color.red(pixelBitmpa);
                    int resG = Color.green(pixelBitmpa);
                    int resB = Color.blue(pixelBitmpa);
                    mInnerCircleColor = Color.argb(resA, resR, resG, resB);
                }
                mPercent = moveX/(float)(getRight() - mBitmapLeft - getLeft()-mCircleRadius);
                if (mOnColorChangeListener != null){
                    mOnColorChangeListener.changeColor(mInnerCircleColor);
                    mOnColorChangeListener.changePosition(mPercent);
                }


                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                return false;
        }
        return super.onTouchEvent(event);
    }

    private float mPercent = 0;

    /**
     * 获取选中的位置百分比
     *
     * @return
     */

    public float getSelectPosition() {
        return mPercent;
    }

    /** 设置圆圈的大小(半径) **/
    public void setCircleRadius(int size){
        mCircleRadius = TuSdkContext.dip2px(size);
        mBitmapLeft = mCircleRadius;
        mBitmapTop = mCircleRadius;
        mCircleX = mCircleRadius;
        mCircleY = mBitmapTop + mBitmapHeight / 2;
        postInvalidate();
    }

    /**
     * 获取选中的颜色
     *
     * @return
     */
    public int getSelectColor() {
        return mInnerCircleColor;
    }

    //停止播放监听
    private OnColorChangeListener mOnColorChangeListener;
    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.mOnColorChangeListener = onColorChangeListener;
    }
    // 颜色改变监听
    public interface OnColorChangeListener {
        void changeColor(int colorId);
        void changePosition(float percent);
    }

    public void reset(){
        moveX = 0;
        mInnerCircleColor = Color.argb(0, 0, 0, 0);
        postInvalidate();
    }

    public void resetToEnd(){
        mDrawRunable = new Runnable() {
            @Override
            public void run() {
                moveX = getRight() - mBitmapLeft - getLeft()-mCircleRadius;
                mInnerCircleColor = Color.argb(0, 0, 0, 0);
                postInvalidate();
            }
        };
    }

    public void findColorString(String colorString){
        int color =  Color.parseColor(colorString);
        int move = 2;
        Bitmap colorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable
                .edit_ic_colorbar);
        for (int i = 2; i < colorBitmap.getWidth(); i++) {
            int pixelBitmpa = colorBitmap.getPixel(i , 2);
            int resA = Color.alpha(pixelBitmpa);
            int resR = Color.red(pixelBitmpa);
            int resG = Color.green(pixelBitmpa);
            int resB = Color.blue(pixelBitmpa);
            if((Math.abs(pixelBitmpa) - Math.abs(color))<5 && Math.abs(pixelBitmpa) - Math.abs(color) >= 0){
                mInnerCircleColor =  Color.argb(resA, resR, resG, resB);
                move = i;
            }
        }

        final int finalMove = move;
        mDrawRunable = new Runnable() {
            @Override
            public void run() {
                moveX = (int) ((getRight() - mBitmapLeft - getLeft()-mCircleRadius) * ((float) finalMove / 505));
                postInvalidate();
            }
        };
    }

    public void findColorInt(int  color){
        int move = 2;
        Bitmap colorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable
                .edit_ic_colorbar);
        for (int i = 2; i < colorBitmap.getWidth(); i++) {
            int pixelBitmpa = colorBitmap.getPixel(i , 2);
            int resA = Color.alpha(pixelBitmpa);
            int resR = Color.red(pixelBitmpa);
            int resG = Color.green(pixelBitmpa);
            int resB = Color.blue(pixelBitmpa);
//            TLog.e("find : %s   pixel : %s  i : %s",Math.abs(color),Math.abs(pixelBitmpa),i);
            if((Math.abs(pixelBitmpa) - Math.abs(color))<5 && Math.abs(pixelBitmpa) - Math.abs(color) >= 0){
                mInnerCircleColor =  Color.argb(resA, resR, resG, resB);
                move = i ;
            }
        }

        final int finalMove = move;
        mDrawRunable = new Runnable() {
            @Override
            public void run() {
                moveX = (int) ((getRight() - mBitmapLeft - getLeft()-mCircleRadius) * ((float) finalMove / 505));
                postInvalidate();
            }
        };
    }
}
