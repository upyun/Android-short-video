package com.upyun.shortvideo.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.video.editor.TuSDKMediaParticleEffectData;

/**
 * Created by sprint on 26/12/2017.
 */

public class MagicEffectsTimelineView extends EffectsTimelineView<MagicEffectModel>
{
    /**
     * 初始化 SceneEffectsTimelineView
     *
     * @param context
     * @param attrs
     */
    public MagicEffectsTimelineView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 魔法特效信息 （继承 TuSDKMediaParticleEffectData）
     */
    public static class MagicEffectModel extends TuSDKMediaParticleEffectData implements EffectModelInterface
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

}
