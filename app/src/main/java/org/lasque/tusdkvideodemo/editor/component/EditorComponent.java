package org.lasque.tusdkvideodemo.editor.component;

import android.graphics.Bitmap;
import android.view.View;

import org.lasque.tusdk.core.seles.sources.TuSdkEditorAudioMixer;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorEffector;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdkvideodemo.editor.MovieEditorController;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/25 11:39
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 编辑组件父类
 * @since V3.0
 */
public abstract class EditorComponent {
    private static final String TAG = "EditorComponent";

    /**
     * 组件枚举
     *
     * @since V3.0.0
     */
    public enum EditorComponentType {
        //主页
        Home,
        //MV
        MV,
        //滤镜
        Filter,
        //音乐
        Music,
        //文字
        Text,
        //特效
        Effect,
        //贴纸
        Sticker,
        //裁剪
        Trim,
        //转场特效
        TransitionsEffect,
        //动态贴纸
        DynamicSticker
    }

    // 编辑控制器
    private MovieEditorController mEditorController;
    // 当前组件枚举
    protected EditorComponentType mComponentType;

    /** 备忘上次应用的效果以及状态 **/

    // 当前特效数据的备份
    protected TuSdkMediaEffectData mMementoEffectData;
    // 记录特效的下标备份
    protected int mMementoEffectIndex;
    // 当前选择的下标
    protected int mSelectIndex = 0;
    // 当前选择的特效数据
    protected TuSdkMediaEffectData mSelectEffectData;
    // 备忘其他音轨音量
    protected float mMementoOtherVolume = 0.5f;
    protected boolean isAnimationStaring = false;


    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorComponent(MovieEditorController editorController) {
        this.mEditorController = editorController;
    }

    /**
     * 获取编辑器
     * @return
     */
    protected TuSdkMovieEditor getMovieEditor(){
        if(mEditorController == null){
            TLog.e("%s EditorComponent is not init",TAG);
            return null;
        }
        return mEditorController.getMovieEditor();
    }

    /**
     * 获取编辑播放器
     * @return
     */
    protected TuSdkEditorPlayer getEditorPlayer(){
        return getMovieEditor().getEditorPlayer();
    }

    /**
     * 获取编辑特效器
     * @return
     */
    protected TuSdkEditorEffector getEditorEffector(){
        return getMovieEditor().getEditorEffector();
    }

    /**
     * 获取混音器
     * @return
     */
    protected TuSdkEditorAudioMixer getEditorAudioMixer(){
        return getMovieEditor().getEditorMixer();
    }


    /**
     * 装载当前组件
     *
     * @since V3.0.0
     */
    public abstract void attach();

    /**
     * 卸载组件
     *
     * @since V3.0.0
     */
    public abstract void detach();

    /**
     * 获取头部的View
     *
     * @return View header的实例
     */
    public abstract View getHeaderView();

    /**
     * 获取底部View
     *
     * @return View bottom的实例
     */
    public abstract View getBottomView();

    /**
     * 获取编辑控制器
     *
     * @return
     */
    protected MovieEditorController getEditorController() {
        return mEditorController;
    }

    /**
     * 获取当前组件枚举
     *
     * @return
     */
    public EditorComponentType getComponentEnum() {
        return mComponentType;
    }

    /** 添加一个封面视图
     * (如果当前组件没有LineView 则不用实现该方法)
     * @param bitmap 一个封面视图
     **/
    public abstract void addCoverBitmap(Bitmap bitmap);
    /** 动画开始 **/
    public void onAnimationStart(){
        isAnimationStaring = true;
    }
    /** 动画结束 **/
    public void onAnimationEnd(){
        isAnimationStaring = false;
    }

    /*---------------------------- 同步Activity的生命周期 ---------------------*/

    /**
     * 同步Activity的OnCreate
     *
     * @since V3.0.0
     */
    public void onCreate() {
    }

    /**
     * 同步Activity的onStart
     *
     * @since V3.0.0
     */
    public void onStart() {
    }

    /**
     * 同步Activity的onResume
     *
     * @since V3.0.0
     */
    public void onResume() {
    }

    /**
     * 同步Activity的onPause
     *
     * @since V3.0.0
     */
    public void onPause() {
    }

    /**
     * 同步Activity的onStop
     *
     * @since V3.0.0
     */
    public void onStop() {
    }

    /**
     * 同步Activity的onDestroy
     *
     * @since V3.0.0
     */
    public void onDestroy() {
    }

}
