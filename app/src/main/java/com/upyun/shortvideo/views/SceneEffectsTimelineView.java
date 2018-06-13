package com.upyun.shortvideo.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.video.editor.TuSDKMediaSceneEffectData;

/**
 * Created by sprint on 26/12/2017.
 */

public class SceneEffectsTimelineView extends EffectsTimelineView<SceneEffectModel>
{
    /**
     * 初始化 SceneEffectsTimelineView
     *
     * @param context
     * @param attrs
     */
    public SceneEffectsTimelineView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 场景特效信息
     */
    public static class SceneEffectModel extends TuSDKMediaSceneEffectData implements EffectModelInterface
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
}
