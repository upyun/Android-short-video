package com.upyun.shortvideo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditorImpl;
import org.lasque.tusdk.core.view.TuSdkRelativeLayout;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;
import org.lasque.tusdk.video.editor.TuSDKMediaRepeatTimeEffect;
import org.lasque.tusdk.video.editor.TuSDKMediaReversalTimeEffect;
import org.lasque.tusdk.video.editor.TuSDKMediaSlowTimeEffect;
import org.lasque.tusdk.video.editor.TuSDKTimeRange;
import com.upyun.shortvideo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ligh
 * @Date: 27/03/2018
 * @Copyright: (c) 2018 tusdk.com. All rights reserved.
 * @Description
 */
public class TimeEffectsLayout extends TuSdkRelativeLayout {

    private TuSdkMovieEditorImpl mMovieEditor;
    private TimeEffectListView mTimeEffectListView;
    private MovieRangScrollerBar mScrollerBar;
    private int mCurrentPosition = 0;

    public TimeEffectsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMovieEditor(TuSdkMovieEditorImpl movieEditor) {
        this.mMovieEditor = movieEditor;
    }

    /**
     * 加载视图
     */
    @Override
    public void loadView() {
        super.loadView();

        getTimeEffectListView();

        if (mTimeEffectListView == null) return;

        List<String> timeModels = new ArrayList<>();
        timeModels.add("no");
        timeModels.add("reverse");
        timeModels.add("repeat");
        timeModels.add("slow");

        mTimeEffectListView.setModeList(timeModels);
    }

    /**
     * 滤镜栏视图
     *
     * @return
     */
    private TimeEffectListView getTimeEffectListView() {
        if (mTimeEffectListView == null) {
            mTimeEffectListView = (TimeEffectListView) findViewById(R.id.lsq_time_effect_list_view);
            mScrollerBar = findViewById(R.id.lsq_time_scroller_bar);

            if (mTimeEffectListView == null) return null;

            mTimeEffectListView.loadView();
            mTimeEffectListView.setCellLayoutId(R.layout.time_effect_list_cell_view);
            mTimeEffectListView.setCellWidth(TuSdkContext.dip2px(62));
            mTimeEffectListView.setItemClickDelegate(mTimeEffectTableItemClickDelegate);
            mTimeEffectListView.reloadData();
            mTimeEffectListView.selectPosition(0);
        }
        return mTimeEffectListView;
    }

    public void setVideoDuration() {
        mScrollerBar.setTimeRangChangedListener(mTimeRangChangedListener);
        mScrollerBar.setBlockRang((float) (0.5 / mMovieEditor.getEditorTransCoder().getVideoActualDuration()));
    }

    public void seek(float percent) {
        mScrollerBar.seek(percent);
    }

    public void setCoverImageList(List<Bitmap> bitmapList) {
        mScrollerBar.drawVideoThumbList(bitmapList);
    }

    private TuSDKTimeRange mTimeRange = TuSDKTimeRange.makeTimeUsRange(0l, 500000);
    private MovieRangScrollerBar.OnTimeRangChangedListener mTimeRangChangedListener = new MovieRangScrollerBar.OnTimeRangChangedListener() {
        @Override
        public void onTimeRangChanged(float leftPercent, float rightPercent) {
            mTimeRange.setStartTimeUs((long) (mMovieEditor.getEditorPlayer().getTotalTimeUS() * leftPercent));
            mTimeRange.setEndTimeUs((long) (mMovieEditor.getEditorPlayer().getTotalTimeUS() * rightPercent));
            handleTimeEffect(mCurrentPosition);
        }
    };

    /**
     * MV 列表点击事件
     */
    private TuSdkTableView.TuSdkTableViewItemClickDelegate<String, TimeEffectCellView> mTimeEffectTableItemClickDelegate = new TuSdkTableView.TuSdkTableViewItemClickDelegate<String, TimeEffectCellView>() {
        @Override
        public void onTableViewItemClick(final String itemData, TimeEffectCellView itemView, final int position) {
            if (TuSdkViewHelper.isFastDoubleClick()) return;

            getTimeEffectListView().selectPosition(position);
            mCurrentPosition = position;
            handleTimeEffect(position);

        }
    };

    private void handleTimeEffect(int position) {
        // TODO 处理时间特效
        switch (position) {
            case 0:
                mMovieEditor.getEditorPlayer().clearTimeEffect();
                break;
            case 1:
                //时光倒流
                TuSDKMediaReversalTimeEffect reversalTimeEffect = new TuSDKMediaReversalTimeEffect();
                reversalTimeEffect.setTimeRange(0,mMovieEditor.getEditorPlayer().getTotalTimeUS());
                mMovieEditor.getEditorPlayer().setTimeEffect(reversalTimeEffect);
                break;
            case 2:
                //反复
                TuSDKMediaRepeatTimeEffect repeatTimeEffect = new TuSDKMediaRepeatTimeEffect();
                repeatTimeEffect.setTimeRange(mTimeRange.getStartTimeUS(), mTimeRange.getEndTimeUS());
                repeatTimeEffect.setRepeatCount(2);
                repeatTimeEffect.setDropOverTime(true);
                mMovieEditor.getEditorPlayer().setTimeEffect(repeatTimeEffect);
                break;
            case 3:
                //慢动作
                TuSDKMediaSlowTimeEffect slowTimeEffect = new TuSDKMediaSlowTimeEffect();
                slowTimeEffect.setTimeRange(mTimeRange.getStartTimeUS(), mTimeRange.getEndTimeUS());
                slowTimeEffect.setSpeed(0.6f);
                mMovieEditor.getEditorPlayer().setTimeEffect(slowTimeEffect);
                break;
        }
    }

}
