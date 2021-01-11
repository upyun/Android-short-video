package org.lasque.tusdkvideodemo.views.cosmetic;

import android.content.Context;

import org.lasque.tusdk.core.seles.SelesParameters;
import org.lasque.tusdk.video.editor.TuSdkMediaCosmeticEffectData;
import org.lasque.tusdkvideodemo.views.cosmetic.panel.BasePanel;
import org.lasque.tusdkvideodemo.views.cosmetic.panel.blush.BlushPanel;
import org.lasque.tusdkvideodemo.views.cosmetic.panel.eyebrow.EyebrowPanel;
import org.lasque.tusdkvideodemo.views.cosmetic.panel.eyelash.EyelashPanel;
import org.lasque.tusdkvideodemo.views.cosmetic.panel.eyeliner.EyelinerPanel;
import org.lasque.tusdkvideodemo.views.cosmetic.panel.eyeshadow.EyeshadowPanel;
import org.lasque.tusdkvideodemo.views.cosmetic.panel.lipstick.LipstickPanel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * TuSDK
 * org.lasque.tusdkvideodemo.views.cosmetic
 * droid-sdk-video-refresh
 *
 * @author H.ys
 * @Date 2020/10/20  11:18
 * @Copyright (c) 2020 tusdk.com. All rights reserved.
 */
public class CosmeticPanelController {
    public static HashMap<String, Float> mDefaultCosmeticPercentParams = new HashMap<String, Float>() {
        {
            put("lipAlpha",0.4f);
            put("blushAlpha",0.5f);
            put("eyebrowAlpha",0.4f);
            put("eyeshadowAlpha",0.5f);
            put("eyelineAlpha",0.5f);
            put("eyelashAlpha",0.5f);
        }
    };

    private static HashMap<String,Float> mDefaultCosmeticMaxPercentParams = new HashMap<String, Float>(){
        {
            put("lipAlpha",0.8f);
            put("blushAlpha",1.0f);
            put("eyebrowAlpha",0.7f);
            put("eyeshadowAlpha",1.0f);
            put("eyelineAlpha",1.0f);
            put("eyelashAlpha",1.0f);
        }
    };

    /**
     * 口红列表
     */
    public static List<CosmeticTypes.LipstickType> mLipstickTypes = Arrays.asList(CosmeticTypes.LipstickType.values());

    /**
     * 睫毛列表
     */
    public static List<CosmeticTypes.EyelashType> mEyelashTypes = Arrays.asList(CosmeticTypes.EyelashType.values());

    /**
     * 眉毛列表
     */
    public static List<CosmeticTypes.EyebrowType> mEyebrowTypes = Arrays.asList(CosmeticTypes.EyebrowType.values());

    /**
     * 腮红列表
     */
    public static List<CosmeticTypes.BlushType> mBlushTypes = Arrays.asList(CosmeticTypes.BlushType.values());

    /**
     * 眼影类型
     */
    public static List<CosmeticTypes.EyeshadowType> mEyeshadowTypes = Arrays.asList(CosmeticTypes.EyeshadowType.values());

    /**
     * 眼线类型
     */
    public static List<CosmeticTypes.EyelinerType> mEyelinerTypes = Arrays.asList(CosmeticTypes.EyelinerType.values());



    private TuSdkMediaCosmeticEffectData mEffect = new TuSdkMediaCosmeticEffectData();

    private Context mContext;

    public CosmeticPanelController(Context context){
        this.mContext = context;
        for (String key : mDefaultCosmeticMaxPercentParams.keySet()){
            SelesParameters.FilterArg arg = mEffect.getFilterArg(key);
            arg.setMaxValueFactor(mDefaultCosmeticMaxPercentParams.get(key));
        }

    }

    public LipstickPanel getLipstickPanel() {
        if (mLipstickPanel == null){
            mLipstickPanel = new LipstickPanel(this);
        }
        return mLipstickPanel;
    }

    public BlushPanel getBlushPanel() {
        if (mBlushPanel == null){
            mBlushPanel = new BlushPanel(this);
        }
        return mBlushPanel;
    }

    public EyebrowPanel getEyebrowPanel() {
        if (mEyebrowPanel == null){
            mEyebrowPanel = new EyebrowPanel(this);
        }
        return mEyebrowPanel;
    }

    public EyeshadowPanel getEyeshadowPanel() {
        if (mEyeshadowPanel == null){
            mEyeshadowPanel = new EyeshadowPanel(this);
        }
        return mEyeshadowPanel;
    }

    public EyelinerPanel getEyelinerPanel() {
        if (mEyelinerPanel == null){
            mEyelinerPanel = new EyelinerPanel(this);
        }
        return mEyelinerPanel;
    }

    public EyelashPanel getEyelashPanel() {
        if (mEyelashPanel == null){
            mEyelashPanel = new EyelashPanel(this);
        }
        return mEyelashPanel;
    }

    public BasePanel getPanel(CosmeticTypes.Types types){
        BasePanel panel = null;
        switch (types){
            case Lipstick:
                panel = getLipstickPanel();
                break;
            case Blush:
                panel = getBlushPanel();
                break;
            case Eyebrow:
                panel = getEyebrowPanel();
                break;
            case Eyeshadow:
                panel = getEyeshadowPanel();
                break;
            case Eyeliner:
                panel = getEyelinerPanel();
                break;
            case Eyelash:
                panel = getEyelashPanel();
                break;
        }
        return panel;
    }

    private LipstickPanel mLipstickPanel;
    private BlushPanel mBlushPanel;
    private EyebrowPanel mEyebrowPanel;
    private EyeshadowPanel mEyeshadowPanel;
    private EyelinerPanel mEyelinerPanel;
    private EyelashPanel mEyelashPanel;


    public Context getContext(){
        return mContext;
    }

    public TuSdkMediaCosmeticEffectData getEffect(){
        return mEffect;
    }

    public void setPanelClickListener(BasePanel.OnPanelClickListener listener){
        getLipstickPanel().setOnPanelClickListener(listener);
        getBlushPanel().setOnPanelClickListener(listener);
        getEyebrowPanel().setOnPanelClickListener(listener);
        getEyeshadowPanel().setOnPanelClickListener(listener);
        getEyelinerPanel().setOnPanelClickListener(listener);
        getEyelashPanel().setOnPanelClickListener(listener);
    }

    public void clearAllCosmetic(){
        for (CosmeticTypes.Types type : CosmeticTypes.Types.values()){
            getPanel(type).clear();
        }
    }

}
