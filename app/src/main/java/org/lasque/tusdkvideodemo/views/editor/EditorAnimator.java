package org.lasque.tusdkvideodemo.views.editor;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdkvideodemo.editor.MovieEditorController;
import org.lasque.tusdkvideodemo.editor.component.EditorComponent;
import org.lasque.tusdkvideodemo.views.VideoContent;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/26 15:02
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 视频编辑动画管理类
 */
public class EditorAnimator {
    private static final String TAG = "EditorAnimator";

    public interface OnAnimationEndListener {
        void onShowAnimationStartListener();
        void onShowAnimationEndListener();
        void onHideAnimationStartListener();
        void onHideAnimationEndListener();
    }

    //动画持续时间
    private static final int DURATION = 250;
    //播放器的content
    private VideoContent mVideoContent;
    private MovieEditorController mEditorController;
    private EditorComponent.EditorComponentType mComponentEnum;
    private OnAnimationEndListener animationEndListener;

    private int mVideoContentHeight;

    //底部组件展示动画
    private ObjectAnimator mBottomShowAnimator = new ObjectAnimator();
    private ObjectAnimator mBottomHideAnimator = new ObjectAnimator();

    public EditorAnimator(MovieEditorController editorController, VideoContent videoContent) {
        this.mVideoContent = videoContent;
        this.mEditorController = editorController;
    }

    public void setAnimationEndListener(OnAnimationEndListener animationEndListener) {
        this.animationEndListener = animationEndListener;
    }

    /**
     * 显示Component
     */
    public void showComponent() {
        if (mEditorController == null) {
            TLog.e("%s EditorController is null !!!", TAG);
            return;
        }
        this.mVideoContentHeight = mVideoContent.getHeight();
        final int bottomHeight = getBottomHeight();
        if (mBottomHideAnimator.isRunning()) mBottomHideAnimator.end();
        mBottomShowAnimator.setIntValues(bottomHeight, 0);
        mBottomShowAnimator.setDuration(DURATION);
        mBottomShowAnimator.addListener(mShowAnimatorListener);
        mBottomShowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mEditorController.getBottomView().setTranslationY(value);
                if (mEditorController.getCurrentComponent().getComponentEnum() != EditorComponent.EditorComponentType.Home)
                    mVideoContent.setHeight(mVideoContentHeight - (bottomHeight - value));

            }
        });
        mBottomShowAnimator.start();
    }


    /**
     * 隐藏Component
     */
    public void hideComponent() {
        if (mEditorController == null) {
            TLog.e("%s EditorController is null !!!", TAG);
            return;
        }
        this.mVideoContentHeight = mVideoContent.getHeight();
        final int bottomHeight = getBottomHeight();
        if (mBottomShowAnimator.isRunning()) mBottomShowAnimator.end();
        mBottomHideAnimator.setIntValues(0, bottomHeight);
        mBottomHideAnimator.setDuration(DURATION);
        mBottomHideAnimator.addListener(mHideAnimatorListener);
        mBottomHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mEditorController.getBottomView().setTranslationY(value);
                mVideoContent.setHeight(mVideoContentHeight + value);
            }
        });
        mBottomHideAnimator.start();
    }


    private int getBottomHeight() {
        if (mEditorController == null || mEditorController.getCurrentComponent() == null || mEditorController.getCurrentComponent().getBottomView() == null)
            return 0;
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mEditorController.getCurrentComponent().getBottomView().measure(w, h);
        return mEditorController.getCurrentComponent().getBottomView().getMeasuredHeight();
    }

    //动画切换
    public void animatorSwitchComponent(EditorComponent.EditorComponentType componentEnum) {
        mComponentEnum = componentEnum;
        hideComponent();
    }

    //进入动画监听
    private Animator.AnimatorListener mShowAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            if(animationEndListener != null)animationEndListener.onShowAnimationStartListener();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mBottomShowAnimator.removeAllListeners();
            if(animationEndListener != null)animationEndListener.onShowAnimationEndListener();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };


    //隐藏动画监听
    private Animator.AnimatorListener mHideAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            if(animationEndListener != null)animationEndListener.onHideAnimationStartListener();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mBottomHideAnimator.removeAllListeners();
            mEditorController.switchComponent(mComponentEnum);
            if(animationEndListener != null)animationEndListener.onHideAnimationEndListener();
            showComponent();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

}
