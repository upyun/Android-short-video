package com.upyun.shortvideo.editor.component;

import android.view.View;

import org.lasque.tusdk.core.seles.sources.TuSdkEditorEffector;
import org.lasque.tusdk.impl.components.widget.sticker.StickerTextItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerData;
import org.lasque.tusdk.video.editor.TuSdkMediaTextEffectData;

import com.upyun.shortvideo.views.editor.playview.rangeselect.TuSdkMovieColorRectView;
import com.upyun.shortvideo.views.editor.TuSdkMovieScrollPlayLineView;

import java.util.ArrayList;
import java.util.List;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/12/19 10:14
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 文字特效备份管理
 */
public class EditorTextBackups {
    /** 文字贴纸父视图 **/
    private StickerView mStickerView;
    /** 文字贴纸操作视图 **/
    private EditorTextComponent.EditorTextBottomView mBottomView;
    /** LineView **/
    private TuSdkMovieScrollPlayLineView mLineView;
    private TuSdkEditorEffector mEditorEffector;

    /** 上一次应用的特效数据 **/
//    private List<StickerTextItemView> mMemeoList = new ArrayList<>();
    /** 当前正在编辑的数据 **/
//    private List<StickerTextItemView> mEditList = new ArrayList<>();

    /** 上次应用的特效数据 **/
    private List<TextBackupEntity> mMemeoBackupEntityList = new ArrayList<>();
    /** 备份实体类列表 **/
    private List<TextBackupEntity> mBackupEntityList = new ArrayList<>();

    public EditorTextBackups(StickerView mStickerView, EditorTextComponent.EditorTextBottomView mBottomView, TuSdkEditorEffector editorEffector) {
        this.mStickerView = mStickerView;
        this.mBottomView = mBottomView;
        this.mEditorEffector = editorEffector;
    }

    /** 设置LineView **/
    public void setLineView(TuSdkMovieScrollPlayLineView lineView){
        this.mLineView = lineView;
    }

    /** 同步组件Attach */
    public void onComponentAttach() {
        mBackupEntityList.addAll(mMemeoBackupEntityList);

        for (TextBackupEntity entity : mMemeoBackupEntityList) {
            if(entity.textItemView != null) {
                entity.textItemView.setVisibility(View.VISIBLE);
                mStickerView.addView(entity.textItemView);
                mStickerView.addSticker(entity.textItemView);
                entity.textItemView.setTranslation(entity.textItemView.getTranslation().x, entity.textItemView.getTranslation().y);
            }

            if(entity.colorRectView != null &&  mLineView != null){
                entity.colorRectView.setVisibility(View.VISIBLE);
            }
        }

    }

    /** 同步组件Detach */
    public void onComponentDetach() {
    }

//    /** 添加一个文字贴纸 **/
//    public void addStickerText(StickerTextItemView itemView) {
//        mEditList.add(itemView);
//    }
//
//    /** 移除一个文字贴纸 **/
//    public void removeStickerText(StickerTextItemView itemView) {
//        mEditList.remove(itemView);
//        mStickerView.removeView(itemView);
//    }


    /** 添加一个备份实体 **/
    public void addBackupEntity(TextBackupEntity entity){
        mBackupEntityList.add(entity);
    }

    public TextBackupEntity findTextBackupEntity( StickerTextItemView stickerItemView){
        for (TextBackupEntity entity : mBackupEntityList) {
            if(entity.textItemView == stickerItemView){
                return entity;
            }
        }
        return null;
    }

    /** 通过贴纸移除一个备份实体 **/
    public void removeBackupEntityWithSticker(StickerTextItemView textItemView){
        if(textItemView == null)return;
        int index = -1;
        for (int i = 0; i < mBackupEntityList.size(); i++) {
            if(textItemView.equals(mBackupEntityList.get(i).textItemView)){
                index = i;
            }
        }
        if(index == -1)return;

        TextBackupEntity entity = mBackupEntityList.get(index);
        if(entity.textItemView != null) mStickerView.removeView(entity.textItemView);
        if(entity.colorRectView != null && mLineView != null)mLineView.deletedColorRect(entity.colorRectView);
        if(entity.textMediaEffectData != null && mEditorEffector != null) mEditorEffector.removeMediaEffectData(entity.textMediaEffectData);

        mBackupEntityList.remove(index);
    }

