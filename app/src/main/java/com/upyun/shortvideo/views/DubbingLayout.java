package com.upyun.shortvideo.views;

import android.content.Context;
import android.util.AttributeSet;

import org.lasque.tusdk.core.view.TuSdkLinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sprint
 * @Date: 25/02/2018
 * @Copyright: (c) 2018 tusdk.com. All rights reserved.
 * @Description 配音视图
 */
public class DubbingLayout extends TuSdkLinearLayout
{
    /** 配音音效列表 */
    private AudioEffectListView mAudioEffectListView;

    public DubbingLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    @Override
    public void loadView() {
        super.loadView();


    }


    /**
     * 配音列表
     */
    public AudioEffectListView getDubbingListView()
    {
        if (mAudioEffectListView == null)
        {
            mAudioEffectListView = findViewById(com.upyun.shortvideo.R.id.lsq_mixing_list_view);
            mAudioEffectListView.loadView();
            mAudioEffectListView.setCellLayoutId(com.upyun.shortvideo.R.layout.movie_editor_audio_effect_cell_view);
//            mAudioEffectListView.setItemClickDelegate(mMixingTableItemClickDelegate);

            mAudioEffectListView.setModeList(getDubbingListData());
            mAudioEffectListView.reloadData();

        }

        return mAudioEffectListView;
    }

    /**
     * 配音数据列表
     * @return
     */
    private List<AudioEffectCellView.AudioEffectEntity> getDubbingListData()
    {
        List<AudioEffectCellView.AudioEffectEntity> groups = new ArrayList<AudioEffectCellView.AudioEffectEntity>();

        String[][] mixingStrings = new String[][]{{"0","nosound"},{"0","soundrecording"},{"1","lively"},{"1","oldmovie"},{"1","relieve"}};

        for(int i = 0; i < mixingStrings.length; i++)
        {
            int type = Integer.parseInt(mixingStrings[i][0]);
            String name = mixingStrings[i][1];
            AudioEffectCellView.AudioEffectEntity audioEffectEntity = new AudioEffectCellView.AudioEffectEntity(type,name);
            groups.add(audioEffectEntity);
        }
        return groups;
    }

}
