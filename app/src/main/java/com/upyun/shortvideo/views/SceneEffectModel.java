package com.upyun.shortvideo.views;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.video.editor.TuSDKMediaSceneEffectData;

/**
 * Created by tutu on 2018/5/3.
 * 场景特效信息
 */

public class SceneEffectModel extends TuSDKMediaSceneEffectData implements EffectsTimelineView.EffectModelInterface
{
    private int mColor;

    public SceneEffectModel(String effectCode)
    {
        super(effectCode);
    }

    @Override
    public int getLabelColor()
    {
        if (mColor > 0) return 0;

        return mColor = TuSdkContext.getColor(TuSdkContext.getColorResId("lsq_scence_effect_color_"+getEffectCode()));
    }

}