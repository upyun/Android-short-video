package com.upyun.shortvideo.views;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.video.editor.TuSDKMediaParticleEffectData;

/**
 * Created by tutu on 2018/5/3.
 * 魔法特效信息 （继承 TuSDKMediaParticleEffectData）
 */

public class MagicEffectModel extends TuSDKMediaParticleEffectData implements EffectsTimelineView.EffectModelInterface
{
    private int mColor;

    public MagicEffectModel(String particleCode)
    {
        super(particleCode);
    }

    @Override
    public int getLabelColor()
    {
        if (mColor > 0) return mColor;

        return mColor = TuSdkContext.getColor(TuSdkContext.getColorResId("lsq_margic_effect_color_"+getParticleCode()));
    }

}