    /** 移除一个备份实体 **/
    public void removeBackupEntity(TextBackupEntity entity){
       boolean result =  mBackupEntityList.remove(entity);

       if(!result)return;
       if(entity.textItemView != null) mStickerView.removeView(entity.textItemView);
       if(entity.colorRectView != null && mLineView != null)mLineView.deletedColorRect(entity.colorRectView);
    }

    /** 应用特效 **/
    public void onApplyEffect() {
//        mMemeoList.clear();
//        mMemeoList.addAll(mEditList);
//        mEditList.clear();

        mMemeoBackupEntityList.clear();
        mMemeoBackupEntityList.addAll(mBackupEntityList);
        mBackupEntityList.clear();
    }

    /** 返回事件 **/
    public void onBackEffect() {
        //移除所有备忘数据

        mStickerView.cancelAllStickerSelected();
        mStickerView.getStickerItems().clear();
        mStickerView.removeAllViews();

        for (TextBackupEntity entity : mBackupEntityList) {
            if(entity.colorRectView != null){
                entity.colorRectView.setVisibility(View.GONE);
            }
        }
        mBackupEntityList.clear();


        for (TextBackupEntity entity : mMemeoBackupEntityList) {
            if(entity.textItemView != null){
                mStickerView.addSticker(entity.textItemView);
                entity.textItemView.setTranslation(entity.textItemView.getTranslation().x, entity.textItemView.getTranslation().y);
            }

            if(entity.colorRectView != null){
                entity.colorRectView.setVisibility(View.VISIBLE);
            }
        }


        //应用数据
        if(mMemeoBackupEntityList.size() > 0 && mEditorEffector != null){
            for (TextBackupEntity entity : mMemeoBackupEntityList) {
                if(entity.textMediaEffectData != null)
                mEditorEffector.addMediaEffectData(entity.textMediaEffectData);
            }
        }
    }

    /** 创建备份实体类 **/
    public static TextBackupEntity createBackUpEntity(StickerData stickerData, StickerTextItemView textItemView, TuSdkMovieColorRectView colorRectView){
        TextBackupEntity entity = new TextBackupEntity();
        entity.stickerData = stickerData;
        entity.textItemView = textItemView;
        entity.colorRectView = colorRectView;
        return entity;
    }

    /** 寻找颜色区间 **/
    public TuSdkMovieColorRectView findColorRect(StickerData stickerData) {
        if(mBackupEntityList.size() == 0 || stickerData == null)return null;
        for (TextBackupEntity entity : mBackupEntityList) {
           if(stickerData.equals(entity.stickerData)){
               return entity.colorRectView;
           }
        }
        return null;
    }

    /** 寻找贴纸数据 **/
    public StickerData findStickerData(TuSdkMovieColorRectView rectView) {
        if(mBackupEntityList.size() == 0 || rectView == null)return null;
        for (TextBackupEntity entity : mBackupEntityList) {
            if(rectView.equals(entity.colorRectView)){
                return entity.stickerData;
            }
        }
        return null;
    }

    /** 寻找贴纸视图 **/
    public StickerTextItemView findStickerItem(TuSdkMovieColorRectView rectView) {
        if(mBackupEntityList.size() == 0 || rectView == null)return null;
        for (TextBackupEntity entity : mBackupEntityList) {
            if(rectView.equals(entity.colorRectView)){
                return entity.textItemView;
            }
        }
        return null;
    }

    /** 实体备份包装类 **/
    public static class TextBackupEntity{
        StickerData stickerData;
        StickerTextItemView textItemView;
        TuSdkMovieColorRectView colorRectView;
        TuSdkMediaTextEffectData textMediaEffectData;
    }

}
