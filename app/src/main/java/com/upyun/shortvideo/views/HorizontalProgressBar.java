package com.upyun.shortvideo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import org.lasque.tusdk.core.TuSdkContext;
import com.upyun.shortvideo.R;

import java.util.LinkedList;

/**
 * @author xujie
 * @Date 2018/11/12
 */

public class HorizontalProgressBar extends View {

    // 记录每次暂停时的进度
    private LinkedList<Float> mPauseProgressList;
    // 进度值
    private float mProgress;

    private Paint mProgressPaint;
    private Paint mBackgroundPaint;
    // 进度条颜色
    private int mProgressColor;
    // 进度条背景色
    private int mBackgroundColor;
    // 默认高度
    private float mDefaultHeight;

    public HorizontalProgressBar(Context context) {
        this(context,null);
    }

    public HorizontalProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HorizontalProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
        init();
    }

    private void init(){
        mPauseProgressList = new LinkedList<>();
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(mProgressColor);
        mBackgroundPaint = new Paint(mBackgroundColor);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBackgroundColor);
    }

    private void getAttrs(Context context,AttributeSet attrs){
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBar);
        mProgressColor = attributes.getColor(R.styleable.HorizontalProgressBar_progressColor,Color.BLACK);
        mBackgroundColor = attributes.getColor(R.styleable.HorizontalProgressBar_backgrounds,Color.WHITE);
        mDefaultHeight = attributes.getDimension(R.styleable.HorizontalProgressBar_defaultHeight,TuSdkContext.dip2px(20f));
        attributes.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        canvas.drawRect(0f,0f,width,mDefaultHeight,mBackgroundPaint);
        canvas.drawRect(0f,0f,width * mProgress,mDefaultHeight,mProgressPaint);
    }

    /** 清除记录 */
    public void clearProgressList(){
        mPauseProgressList.clear();
        setProgress(0);
    }

    /** 获取当前录制记录数 */
    public int getRecordProgressListSize(){
        return mPauseProgressList.size();
    }

    /** 暂停录制 (将添加一个进度) **/
    public synchronized void pauseRecord(){
        if(mPauseProgressList == null)return;
        mPauseProgressList.addLast(getProgress());
    }

    /**
     * 移除上一个片段
     * @return true 删除成功  false 删除失败
     */
    public synchronized boolean removePreSegment() {
        if(mPauseProgressList == null || mPauseProgressList.size() == 0) return false;
        mPauseProgressList.removeLast();
        if(mPauseProgressList.size() != 0) setProgress(mPauseProgressList.getLast());
        else setProgress(0);
        return true;
    }

    public synchronized void setProgress(float progress){
        this.mProgress = progress;
        postInvalidate();
    }
    public synchronized float getProgress(){
        return mProgress;
    }
}
