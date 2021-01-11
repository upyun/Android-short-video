package org.lasque.tusdkvideodemo.editor.component;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;

import org.lasque.tusdk.core.seles.sources.TuSdkEditorEffector;
import org.lasque.tusdk.core.utils.ColorUtils;
import org.lasque.tusdk.impl.components.widget.sticker.StickerTextItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerData;
import org.lasque.tusdk.video.editor.TuSdkMediaTextEffectData;
import org.lasque.tusdkvideodemo.editor.component.helper.EditorTextAndStickerRankHelper;
import org.lasque.tusdkvideodemo.views.editor.TuSdkMovieScrollPlayLineView;
import org.lasque.tusdkvideodemo.views.editor.playview.rangeselect.TuSdkMovieColorRectView;

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
    /** LineView **/
    private TuSdkMovieScrollPlayLineView mLineView;
    private TuSdkEditorEffector mEditorEffector;

    /** 上次应用的特效数据 **/
//    private List<TextBackupEntity> mMemeoBackupEntityList = new ArrayList<>();
    /** 备份实体类列表 **/
//    private List<TextBackupEntity> mBackupEntityList = new ArrayList<>();

    private EditorTextAndStickerRankHelper mRankHelper;

    public EditorTextBackups(StickerView mStickerView, EditorTextComponent.EditorTextBottomView mBottomView, TuSdkEditorEffector editorEffector,EditorTextAndStickerRankHelper rankHelper) {
        this.mStickerView = mStickerView;
        this.mEditorEffector = editorEffector;
        this.mRankHelper = rankHelper;
    }

    /** 设置LineView **/
    public void setLineView(TuSdkMovieScrollPlayLineView lineView) {
        this.mLineView = lineView;
    }

    /** 同步组件Attach */
    public void onComponentAttach() {
        mRankHelper.getBackupLinkedList().clear();
//        mRankHelper.getBackupLinkedList().addAll(mRankHelper.getMemeoBackupLinkedList());
        for (Object item: mRankHelper.getMemeoBackupLinkedList()) {
            if(item instanceof TextBackupEntity) {
                TextBackupEntity entity = (TextBackupEntity) item;
                mRankHelper.getBackupLinkedList().add(entity.clone());
                if (entity.textItemView != null) {
                    if (entity.textMediaEffectData.getAtTimeRange().getStartTime() == 0){
                        entity.textItemView.setVisibility(View.VISIBLE);
                    }
                    if(entity.textItemView.getParent() != null){
                        ((ViewGroup)entity.textItemView.getParent()).removeAllViews();
                    }

                    mStickerView.addView(entity.textItemView);
                    mStickerView.addSticker(entity.textItemView);
                    entity.textItemView.reRotate();
                    entity.textItemView.setTranslation(entity.textItemView.getTranslation().x, entity.textItemView.getTranslation().y);

                    Typeface typeface = Typeface.DEFAULT;
                    if (entity.mTextFont == 2) typeface = Typeface.SERIF;
                    entity.textItemView.setTextFont(typeface);

                    entity.textItemView.setTextStrokeWidth((int) entity.strokeWidth);
                    entity.textItemView.setTextStrokeColor(entity.strokeColor);
                    entity.textItemView.onSelectedColorChanged(0, entity.color);
                    entity.textItemView.setStickerViewType(StickerView.StickerType.Text);
                    entity.textItemView.setStickerType(StickerView.StickerType.Text);

                    //背景颜色
                    entity.textItemView.onSelectedColorChanged(1,ColorUtils.alphaEvaluator(entity.bgAlpha, entity.bgColor));

                    //字间距
                    entity.textItemView.setLetterSpacing(0.07f * entity.wordWidth);
                    //行间距
                    if (entity.rowWidth != 0)
                    entity.textItemView.setLineSpacing(0, 0.5f + entity.rowWidth);

                    int style = entity.isBold ? Typeface.BOLD : Typeface.NORMAL;
                    if (entity.isBold && entity.isItalics) style = Typeface.BOLD_ITALIC;
                    else if (!entity.isBold && entity.isItalics) style = Typeface.ITALIC;
                    entity.textItemView.getTextView().setTypeface(Typeface.defaultFromStyle(style));
                    entity.textItemView.getTextView().setUnderlineText(entity.isUnderline);
                    entity.textItemView.getTextView().setAlpha(entity.alpha);

                }

                if (entity.colorRectView != null && mLineView != null) {
                    entity.colorRectView.setVisibility(View.VISIBLE);
                }
            }else if(item instanceof EditorStickerImageBackups.StickerImageBackupEntity){
                EditorStickerImageBackups.StickerImageBackupEntity entit = (EditorStickerImageBackups.StickerImageBackupEntity) item;
                mRankHelper.getBackupLinkedList().add(entit);
                if (entit.stickerImageItemView != null) {
                    if (entit.stickerImageMediaEffectData.getAtTimeRange().getStartTime() == 0){
                        entit.stickerImageItemView.setVisibility(View.VISIBLE);
                    }
                    entit.stickerImageItemView.setStickerViewType(StickerView.StickerType.Text);
                    entit.stickerImageItemView.setStickerType(StickerView.StickerType.Image);
                    mStickerView.addView(entit.stickerImageItemView, -1);
                    mStickerView.addSticker(entit.stickerImageItemView);
                    entit.stickerImageItemView.setTranslation(entit.stickerImageItemView.getTranslation().x, entit.stickerImageItemView.getTranslation().y);
                }

                if (entit.colorRectView != null && mLineView != null) {
                    entit.colorRectView.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    /** 同步组件Detach */
    public void onComponentDetach() {
    }

    /** 添加一个备份实体 **/
    public void addBackupEntity(TextBackupEntity entity) {
        if(mRankHelper.getBackupLinkedList().contains(entity))return;
        mRankHelper.getBackupLinkedList().add(entity);
    }

    public TextBackupEntity findTextBackupEntity(StickerTextItemView stickerItemView) {
        for (Object item : mRankHelper.getBackupLinkedList()) {
            if(item instanceof TextBackupEntity) {
                TextBackupEntity entity = (TextBackupEntity) item;
                if (entity.textItemView == stickerItemView) {
                    return entity;
                }
            }
        }
        return null;
    }


    public TextBackupEntity findTextBackupEntityToMemo(StickerTextItemView stickerItemView) {
        for (Object item : mRankHelper.getMemeoBackupLinkedList()) {
            if(item instanceof TextBackupEntity) {
                TextBackupEntity entity = (TextBackupEntity) item;
                if (entity.textItemView == stickerItemView) {
                    return entity;
                }
            }
        }
        return null;
    }

    /** 通过贴纸移除一个备份实体 **/
    public void removeBackupEntityWithSticker(StickerTextItemView textItemView) {
        if (textItemView == null) return;
        int index = -1;
        for (int i = 0; i < mRankHelper.getBackupLinkedList().size(); i++) {
           if(mRankHelper.getBackupLinkedList().get(i) instanceof TextBackupEntity) {
               if (textItemView.equals(((TextBackupEntity) mRankHelper.getBackupLinkedList().get(i)).textItemView)) {
                   index = i;
               }
           }
        }
        if (index == -1) return;

        TextBackupEntity entity = (TextBackupEntity) mRankHelper.getBackupLinkedList().get(index);
        if (entity.textItemView != null) mStickerView.removeView(entity.textItemView);
        if (entity.colorRectView != null && mLineView != null)
            mLineView.deletedColorRect(entity.colorRectView);
        if (entity.textMediaEffectData != null && mEditorEffector != null)
            mEditorEffector.removeMediaEffectData(entity.textMediaEffectData);

        mRankHelper.getBackupLinkedList().remove(index);
    }

//    /** 移除一个备份实体 **/
//    public void removeBackupEntity(TextBackupEntity entity) {
//        boolean result = mRankHelper.getBackupLinkedList().remove(entity);
//
//        if (!result) return;
//        if (entity.textItemView != null) mStickerView.removeView(entity.textItemView);
//        if (entity.colorRectView != null && mLineView != null)
//            mLineView.deletedColorRect(entity.colorRectView);
//    }

    /** 应用特效 **/
    public void onApplyEffect() {
//        mMemeoList.clear();
//        mMemeoList.addAll(mEditList);
//        mEditList.clear();

//        mMemeoBackupEntityList.clear();
//
//
//        for (TextBackupEntity entity : mBackupEntityList) {
//            mMemeoBackupEntityList.add(entity.clone());
//        }
//        mBackupEntityList.clear();

        mRankHelper.getMemeoBackupLinkedList().clear();
        mRankHelper.getMemeoBackupLinkedList().addAll(mRankHelper.getBackupLinkedList());

    }

    /** 返回事件 **/
    public void onBackEffect() {
        //移除所有备忘数据
        mStickerView.cancelAllStickerSelected();
        mStickerView.getStickerItems().clear();
        mStickerView.removeAllViews();

        for (Object item : mRankHelper.getBackupLinkedList()) {
            if(item instanceof TextBackupEntity) {
                TextBackupEntity entity = (TextBackupEntity) item;
                if (entity.colorRectView != null) {
                    entity.colorRectView.setVisibility(View.GONE);
                }
            }
        }
        mRankHelper.getBackupLinkedList().clear();

        //应用数据
        if (mRankHelper.getMemeoBackupLinkedList().size() > 0 && mEditorEffector != null) {
            for (Object item : mRankHelper.getMemeoBackupLinkedList()) {
                if(item instanceof TextBackupEntity){
                    TextBackupEntity entity = (TextBackupEntity) item;
                    mEditorEffector.addMediaEffectData(entity.textMediaEffectData);
                }else if(item instanceof EditorStickerImageBackups.StickerImageBackupEntity){
                    EditorStickerImageBackups.StickerImageBackupEntity entity = (EditorStickerImageBackups.StickerImageBackupEntity) item;
                    entity.stickerImageMediaEffectData.destory();
                    mEditorEffector.addMediaEffectData(entity.stickerImageMediaEffectData);
                }

            }
        }
    }

    /** 创建备份实体类 **/
    public static TextBackupEntity createBackUpEntity(StickerData stickerData, StickerTextItemView textItemView, TuSdkMovieColorRectView colorRectView) {
        return createBackUpEntity(stickerData, textItemView, colorRectView,1);
    }

    public static TextBackupEntity createBackUpEntity(StickerData stickerData, StickerTextItemView textItemView, TuSdkMovieColorRectView colorRectView,int textFontIndex){
        TextBackupEntity entity = new TextBackupEntity();
        entity.stickerData = stickerData;
        entity.textItemView = textItemView;
        entity.colorRectView = colorRectView;
//        entity.rowWidth = textItemView.getTextView().getLineSpacingMultiplier();
        entity.mTextFont = textFontIndex;
        return entity;
    }

    /** 寻找颜色区间 **/
    public TuSdkMovieColorRectView findColorRect(StickerData stickerData) {
        if (mRankHelper.getBackupLinkedList().size() == 0 || stickerData == null) return null;
        for (Object item : mRankHelper.getBackupLinkedList()) {
            if(item instanceof TextBackupEntity) {
                TextBackupEntity entity = (TextBackupEntity) item;
                if (stickerData.equals(entity.stickerData)) {
                    return entity.colorRectView;
                }
            }
        }
        return null;
    }

    /** 寻找贴纸数据 **/
    public StickerData findStickerData(TuSdkMovieColorRectView rectView) {
        if (mRankHelper.getBackupLinkedList().size() == 0 || rectView == null) return null;
        for (Object item : mRankHelper.getBackupLinkedList()) {
            if(item instanceof TextBackupEntity) {
                TextBackupEntity entity = (TextBackupEntity) item;
                if (rectView.equals(entity.colorRectView)) {
                    return entity.stickerData;
                }
            }
        }
        return null;
    }

    /** 寻找贴纸视图 **/
    public StickerTextItemView findStickerItem(TuSdkMovieColorRectView rectView) {
        if (mRankHelper.getBackupLinkedList().size() == 0|| rectView == null) return null;
        for (Object item : mRankHelper.getBackupLinkedList()) {
            if(item instanceof TextBackupEntity) {
                TextBackupEntity entity = (TextBackupEntity) item;
                if (rectView.equals(entity.colorRectView)) {
                    return entity.textItemView;
                }
            }
        }
        return null;
    }

    public TextBackupEntity findTextBackupEntity(StickerData stickerData) {
        if (mRankHelper.getBackupLinkedList().size() == 0 || stickerData == null) return null;
        for (Object item : mRankHelper.getBackupLinkedList()) {
            if(item instanceof TextBackupEntity) {
                TextBackupEntity entity = (TextBackupEntity) item;
                if (stickerData.equals(entity.stickerData)) {
                    return entity;
                }
            }
        }
        return null;
    }

    /** 实体备份包装类 **/
    public static class TextBackupEntity {
        StickerData stickerData;
        StickerTextItemView textItemView;
        TuSdkMovieColorRectView colorRectView;
        TuSdkMediaTextEffectData textMediaEffectData;
        /** 不透明度 **/
        public float alpha = 1f;
        /** 描边宽度 **/
        public float strokeWidth = 0f;
        /** 描边颜色 **/
        public int strokeColor;
        /** 背景不透明度 **/
        public float bgAlpha = 0.5f;
        /** 背景颜色 **/
        public int bgColor = 0;
        /** 行间距 **/
        public float rowWidth = 0f;
        /** 字间距 **/
        public float wordWidth = 0f;
        /** 是否加粗 **/
        public boolean isBold = false;
        /** 下划线 **/
        public boolean isUnderline = false;
        /** 斜体 **/
        public boolean isItalics = false;
        /** 字号 **/
        public float mFontSize = 0f;
        /** 字体 **/
        public int mTextFont ;
        /** 颜色 **/
        public int color = Color.WHITE;
        /** 第一次点进不透明度 **/
        public boolean firstSetAlpha = true;
        /** 第一次点进背景色透明度 **/
        public boolean firstSetBackGroudAlpha = true;
        /** 是否需要反转 **/
        public boolean needReverse = true;
        @Override
        public TextBackupEntity clone(){
            TextBackupEntity entity = new TextBackupEntity();
            entity.stickerData = stickerData;
            entity.textItemView = textItemView;
            entity.colorRectView = colorRectView;
            entity.textMediaEffectData = textMediaEffectData;
            entity.alpha = alpha;
            entity.strokeWidth = strokeWidth;
            entity.bgAlpha = bgAlpha;
            entity.rowWidth = rowWidth;
            entity.isBold = isBold;
            entity.isUnderline = isUnderline;
            entity.isItalics = isItalics;
            entity.mTextFont = mTextFont;
            entity.color = color;
            entity.bgColor = bgColor;
            entity.strokeColor = strokeColor;
            entity.firstSetAlpha = firstSetAlpha;
            entity.firstSetBackGroudAlpha = firstSetBackGroudAlpha;
            entity.needReverse = needReverse;
            return entity;
        }
    }

}
