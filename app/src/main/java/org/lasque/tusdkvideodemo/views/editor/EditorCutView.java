package org.lasque.tusdkvideodemo.views.editor;

import android.graphics.Bitmap;

import android.widget.TextView;

import org.lasque.tusdk.core.utils.TLog;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.editor.MovieEditorCutActivity;
import org.lasque.tusdkvideodemo.views.editor.playview.TuSdkMovieScrollContent;
import org.lasque.tusdkvideodemo.views.editor.playview.TuSdkRangeSelectionBar;
import org.lasque.tusdkvideodemo.views.editor.ruler.RulerView;

import java.util.List;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/21 15:21
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 编辑里选取裁剪的View
 */
public class EditorCutView {
    //时间选择
    private TuSdkMovieScrollContent mRangeView;
    //刻度
    private RulerView mRulerView;
    private TextView mTimeRangView;
    private SpeedView mPlayingSpeedView;
    //最小裁剪时间
    private long mMinCutTimeUs =  3 * 1000000;
    private boolean isEnable = true;
    private MovieEditorCutActivity mActivity;
    private float mCurrentRangTime = 0f;


    public EditorCutView(MovieEditorCutActivity activity) {
        this.mActivity = activity;
        init();
    }



    /** 获取布局Id */
    protected int getLayoutId() {
        return R.layout.editor_cut_view;
    }

    /** 初始化View */
    private void init() {
        mRangeView = mActivity.findViewById(R.id.lsq_range_line);
        mRangeView.setType(1);
        mRangeView.setShowSelectBar(true);
        mRangeView.setNeedShowCursor(true);
        mRulerView = mActivity.findViewById(R.id.lsq_rule_view);
        mTimeRangView = mActivity.findViewById(R.id.lsq_range_time);
        mPlayingSpeedView = mActivity.findViewById(R.id.lsq_playing_speed_bar);
    }

    private int mTwoBarsDistance = 0;
    public void setTwoBarsMinDistance(int twoBarsDistance){
        this.mTwoBarsDistance = twoBarsDistance;
//        mRangeView.setTwoBarsMinDistance(twoBarsDistance);
    }

    public void setMinCutTimeUs(float timeUs){
        mRangeView.setMinWidth(timeUs);
    }

    /**
     * 设置时间区间
     * @param times
     */
    public void setRangTime(float times){
        mCurrentRangTime = times;
        String rangeTime = String.format("%s %.1f %s",mActivity.getResources().getString(R.string.lsq_movie_cut_selecttime),times,"s");
        mTimeRangView.setText(rangeTime);
    }

    public void setSpeedChangeRangTime(float times){
        String rangeTime = String.format("%s %.1f %s",mActivity.getResources().getString(R.string.lsq_movie_cut_selecttime),times,"s");
        mTimeRangView.setText(rangeTime);
    }

    public float getRangTime(){
        return mCurrentRangTime;
    }

    /**
     * 设置封面图
     * @param coverList 封面图列表
     */
    public void setCoverList(List<Bitmap> coverList){
        if(coverList == null){
            TLog.e(" bitmap list of cover is null !!!");
            return;
        }
//        mRangeView.setBitmapList(coverList);
//        mRangeView.setMinSelectTimeUs(mMinCutTimeUs);
    }

    /**
     * 设置视频的总时长
     * @param totalTimeUs
     */
    public void setTotalTime(long totalTimeUs){
        if(totalTimeUs <= 0)
        {
            TLog.e(" video time length mast > 0  !!!");
            return;
        }
        mRulerView.setMaxValueAndPaintColor(totalTimeUs, mActivity.getResources().getColor(R.color.lsq_color_white));
    }

    /**
     * 设置选择区间回调
     * @param onSelectTimeChangeListener
     */
    public void setOnSelectCeoverTimeListener(TuSdkRangeSelectionBar.OnSelectRangeChangedListener onSelectTimeChangeListener){
        if(onSelectTimeChangeListener == null)
        {
            TLog.e("setSelectCoverTimeListener is null !!!");
            return;
        }
        mRangeView.setSelectRangeChangedListener(onSelectTimeChangeListener);
    }

    /**
     * 播放指针 位置改变监听
     * @param progressChangeListener
     */
    public void setOnPlayPointerChangeListener(TuSdkMovieScrollContent.OnPlayProgressChangeListener progressChangeListener){
        if(progressChangeListener == null){
            TLog.e("setSelectCoverTimeListener is null !!!");
            return;
        }
        mRangeView.setProgressChangeListener(progressChangeListener);
    }


    /**
     * 设置播放进度
     * @param percent 播放进度的百分比
     */
    public void setVideoPlayPercent(float percent){
        if(mRangeView == null) return;
        if(percent < 0){
            TLog.e("setSelectCoverTimeListener is null !!!");
            return;
        }
        mRangeView.setPercent(percent);
    }

    public TuSdkMovieScrollContent getLineView(){
        return mRangeView;
    }

    public void addBitmap(Bitmap bitmap) {
        mRangeView.addBitmap(bitmap);
    }

    /** 设置是否启用 **/
    public void setEnable(boolean isEnable){
        this.isEnable = isEnable;
        mRangeView.setEnable(isEnable);
    }

    public void setOnPlayingSpeedChangeListener(SpeedView.OnPlayingSpeedChangeListener listener){
        if (mPlayingSpeedView == null) return;
        mPlayingSpeedView.setPlayingSpeedChangeListener(listener);
    }


}
