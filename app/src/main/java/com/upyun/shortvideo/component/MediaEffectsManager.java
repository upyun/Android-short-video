package com.upyun.shortvideo.component;

import com.upyun.shortvideo.views.MagicEffectModel;
import com.upyun.shortvideo.views.SceneEffectModel;

import org.lasque.tusdk.video.editor.TuSDKMediaAudioEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaStickerAudioEffectData;
import org.lasque.tusdk.video.editor.TuSDKMediaStickerEffectData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sprint
 * @Date: 27/02/2018
 * @Copyright: (c) 2018 tusdk.com. All rights reserved.
 * @Description 特效管理器
 */
public class MediaEffectsManager
{
    private static final MediaEffectsManager MEDIA_EFFECT_MANAGER = new MediaEffectsManager();

    // 贴纸特效添加一个 （支持添加多个 Demo只演示一个）
    private TuSDKMediaStickerEffectData mStickerEffectData;

    // 音效配音 （支持添加多个，Demo只演示一个）
    private TuSDKMediaAudioEffectData mAudioEffectData;

    // 贴纸+音效 （只支持添加一个）
    private TuSDKMediaStickerAudioEffectData mStickerAudioEffectData;

    // 场景特效 TuSDKMediaSceneEffectData （支持添加多个）
    private List<SceneEffectModel> mSceneEffectDataList = new ArrayList<>();

    // 魔法特效 TuSDKMediaParticleEffectData （支持添加多个）
    private List<MagicEffectModel> mMagicEffectDataList = new ArrayList<>();

    private MediaEffectsManager(){}

    /**
     * 获取 MediaEffectsManager
     * @return
     */
    public static MediaEffectsManager getMediaEffectManager()
    {
        return MEDIA_EFFECT_MANAGER;
    }

    /**
     * 获取设置的音效
     * @return
     */
    public TuSDKMediaAudioEffectData getAudioEffectData()
    {
        return mAudioEffectData;
    }

    /**
     * 获取设置的贴纸+音效（MV）
     * @return
     */
    public TuSDKMediaStickerAudioEffectData getStickerAudioEffectData() {
        return mStickerAudioEffectData;
    }

    /**
     * 设置贴纸
     *
     * @param stickerEffectData
     */
    public void setStickerEffectData(TuSDKMediaStickerEffectData stickerEffectData) {
        this.mStickerEffectData = stickerEffectData;

        this.mAudioEffectData = null;
        this.mStickerAudioEffectData = null;

    }

    /**
     *
     * @param audioEffectData
     */
    public void setAudioEffectData(TuSDKMediaAudioEffectData audioEffectData)
    {
        this.mAudioEffectData = audioEffectData;

        // 不能与 TuSDKMediaStickerAudioEffectData 同时使用
        this.mStickerAudioEffectData  = null;

        // 不显示贴纸
        this.mStickerEffectData = null;
    }

    /**
     * 设置贴纸+配音特效
     * @param stickerAudioEffectData
     */
    public void setStickerAudioEffectData(TuSDKMediaStickerAudioEffectData stickerAudioEffectData)
    {
        this.mStickerAudioEffectData = stickerAudioEffectData;

        // 不能与 TuSDKMediaAudioEffectData 同时使用
        this.mAudioEffectData = null;
        // 不显示
        this.mStickerEffectData = null;
    }

    /**
     * 设置场景特效
     *
     * @param sceneEffectDataList
     */
    public void setSceneEffectDataList(List<SceneEffectModel> sceneEffectDataList) {

        this.mMagicEffectDataList = null;

        this.mSceneEffectDataList = sceneEffectDataList;

    }

    /**
     * 设置魔法特效
     *
     * @param magicEffectDataList
     */
    public void setMagicEffectDataList(List<MagicEffectModel> magicEffectDataList) {

        this.mSceneEffectDataList = null;

        this.mMagicEffectDataList = magicEffectDataList;
    }

    /**
     * 获取设置的特效列表
     *
     * @return
     */
    public List<TuSDKMediaEffectData> getAllMediaEffectList()
    {
         List<TuSDKMediaEffectData> effectDataList = new ArrayList<TuSDKMediaEffectData>();

         if (mSceneEffectDataList != null)
             effectDataList.addAll(mSceneEffectDataList);

        if (mMagicEffectDataList != null)
            effectDataList.addAll(mMagicEffectDataList);

        if (mStickerAudioEffectData != null)
            effectDataList.add(mStickerAudioEffectData);

        if (mAudioEffectData != null)
            effectDataList.add(mAudioEffectData);

        if (mStickerEffectData != null)
            effectDataList.add(mStickerEffectData);

        return effectDataList;
    }

    public void clearMediaEffectList()
    {
        this.mSceneEffectDataList = null;
        this.mMagicEffectDataList = null;
        this.mStickerAudioEffectData = null;
        this.mStickerEffectData = null;
        this.mAudioEffectData = null;
    }

}
