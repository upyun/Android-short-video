/**
 *  TuSDK
 *  TuSDKVideoDemo
 *  SpeedView.java
 *  @author  H.ys
 *  @Date    2019/5/30 18:42
 *  @Copyright 	(c) 2019 tusdk.com. All rights reserved.
 *
 *
 */
package org.lasque.tusdkvideodemo.views.editor;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.lasque.tusdk.core.utils.hardware.TuSdkRecorderVideoCamera;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import com.upyun.shortvideo.R;


/**
 * 播放速度切换控件
 */
public class SpeedView extends RelativeLayout {


    /**
     * 播放速度控制回调
     */
    public interface OnPlayingSpeedChangeListener{

        void onChanged(float speed);
    }

    public void setPlayingSpeedChangeListener(OnPlayingSpeedChangeListener mPlayingSpeedChangeListener) {
        this.mPlayingSpeedChangeListener = mPlayingSpeedChangeListener;
    }

    /**
     * 播放速度控制回调
     */
    private OnPlayingSpeedChangeListener mPlayingSpeedChangeListener;

    /**
     * 播放速度控制Bar
     */
    private LinearLayout mSpeedBarWrap;

    private OnClickListener mSpeedBarOnClickListener = new TuSdkViewHelper.OnSafeClickListener() {
        @Override
        public void onSafeClick(View v) {
            for (int i=0;i<mSpeedBarWrap.getChildCount();i++){
                Button view = (Button) mSpeedBarWrap.getChildAt(i);
                view.setTextColor(getContext().getResources().getColor(R.color.lsq_color_white));
                view.setBackgroundResource(0);
                view.setBackgroundColor(Color.parseColor("#00000000"));
            }
            ((Button) v).setTextColor(getContext().getResources().getColor(R.color.lsq_editor_cut_select_font_color));
            v.setBackgroundResource(R.drawable.tusdk_edite_cut_speed_button_bg);
            if (mPlayingSpeedChangeListener == null) return;
            TuSdkRecorderVideoCamera.SpeedMode speedMode = TuSdkRecorderVideoCamera.SpeedMode.values()[Integer.parseInt((String)v.getTag())];
            mPlayingSpeedChangeListener.onChanged(speedMode.getSpeedRate());
        }
    };

    public SpeedView(Context context) {
        this(context,null);
    }

    public SpeedView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SpeedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        mSpeedBarWrap = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.lsq_editor_speed_bar, null);
        for (int i=0;i<mSpeedBarWrap.getChildCount();i++){
            View view = mSpeedBarWrap.getChildAt(i);
            view.setOnClickListener(mSpeedBarOnClickListener);
        }
        LayoutParams speedViewWrap = new RelativeLayout.LayoutParams(context,attrs);
        addView(mSpeedBarWrap,speedViewWrap);
    }
}
